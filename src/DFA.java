/*
 * DFA.java
 * --------
 * Defines a DFA (Deterministic Finite Automaton) class with parsing from assignment format.
 * 
 * Each DFA has:
 *   - set of states
 *   - alphabet
 *   - start state
 *   - set of accept/final states
 *   - transition function (as a nested HashMap)
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
     */
    public DFA(Set<String> states, Set<String> alphabet, String startState,
            Set<String> acceptStates, Map<String, Map<String, String>> transitions) {
        this.states = states;
        this.alphabet = alphabet;
        this.startState = startState;
        this.acceptStates = acceptStates;
        this.transitions = transitions;
    }

    /**
     * Parses a DFA from a line in assignment format.
     * Example: q0fq1q2fq3,q0aq1,q0bq0,q1aq2,q1bq3,q2aq2,q2bq3,q3aq0,q3bq2
     * 
     * Accepting states end with 'f' (e.g. q0f), others do not.
     * Transitions: q0aq1 means from q0, symbol 'a', go to q1.
     * 
     * @param line Encoded line
     * @return DFA object
     */
    public static DFA fromFormat(String line) {
        String[] pieces = line.trim().split(",");
        String statesPart = pieces[0];
        Set<String> states = new HashSet<>();
        Set<String> acceptStates = new HashSet<>();
        // Parse states while detecting accept states (ending in 'f')
        String[] stateTokens = statesPart.split("q");
        for (String s : stateTokens) {
            if (s.isEmpty()) continue;
            String name = "q" + (s.endsWith("f") ? s.substring(0, s.length()-1) : s);
            states.add(name);
            if (s.endsWith("f")) acceptStates.add(name);
        }
        String startState = states.iterator().next(); // First state is always start
        Map<String, Map<String, String>> transitions = new HashMap<>();
        for (String state : states)
            transitions.put(state, new HashMap<>());
        Set<String> alphabet = new HashSet<>();
        // Parse transitions
        for (int i = 1; i < pieces.length; i++) {
            String t = pieces[i];
            String from = t.substring(0,2);
            String symbol = t.substring(2,3);
            String to = t.substring(3);
            transitions.get(from).put(symbol, to);
            alphabet.add(symbol);
        }
        return new DFA(states, alphabet, startState, acceptStates, transitions);
    }

    /**
     * Runs the DFA from the start state on the given input string and returns if it is accepted.
     *
     * @param input Input string (sequence of symbols)
     * @return true if accepted, false otherwise
     */
    public boolean accepts(String input) {
        String current = startState;
        for (int i=0; i<input.length(); i++) {
            String sym = input.substring(i,i+1);
            Map<String,String> map = transitions.get(current);
            if (map == null || !map.containsKey(sym))
                return false;
            current = map.get(sym);
        }
        return acceptStates.contains(current);
    }
}