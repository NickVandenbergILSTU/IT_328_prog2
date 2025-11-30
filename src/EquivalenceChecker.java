/*
 * EquivalenceChecker.java
 * -----------------------
 * Checks if two DFAs accept exactly the same set of strings (ending in q0 or q2).
 * If not, outputs a separating string accepted by one and not the other.
 */

import java.io.*;
import java.util.*;

public class EquivalenceChecker {
    public static EmptinessChecker.Pair<Boolean, String> findDifferingString(DFA dfa1, DFA dfa2) {
        // BFS product DFA: (state1, state2, path)
        Queue<Triple> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        queue.add(new Triple(dfa1.startState, dfa2.startState, ""));
        visited.add(dfa1.startState + "|" + dfa2.startState);

        while (!queue.isEmpty()) {
            Triple curr = queue.poll();
            String s1 = curr.s1;
            String s2 = curr.s2;
            String path = curr.path;
            boolean acc1 = (s1.equals("q0") || s1.equals("q2"));
            boolean acc2 = (s2.equals("q0") || s2.equals("q2"));
            // Accepted by one and not the other
            if (acc1 ^ acc2) {
                return new EmptinessChecker.Pair<>(false, path);
            }
            for (String sym : dfa1.alphabet) {
                String n1 = dfa1.transitions.get(s1).get(sym);
                String n2 = dfa2.transitions.get(s2).get(sym);
                if (n1 != null && n2 != null && !visited.contains(n1 + "|" + n2 + "#" + path + sym)) {
                    queue.add(new Triple(n1, n2, path + sym));
                    visited.add(n1 + "|" + n2 + "#" + path + sym);
                }
            }
        }
        return new EmptinessChecker.Pair<>(true, null);
    }

    public static class Triple {
        public String s1, s2, path;
        public Triple(String a, String b, String c) { s1=a; s2=b; path=c; }
    }

    // Read two lines from file
    public static List<String> readLines(String filename) throws IOException {
        List<String> list = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = br.readLine()) != null) {
            if (line.startsWith("#") || line.trim().isEmpty()) continue;
            list.add(line.trim());
        }
        br.close();
        return list;
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Usage: java EquivalenceChecker <dfa_file>");
            return;
        }
        List<String> lines = readLines(args[0]);
        if (lines.size() < 2) {
            System.out.println("Error: file should contain two DFA lines.");
            return;
        }
        DFA dfa1 = DFA.fromFormat(lines.get(0));
        DFA dfa2 = DFA.fromFormat(lines.get(1));
        EmptinessChecker.Pair<Boolean, String> result = findDifferingString(dfa1, dfa2);
        if (result.first) {
            System.out.println("yes");
        } else {
            System.out.println("no");
            System.out.println(result.second);
        }
    }
}