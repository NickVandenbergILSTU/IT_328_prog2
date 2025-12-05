/**
 * EmptinessChecker.java
 * Decides if the language of a DFA is empty, and finds a witness string if not.
 */

import java.util.*;

public class EmptinessChecker {

    /**
     * Returns "yes" if DFA's language is empty, else "no" and a string accepted
     * e.g. output: "no\nabaa"
     */
    public static String checkEmptiness(DFA dfa) {
        // BFS from startState, record path to each state
        Map<String, String> from = new HashMap<>();
        Map<String, String> discoveryString = new HashMap<>();
        Queue<String> queue = new ArrayDeque<>();
        from.put(dfa.startState, null);
        discoveryString.put(dfa.startState, "");
        queue.add(dfa.startState);
        while (!queue.isEmpty()) {
            String current = queue.poll();
            String curString = discoveryString.get(current);
            if (dfa.acceptStates.contains(current)) {
                // Found a path to accept
                return "no\n" + curString;
            }
            for (char c : new char[] {'a', 'b'}) {
                String next = dfa.move(current, c);
                if (next != null && !from.containsKey(next)) {
                    from.put(next, current);
                    discoveryString.put(next, curString + c);
                    queue.add(next);
                }
            }
        }
        return "yes";
    }
}