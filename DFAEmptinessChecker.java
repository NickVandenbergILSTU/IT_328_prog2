
import java.util.*;



/**
 * Implements an algorithm to decide whether the language of a DFA is empty.
 *
 * Algorithm (reachability BFS):
 *  1. Start from the start state with the empty string "".
 *  2. Perform BFS over the state graph, tracking the input string used to reach each state.
 *  3. If we ever reach an accepting state, the language is non-empty and
 *     the corresponding string is a witness.
 *  4. If BFS finishes without reaching an accepting state, the language is empty.
 */
public class DFAEmptinessChecker {


    /**
     * Result container:
     *  - isEmpty: true if language is empty
     *  - witness: a string accepted by the DFA if non-empty, otherwise null
     */
    public static class EmptinessResult {
        public final boolean isEmpty;
        public final String witness;


        public EmptinessResult(boolean isEmpty, String witness) {
            this.isEmpty = isEmpty;
            this.witness = witness;
        }
    }



    /**
     * Checks whether the language of the given DFA is empty.
     *
     * @param dfa DFA to check
     * @return EmptinessResult
     */
    public static EmptinessResult checkEmptiness(DFA dfa) {


        String start = dfa.getStartState();
        Set<String> accept = dfa.getAcceptStates();


        // BFS queue holds pairs (state, inputStringLeadingHere)
        Queue<StateWithString> queue = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();

        queue.add(new StateWithString(start, ""));
        visited.add(start);



        while (!queue.isEmpty()) {


            StateWithString current = queue.poll();
            String state = current.state;
            String str = current.witness;

            if (accept.contains(state)) {
                // We have found an accepting state; str is a witness.
                return new EmptinessResult(false, str);
            }



            for (char symbol : new char[]{'a', 'b'}) {
                String next = dfa.delta(state, symbol);
                if (next != null && !visited.contains(next)) {
                    visited.add(next);
                    queue.add(new StateWithString(next, str + symbol));
                }
            }
        }

        // No accepting state is reachable
        return new EmptinessResult(true, null);
    }



    /**
     * Helper class to store state and string used to reach it.
     */
    private static class StateWithString {
        
        final String state;
        final String witness;

        StateWithString(String state, String witness) {
            this.state = state;
            this.witness = witness;
        }
    }
}