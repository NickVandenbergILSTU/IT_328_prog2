

import java.util.*;

/**
 * Parses a DFA from a single line of text in the format:
 *
 * Example (from project description):
 *   q0fq1q2fq3,q0aq1,q0bq0,q1aq2,q1bq3,q2aq2,q2bq3,q3aq0,q3bq2
 *
 * Meaning:
 *   - States: q0, q1, q2, q3
 *   - Accepting: q0, q2
 *   - Start state: the first state listed (here q0)
 *   - Transitions: "q0aq1" is q0 -a-> q1, etc.
 *
 * Alphabet is assumed to be {a, b}.
 */
public class DFAParser {

    /**
     * Parse a DFA from a single input line.
     *
     * @param line input line describing a DFA
     * @return DFA instance
     * @throws IllegalArgumentException if input line is malformed
     */
    public static DFA parseFromLine(String line) {
        line = line.trim();
        if (line.isEmpty()) {
            throw new IllegalArgumentException("Empty DFA description line.");
        }

        String[] parts = line.split(",");
        if (parts.length < 1) {
            throw new IllegalArgumentException("Malformed DFA description: " + line);
        }

        // First part lists states, with 'f' suffix indicating accepting states.
        String statePart = parts[0];

        Set<String> states = new LinkedHashSet<>();
        Set<String> acceptStates = new LinkedHashSet<>();

        // We parse by scanning for tokens that look like "q<number>" optionally followed by "f".
        int i = 0;
        String currentState = null;
        while (i < statePart.length()) {
            char c = statePart.charAt(i);
            if (c == 'q') {
                // Parse the state name "q" + digits
                int j = i + 1;
                while (j < statePart.length() && Character.isDigit(statePart.charAt(j))) {
                    j++;
                }
                currentState = statePart.substring(i, j);
                states.add(currentState);
                // Check if next char is 'f' (accepting)
                if (j < statePart.length() && statePart.charAt(j) == 'f') {
                    acceptStates.add(currentState);
                    j++;
                }
                i = j;
            } else {
                // Unexpected character
                throw new IllegalArgumentException("Unexpected character in states part: '" + c + "' in " + statePart);
            }
        }

        if (states.isEmpty()) {
            throw new IllegalArgumentException("No states found in DFA description: " + line);
        }

        String startState = states.iterator().next(); // first listed state is start

        Map<String, Map<Character, String>> transitionFunction = new HashMap<>();

        // Initialize transition map for all states (optional, but keeps things tidy)
        for (String s : states) {
            transitionFunction.put(s, new HashMap<>());
        }

        // Remaining parts are transitions like "q0aq1"
        for (int idx = 1; idx < parts.length; idx++) {
            String t = parts[idx].trim();
            if (t.isEmpty()) continue;
            // Format: <fromState><symbol><toState>
            // where <fromState> and <toState> are of form q + digits, and symbol is a or b.
            // We'll parse: find first 'q', then digits, then symbol, then 'q' + digits again.
            int p = 0;
            if (p >= t.length() || t.charAt(p) != 'q') {
                throw new IllegalArgumentException("Malformed transition (expected fromState starting with 'q'): " + t);
            }
            int p2 = p + 1;
            while (p2 < t.length() && Character.isDigit(t.charAt(p2))) {
                p2++;
            }
            String fromState = t.substring(p, p2);
            if (!states.contains(fromState)) {
                throw new IllegalArgumentException("Transition from unknown state: " + fromState);
            }
            if (p2 >= t.length()) {
                throw new IllegalArgumentException("Missing symbol in transition: " + t);
            }
            char symbol = t.charAt(p2);
            if (symbol != 'a' && symbol != 'b') {
                throw new IllegalArgumentException("Invalid symbol '" + symbol + "' in transition: " + t);
            }
            int p3 = p2 + 1;
            if (p3 >= t.length() || t.charAt(p3) != 'q') {
                throw new IllegalArgumentException("Malformed toState in transition: " + t);
            }
            int p4 = p3 + 1;
            while (p4 < t.length() && Character.isDigit(t.charAt(p4))) {
                p4++;
            }
            String toState = t.substring(p3, p4);
            if (!states.contains(toState)) {
                throw new IllegalArgumentException("Transition to unknown state: " + toState);
            }
            if (p4 != t.length()) {
                throw new IllegalArgumentException("Unexpected trailing characters in transition: " + t);
            }

            Map<Character, String> inner = transitionFunction.get(fromState);
            if (inner == null) {
                inner = new HashMap<>();
                transitionFunction.put(fromState, inner);
            }
            if (inner.containsKey(symbol)) {
                throw new IllegalArgumentException("Non-deterministic transition: " + fromState
                        + " already has a transition on " + symbol);
            }
            inner.put(symbol, toState);
        }

        return new DFA(states, startState, acceptStates, transitionFunction);
    }
}