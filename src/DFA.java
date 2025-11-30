/*
 * DFA.java
 * --------
 * Defines DFA class and parsing logic according to assignment format:
 * Accepting states are states ending in 'f' (q0f means q0 is accepting), e.g.:
 * q0fq1q2fq3,... transitions ...
 *
 * Author(s): (put your names here)
 */

import java.util.*;

public class DFA {
    public Set<String> states;          // All state names
    public Set<String> alphabet;        // All allowed input symbols
    public String startState;           // Name of the start state
    public Set<String> acceptStates;    // Set of accepting state names
    public Map<String, Map<String, String>> transitions;  // state -> symbol -> next state

    /**
     * Constructs a DFA with given parameters.
     **/
    public DFA(Set<String> states, Set<String> alphabet, String startState,
            Set<String> acceptStates, Map<String, Map<String, String>> transitions) {
        this.states = states;
        this.alphabet = alphabet;
        this.startState = startState;
        this.acceptStates = acceptStates;
        this.transitions = transitions;
    }

    /**
     * Parses a DFA from the assignment format.
     * Accept states are denoted with 'f' suffix in state string (e.g. q2f).
     * States could be:   q0fq1q2fq3   (means states: q0, q1, q2, q3; accept: q0, q2)
     * Example input:     q0fq1q2fq3,q0aq1,q0bq0,q1aq2,q1bq3,q2aq2,q2bq3,q3aq0,q3bq2
     */
    public static DFA fromFormat(String line) {
        String[] pieces = line.trim().split(",");
        String statesPart = pieces[0];

        Set<String> states = new LinkedHashSet<>();
        Set<String> acceptStates = new LinkedHashSet<>();

        // Parse state section handling both regular and accepting states
        // Handles input like: q0fq1q2fq3
        for (int i = 0; i < statesPart.length();) {
            if (statesPart.charAt(i) != 'q') { i++; continue; }
            int nextQ = statesPart.indexOf('q', i+1);
            String st;
            if (nextQ == -1) { st = statesPart.substring(i); i = statesPart.length(); }
            else { st = statesPart.substring(i, nextQ); i = nextQ; }
            // If ends in 'f', it's an accept state
            if (st.endsWith("f")) {
                String stateName = st.substring(0, st.length()-1); // Remove 'f'
                states.add(stateName);
                acceptStates.add(stateName);
            } else {
                states.add(st);
            }
        }
        // First state is start by convention
        String startState = states.iterator().next();

        // Initialize transition table so all states have a map (even if empty)
        Map<String, Map<String, String>> transitions = new HashMap<>();
        for (String state : states)
            transitions.put(state, new HashMap<>());
        Set<String> alphabet = new LinkedHashSet<>();

        // Parse transitions section
        for (int i = 1; i < pieces.length; i++) {
            String t = pieces[i].trim();
            if (t.length() < 4) continue; // skip incomplete
            String from = t.substring(0,2); // "q0"
            String symbol = t.substring(2,3);
            String to = t.substring(3); // "q1"
            transitions.get(from).put(symbol, to);
            alphabet.add(symbol);
        }

        return new DFA(states, alphabet, startState, acceptStates, transitions);
    }

    /**
     * Runs the DFA from the start state on the given input string and returns if it is accepted.
     */
    public boolean accepts(String input) {
        String current = startState;
        for (int i = 0; i < input.length(); i++) {
            String sym = input.substring(i,i+1);
            Map<String, String> map = transitions.get(current);
            if (map == null || !map.containsKey(sym))
                return false;
            current = map.get(sym);
        }
        return acceptStates.contains(current);
    }
}