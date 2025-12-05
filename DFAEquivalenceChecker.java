

import java.util.*;

/**
 * Uses DFAEmptinessChecker to check language equivalence of two DFAs.
 *
 * Two DFAs M1 and M2 are equivalent iff:
 *   L(M1) = L(M2)
 *
 * We use the symmetric difference construction:
 *   L(M1) Δ L(M2) = (L(M1) \ L(M2)) ∪ (L(M2) \ L(M1))
 *
 * 1. Construct a product DFA Mdiff whose states are pairs (p, q).
 * 2. (p, q) is accepting in Mdiff iff exactly one of p, q is accepting in the
 *    original DFAs (i.e., they differ).
 * 3. L(M1) = L(M2)  iff  L(Mdiff) is empty.
 *
 * We then call DFAEmptinessChecker on Mdiff. If non-empty, the witness
 * found is a string that is accepted by exactly one of M1, M2.
 */
public class DFAEquivalenceChecker {



    /**
     * Result container for equivalence check.
     *
     * - equivalent: true if languages are equal
     * - witness: if not equivalent, a string in the symmetric difference
     *            (accepted by exactly one of the DFAs)
     * - acceptedByFirst: if not equivalent, whether the witness is accepted by the
     *                    first DFA (otherwise it is accepted by the second)
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
     * Check whether two DFAs are language-equivalent.
     *
     * @param dfa1 first DFA
     * @param dfa2 second DFA
     * @return EquivalenceResult
     */
    public static EquivalenceResult checkEquivalence(DFA dfa1, DFA dfa2) {


        // Build product DFA Mdiff
        DFA diff = buildSymmetricDifferenceDFA(dfa1, dfa2);


        // Use emptiness checker on Mdiff
        DFAEmptinessChecker.EmptinessResult er = DFAEmptinessChecker.checkEmptiness(diff);



        if (er.isEmpty) {
            return new EquivalenceResult(true, null, false);
        } else {
            String w = er.witness;
            boolean firstAccepts = dfa1.accepts(w);
            // By construction, exactly one accepts, so secondAccepts = !firstAccepts
            return new EquivalenceResult(false, w, firstAccepts);



        }
    }




    /**
     * Build a DFA recognizing the symmetric difference of L(dfa1) and L(dfa2).
     */
    private static DFA buildSymmetricDifferenceDFA(DFA dfa1, DFA dfa2) {



        // Product states: (p, q) as string "p|q"
        Set<String> states = new LinkedHashSet<>();
        Set<String> acceptStates = new LinkedHashSet<>();
        Map<String, Map<Character, String>> transitionFunction = new HashMap<>();




        String start = pair(dfa1.getStartState(), dfa2.getStartState());
        states.add(start);


        Queue<String> queue = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            String pq = queue.poll();
            String[] parts = pq.split("\\|", 2);
            String p = parts[0];
            String q = parts[1];




            boolean pAccept = dfa1.getAcceptStates().contains(p);
            boolean qAccept = dfa2.getAcceptStates().contains(q);
            if (pAccept ^ qAccept) {
                acceptStates.add(pq);
            }



            Map<Character, String> inner = transitionFunction.computeIfAbsent(pq, k -> new HashMap<>());

            for (char symbol : new char[]{'a', 'b'}) {
                String pNext = dfa1.delta(p, symbol);
                String qNext = dfa2.delta(q, symbol);
                if (pNext == null || qNext == null) {
                    // If transitions are missing, we stop following this path.
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