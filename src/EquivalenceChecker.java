/*
 * EquivalenceChecker.java
 * -----------------------
 * Implements Problem 2: Checks if two DFAs are equivalent, and gives witness if not.
 * 
 * Run as: java EquivalenceChecker examples/ex2.txt
 *
 * Author(s): (put your names here)
 */

import java.io.*;
import java.util.*;

public class EquivalenceChecker {
    /**
     * Builds symmetric difference DFA: accepts string accepted by one DFA but not the other.
     * 
     * States: pairs (qA, qB)
     * Accepting if exactly one is accepting (exclusive-or).
     * 
     * @param dfa1 DFA 1
     * @param dfa2 DFA 2
     * @return DFA: symmetric difference
     */
    public static DFA symmetricDifference(DFA dfa1, DFA dfa2) {
        Set<String> alphabet = new HashSet<>(dfa1.alphabet);
        alphabet.retainAll(dfa2.alphabet); // intersection of alphabets

        Set<String> states = new HashSet<>();
        Set<String> acceptStates = new HashSet<>();
        Map<String, Map<String, String>> transitions = new HashMap<>();

        // Product states: "qA_qB"
        for (String s1 : dfa1.states) {
            for (String s2 : dfa2.states) {
                String pair = s1 + "_" + s2;
                states.add(pair);
                transitions.put(pair, new HashMap<>());
                for (String a : alphabet) {
                    String t1 = dfa1.transitions.get(s1).get(a);
                    String t2 = dfa2.transitions.get(s2).get(a);
                    if (t1 != null && t2 != null)
                        transitions.get(pair).put(a, t1 + "_" + t2);
                }
                boolean s1Accept = dfa1.acceptStates.contains(s1);
                boolean s2Accept = dfa2.acceptStates.contains(s2);
                if (s1Accept ^ s2Accept) // XOR means accepted by one, not the other
                    acceptStates.add(pair);
            }
        }
        String start = dfa1.startState + "_" + dfa2.startState;
        return new DFA(states, alphabet, start, acceptStates, transitions);
    }

    // File reading utility
    public static List<String> readLines(String filename) throws IOException {
        List<String> result = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        while ( (line = br.readLine()) != null ) {
            if (line.startsWith("#") || line.trim().isEmpty()) continue;
            result.add(line.trim());
        }
        br.close();
        return result;
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

        DFA sd = symmetricDifference(dfa1, dfa2);
        // Reuse EmptinessChecker for symmetric difference!
        EmptinessChecker.Pair<Boolean,String> result = EmptinessChecker.isEmpty(sd);

        if (result.first) {
            System.out.println("yes");
        } else {
            System.out.println("no");
            System.out.println(result.second);
        }
    }
}