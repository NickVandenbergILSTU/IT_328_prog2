import java.util.*;

/**
 * Represents a DFA over an alphabet {a, b}.
 *
 * States are represented as strings (e.g., "q0", "q1", ...).
 * Transitions are stored in a map: state -> (symbol -> nextState).
 */
public class DFA {
    private final Set<String> states;
    private final String startState;
    private final Set<String> acceptStates;
    private final Map<String, Map<Character, String>> transitionFunction;

    /**
     * Construct a DFA.
     *
     * @param states             set of all states
     * @param startState         start state
     * @param acceptStates       set of accepting states
     * @param transitionFunction transition function
     */
    public DFA(Set<String> states,
               String startState,
               Set<String> acceptStates,
               Map<String, Map<Character, String>> transitionFunction) {
        this.states = Collections.unmodifiableSet(new HashSet<>(states));
        this.startState = startState;
        this.acceptStates = Collections.unmodifiableSet(new HashSet<>(acceptStates));
        this.transitionFunction = new HashMap<>();
        for (Map.Entry<String, Map<Character, String>> e : transitionFunction.entrySet()) {
            this.transitionFunction.put(e.getKey(), new HashMap<>(e.getValue()));
        }
    }

    public Set<String> getStates() {
        return states;
    }

    public String getStartState() {
        return startState;
    }

    public Set<String> getAcceptStates() {
        return acceptStates;
    }

    public Map<String, Map<Character, String>> getTransitionFunction() {
        return transitionFunction;
    }

    /**
     * Returns the next state for a given state and input symbol.
     *
     * @param state current state
     * @param symbol input symbol ('a' or 'b')
     * @return next state, or null if undefined
     */
    public String delta(String state, char symbol) {
        Map<Character, String> inner = transitionFunction.get(state);
        if (inner == null) return null;
        return inner.get(symbol);
    }

    /**
     * Simulate this DFA on the given input string.
     *
     * @param input string over {a,b}
     * @return true if this DFA accepts the string, false otherwise
     */
    public boolean accepts(String input) {
        String current = startState;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            current = delta(current, c);
            if (current == null) {
                return false;
            }
        }
        return acceptStates.contains(current);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("States: ").append(states).append("\n");
        sb.append("Start: ").append(startState).append("\n");
        sb.append("Accept: ").append(acceptStates).append("\n");
        sb.append("Transitions:\n");
        for (String s : states) {
            Map<Character, String> inner = transitionFunction.get(s);
            if (inner != null) {
                for (Map.Entry<Character, String> e : inner.entrySet()) {
                    sb.append("  ").append(s).append(" --")
                            .append(e.getKey()).append("--> ")
                            .append(e.getValue()).append("\n");
                }
            }
        }
        return sb.toString();
    }
}