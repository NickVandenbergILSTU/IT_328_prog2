import java.util.*;

/**
 * Checks if two DFAs recognize the same language by using DFAEmptinessChecker.
 *
 * Two DFAs M1 and M2 are equivalent if they accept exactly the same set of strings.
 *
 * 
 * The approach: build a new DFA that accepts strings that are in one language
 * but not the other. If this DFA's language is empty, then the original two
 * DFAs must be equivalent.
 *
 * 1. Create a product DFA where states are pairs (p, q) from the two DFAs.
 * 2. Mark (p, q) as accepting if exactly one of p or q was accepting in its
 *    original DFA (meaning the DFAs disagree on that string).
 * 3. If the product DFA accepts nothing, then M1 and M2 are equivalent.
 *
 * 
 * We use DFAEmptinessChecker on the product DFA. If it finds a string, that's
 * a witness showing the DFAs differ.
 */
public class DFAEquivalenceChecker {



    /**
     * Holds the result of checking if two DFAs are equivalent.
     *
     * - equivalent: true if the DFAs accept the same language
     * - witness: if not equivalent, an example string that one DFA accepts
     *            and the other rejects
     * - acceptedByFirst: if not equivalent, tells us whether the witness is
     *                    accepted by the first DFA (false means the second accepts it)
     */
    public static class EquivalenceResult {
        public final boolean equivalent;
        public final String witness;
        public final boolean acceptedByFirst;

        public EquivalenceResult(boolean equivalent, String witness, boolean acceptedByFirst) {
            this.equivalent = equivalent;
            this.witness = witness;
            this.acceptedByFirst = acceptedByFirst;
        }
    }




    /**
     * Check whether two DFAs recognize the same language.
     *
     * @param dfa1 first DFA
     * @param dfa2 second DFA
     * @return EquivalenceResult with the answer and possibly a counterexample
     */
    public static EquivalenceResult checkEquivalence(DFA dfa1, DFA dfa2) {


        // Build a DFA that accepts strings where dfa1 and dfa2 disagree
        DFA diff = buildSymmetricDifferenceDFA(dfa1, dfa2);


        // Check if this difference DFA accepts anything
        DFAEmptinessChecker.EmptinessResult er = DFAEmptinessChecker.checkEmptiness(diff);




        if (er.isEmpty) {

            // The DFAs never disagree, so they're equivalent
            return new EquivalenceResult(true, null, false);
        } else {


            // Found a string where they disagree
            String w = er.witness;
            boolean firstAccepts = dfa1.accepts(w);

            // Exactly one of them accepts it
            return new EquivalenceResult(false, w, firstAccepts);
        }
    }




    /**
     * Build a DFA that accepts strings where dfa1 and dfa2 disagree.
     * This is the "symmetric difference" - strings in one language but not both.
     */
    private static DFA buildSymmetricDifferenceDFA(DFA dfa1, DFA dfa2) {
        // Product states are pairs (p, q) encoded as "p|q"
        Set<String> states = new LinkedHashSet<>();
        Set<String> acceptStates = new LinkedHashSet<>();
        Map<String, Map<Character, String>> transitionFunction = new HashMap<>();


        String start = pair(dfa1.getStartState(), dfa2.getStartState());
        states.add(start);



        // BFS to explore all reachable product states
        Queue<String> queue = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            String pq = queue.poll();
            String[] parts = pq.split("\\|", 2);
            String p = parts[0];
            String q = parts[1];



            // Accept this product state if exactly one of p or q is accepting
            // (meaning dfa1 and dfa2 disagree at this point)
            boolean pAccept = dfa1.getAcceptStates().contains(p);
            boolean qAccept = dfa2.getAcceptStates().contains(q);
            if (pAccept ^ qAccept) {
                acceptStates.add(pq);
            }


            

            // Build transitions for this product state
            Map<Character, String> inner = transitionFunction.computeIfAbsent(pq, k -> new HashMap<>());

            for (char symbol : new char[]{'a', 'b'}) {
                String pNext = dfa1.delta(p, symbol);
                String qNext = dfa2.delta(q, symbol);
                if (pNext == null || qNext == null) {
                    // If either DFA doesn't have a transition, skip this symbol
                    continue;
                }
                String pqNext = pair(pNext, qNext);
                inner.put(symbol, pqNext);
                if (!visited.contains(pqNext)) {
                    visited.add(pqNext);
                    states.add(pqNext);
                    queue.add(pqNext);
                }
            }
        }

        return new DFA(states, start, acceptStates, transitionFunction);
    }

    private static String pair(String p, String q) {
        return p + "|" + q;
    }
}