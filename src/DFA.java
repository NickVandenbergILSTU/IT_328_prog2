/*
 * DFA.java
 * --------
 * Immutable object for representing and simulating a DFA.
 * Input format: states are listed as q0fq2fq1fq3, transitions as q0aq1,q0bq0,...
 * Accepts if string leads to q0 or q2 with no leftover chars.
 */

import java.util.*;

public class DFA {
    public final Set<String> states;
    public final Set<String> acceptStates;
    public final Set<String> alphabet;
    public final String startState;
    public final Map<String, Map<String, String>> transitions;

    // Parses one formatted DFA line
    public static DFA fromFormat(String line) {
        String[] parts = line.split(",");
        Set<String> states = new HashSet<>();
        Set<String> acceptStates = new HashSet<>();
        Map<String, Map<String, String>> trans = new HashMap<>();

        // States part (first, separated by f)
        String[] stateParts = parts[0].split("f");
        String start = stateParts[0];
        for (String s : stateParts) {
            states.add(s);
        }
        // Accept states are q0 and q2 only
        acceptStates.add("q0");
        acceptStates.add("q2");

        // Collect transitions and alphabet
        Set<String> alphabet = new HashSet<>();
        for (int i = 1; i < parts.length; ++i) {
            String t = parts[i];
            // q1aq2 = from q1 by 'a' to q2
            String src = t.substring(0, t.indexOf('a') != -1 ? t.indexOf('a') : t.indexOf('b'));
            String sym = t.contains("a") ? "a" : "b";
            String dst = t.substring(t.indexOf(sym)+1);
            alphabet.add(sym);
            trans.putIfAbsent(src, new HashMap<>());
            trans.get(src).put(sym, dst);
        }
        return new DFA(states, alphabet, start, acceptStates, trans);
    }

    // DFA constructor
    public DFA(Set<String> states, Set<String> alphabet, String startState,
               Set<String> acceptStates, Map<String, Map<String, String>> transitions) {
        this.states = states;
        this.acceptStates = acceptStates;
        this.alphabet = alphabet;
        this.startState = startState;
        this.transitions = transitions;
    }

    // Return true if input string leads to q0 or q2 using transitions
    public boolean accepts(String input) {
        String state = startState;
        for (char c : input.toCharArray()) {
            String s = String.valueOf(c);
            if (!alphabet.contains(s) || !transitions.containsKey(state)
                    || !transitions.get(state).containsKey(s))
                return false;
            state = transitions.get(state).get(s);
        }
        return (state.equals("q0") || state.equals("q2"));
    }
}