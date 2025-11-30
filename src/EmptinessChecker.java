/*
 * EmptinessChecker.java
 * ---------------------
 * Checks if a DFA's language is empty, and finds a witness string if not.
 * Robust version: Handles missing transitions, so incomplete DFA specs will NOT throw an exception.
 *
 * Run as: java -cp src EmptinessChecker examples/ex1.txt
 *
 * Author(s): (put your names here)
 */

import java.io.*;
import java.util.*;

public class EmptinessChecker {
    /**
     * Uses BFS to determine if DFA can reach any accepting state; returns shortest accepting string as witness.
     * 
     * Now robust to missing transitions:
     * - If a state has no transitions for some symbol, treat as dead-end (do not enqueue).
     *
     * @param dfa DFA to check
     * @return Pair<Boolean, String>: true if language is empty, otherwise witness string
     */
    public static Pair<Boolean,String> isEmpty(DFA dfa) {
        Queue<Pair<String, String>> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        queue.add(new Pair<>(dfa.startState, ""));
        while (!queue.isEmpty()) {
            Pair<String, String> curr = queue.poll();
            String state = curr.first;
            String path = curr.second;
            // If we've hit an accept state, path is a witness string (shortest by BFS)
            if (dfa.acceptStates.contains(state)) {
                return new Pair<>(false, path);
            }
            // Only track STATE, not path in visited
            if (visited.contains(state)) continue;
            visited.add(state);
            // Try transitions for all symbols in the alphabet
            Map<String,String> stateTransitions = dfa.transitions.get(state);
            if (stateTransitions == null) continue; // If this state has no transitions defined, skip
            for (String sym : dfa.alphabet) {
                String next = stateTransitions.get(sym); // This could be null if transition is missing
                if (next != null)
                    queue.add(new Pair<>(next, path + sym));
                // If next is null then this symbol leads nowhere; just skip
            }
        }
        return new Pair<>(true, null); // No accepting state found: language is empty
    }

    // Helper Pair class for BFS queue
    public static class Pair<F, S> {
        public final F first;
        public final S second;
        public Pair(F f, S s) { first = f; second = s; }
    }

    // File reading utility (skip comments)
    public static String readLine(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        do { line = br.readLine(); } while(line != null && (line.startsWith("#") || line.trim().isEmpty())); // skip comments/empty
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
        Pair<Boolean,String> result = isEmpty(dfa);
        if (result.first) {
            System.out.println("yes");
        } else {
            System.out.println("no");
            System.out.println(result.second);
        }
    }
}