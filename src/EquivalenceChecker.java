/**
 * EquivalenceChecker.java
 * Decides if two DFAs are equivalent; returns a witness string if not.
 */

import java.util.*;

public class EquivalenceChecker {

    /**
     * Returns "yes" if equivalent, else "no" and a string separating them.
     */
    public static String checkEquivalence(DFA dfa1, DFA dfa2) {
        // We'll do BFS on the product DFA to search for a state (q1, q2) where
        // exactly one is accepting; then output the string leading to it.
        class StatePair {
            String s1, s2;
            StatePair(String s1, String s2) { this.s1 = s1; this.s2 = s2;}
            @Override public boolean equals(Object o) {
                if (!(o instanceof StatePair)) return false;
                StatePair p = (StatePair)o;
                return s1.equals(p.s1) && s2.equals(p.s2);
            }
            @Override public int hashCode() { return Objects.hash(s1, s2);}
        }
        Set<StatePair> visited = new HashSet<>();
        Map<StatePair, String> path = new HashMap<>();
        Queue<StatePair> queue = new ArrayDeque<>();
        StatePair start = new StatePair(dfa1.startState, dfa2.startState);
        visited.add(start);
        path.put(start, "");
        queue.add(start);

        while (!queue.isEmpty()) {
            StatePair curr = queue.poll();
            boolean acc1 = dfa1.acceptStates.contains(curr.s1);
            boolean acc2 = dfa2.acceptStates.contains(curr.s2);
            if (acc1 != acc2) {
                // Witness: string in symmetric difference
                return "no\n" + path.get(curr);
            }
            for (char c : new char[] {'a','b'}) {
                String n1 = dfa1.move(curr.s1, c);
                String n2 = dfa2.move(curr.s2, c);
                StatePair next = new StatePair(n1, n2);
                if (!visited.contains(next)) {
                    visited.add(next);
                    path.put(next, path.get(curr) + c);
                    queue.add(next);
                }
            }
        }
        return "yes";
    }
}