/**
 * DFA.java
 * A representation of a Deterministic Finite Automaton with alphabet {a, b}.
 */

import java.util.*;

public class DFA {
    public Set<String> states;
    public String startState;
    public Set<String> acceptStates;
    public Map<Pair, String> transitions;

    // Helper pair class for (state, symbol)
    public static class Pair {
        public String state;
        public char symbol;
        public Pair(String state, char symbol) {
            this.state = state;
            this.symbol = symbol;
        }
        // required for use as Map key
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Pair)) return false;
            Pair p = (Pair)o;
            return state.equals(p.state) && symbol == p.symbol;
        }
        @Override
        public int hashCode() {
            return Objects.hash(state, symbol);
        }
    }

    // Construct from string description as given in Problem 1 input
    public DFA(String description) {
        states = new HashSet<>();
        acceptStates = new HashSet<>();
        transitions = new HashMap<>();

        String[] parts = description.trim().split(",");
        if (parts.length < 1) throw new IllegalArgumentException("Invalid DFA description.");

        // Parse state list (e.g. q0fq1q2fq3)
        String stateDescr = parts[0];
        List<String> tmpStates = new ArrayList<>();
        int i = 0;
        while (i < stateDescr.length()) {
            if (stateDescr.charAt(i) == 'q') {
                int start = i;
                i++;
                while (i < stateDescr.length() && Character.isDigit(stateDescr.charAt(i))) i++;
                String name = stateDescr.substring(start, i);
                boolean isFinal = false;
                if (i < stateDescr.length() && stateDescr.charAt(i) == 'f') {
                    isFinal = true;
                    i++;
                }
                states.add(name);
                tmpStates.add(name);
                if (isFinal) acceptStates.add(name);
            } else {
                i++; // skip unexpected chars
            }
        }
        if (tmpStates.size() == 0) throw new IllegalArgumentException("No states found.");
        startState = tmpStates.get(0);

        // Parse transitions
        for (int j = 1; j < parts.length; j++) {
            String t = parts[j];
            // Example: q0aq1 means from q0, on a, go to q1
            if (!t.startsWith("q")) throw new IllegalArgumentException("Invalid transition: " + t);
            int si = 1;
            while (si < t.length() && Character.isDigit(t.charAt(si))) si++;
            String from = t.substring(0, si);
            char symbol = t.charAt(si);
            si++;
            if (t.charAt(si) != 'q') throw new IllegalArgumentException("Invalid transition: " + t);
            int sj = si + 1;
            while (sj < t.length() && Character.isDigit(t.charAt(sj))) sj++;
            String to = t.substring(si, sj);
            transitions.put(new Pair(from, symbol), to);
        }
    }

    // Run DFA, return accept/reject, and if accept, last state
    public boolean run(String input) {
        String current = startState;
        for (char c : input.toCharArray()) {
            Pair key = new Pair(current, c);
            if (!transitions.containsKey(key)) return false;
            current = transitions.get(key);
        }
        return acceptStates.contains(current);
    }

    // Given state and symbol, next state
    public String move(String state, char symbol) {
        return transitions.get(new Pair(state, symbol));
    }
}