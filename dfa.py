"""
Defines a DFA class and utility functions for parsing DFAs from the assignment's required text format.
All state, alphabet, transition, and accept state operations are encapsulated here.

Usage:
    from dfa import DFA

Author(s): Nick Vandenberg
"""
import collections

class DFA:
    def __init__(self, states, alphabet, start_state, accept_states, transitions):
        """
        Initializes the DFA with all required components.

        Args:
            states: Set of all state names (e.g., {"q0", "q1", "q2"})
            alphabet: Set of all symbols (e.g., {"a", "b"})
            start_state: The name of the start state (e.g., "q0")
            accept_states: Set of accept state names (e.g., {"q0", "q2"})
            transitions: Dict of form {state: {symbol: next_state}}, e.g. {"q0": {"a": "q1", "b":"q0"}}
        """
        self.states = set(states)
        self.alphabet = set(alphabet)
        self.start_state = start_state
        self.accept_states = set(accept_states)
        self.transitions = transitions    # transitions[state][symbol] => next_state

    @staticmethod
    def from_format(line):
        """
        Parse a DFA from the assignment-specified text format into a DFA object.

        The format:
            Header section: states, some ending with 'f' for final/accepting, e.g., q0fq1q2fq3
            Transition section: Each like 'q0aq1' for from q0, symbol a, to q1

        Example input:
            "q0fq1q2fq3,q0aq1,q0bq0,q1aq2,q1bq3,q2aq2,q2bq3,q3aq0,q3bq2"

        Returns:
            DFA instance with parsed components

        Comments:
            - This function handles parsing both state and transition sections.
            - Accepting states are noted with a trailing 'f'.
            - Transitions are parsed by string slicing, relying on the consistent DFA encoding.
        """
        field, *transitions_strs = line.strip().split(',')
        states = []
        accept_states = []
        # Parse states and identify accepting states
        for st in field.split('q')[1:]:
            # Each state string starts after 'q', ends with optional 'f'
            stname = 'q' + st[:-1] if st[-1] == 'f' else 'q' + st
            states.append(stname)
            if st[-1] == 'f':
                accept_states.append(stname)
        # The first state is always the start state by convention
        start_state = states[0]
        transitions = {s: {} for s in states}
        alphabet = set()
        # Parse transitions
        for t in transitions_strs:
            # Transition encodings like q0aq1, i.e., from 'q0', symbol 'a', to 'q1'
            s = t[0:2]      # From state (e.g., "q0")
            a = t[2]        # Transition symbol (e.g., "a")
            s2 = t[3:]      # To state (e.g., "q1")
            transitions[s][a] = s2
            alphabet.add(a)
        return DFA(states, alphabet, start_state, accept_states, transitions)