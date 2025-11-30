/*
 * EmptinessChecker.java
 * ---------------------
 * Checks emptiness of DFA language - "yes" iff no string leads to q0 or q2, "no" and string otherwise.
 * Only "accepts" a string if it can reach q0 or q2 with no leftover chars.
 */

import java.io.*;
import java.util.*;

public class EmptinessChecker {
    public static Pair<Boolean, String> isEmpty(DFA dfa) {
        Queue<Pair<String, String>> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        queue.add(new Pair<>(dfa.startState, ""));
        visited.add(dfa.startState);

        while (!queue.isEmpty()) {
            Pair<String, String> curr = queue.poll();
            String state = curr.first;
            String path = curr.second;
            // Accept if in q0 or q2 at end of some input
            if (state.equals("q0") || state.equals("q2")) {
                return new Pair<>(false, path);
            }
            for (String sym : dfa.alphabet) {
                String next = dfa.transitions.get(state).get(sym);
                if (next != null && !visited.contains(next + "#" + (path + sym))) {
                    queue.add(new Pair<>(next, path + sym));
                    visited.add(next + "#" + (path + sym));
                }
            }
        }
        return new Pair<>(true, null);
    }

    // Helper Pair class
    public static class Pair<F, S> {
        public final F first;
        public final S second;
        public Pair(F f, S s) {
            first = f;
            second = s;
        }
    }

    // Reads DFA line from file
    public static String readLine(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        do { line = br.readLine(); } while (line != null && (line.startsWith("#") || line.trim().isEmpty()));
        String clean = line == null ? "" : line.trim();
        br.close();
        return clean;
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Usage: java EmptinessChecker <dfa_file>");
            return;
        }
        String line = readLine(args[0]);
        DFA dfa = DFA.fromFormat(line);
        Pair<Boolean, String> result = isEmpty(dfa);
        if (result.first) {
            System.out.println("yes");
        } else {
            System.out.println("no");
            System.out.println(result.second);
        }
    }
}