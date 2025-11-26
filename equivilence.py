"""
Implements Problem 2: tests if two DFAs accept exactly the same language, and gives a separating string if not.

Usage:
    python equivalence.py ex2.txt

Author(s): Nick
"""
from dfa import DFA
from emptiness import is_empty
import collections

def symmetric_difference_dfa(dfa1, dfa2):
    """
    Constructs a DFA that accepts strings accepted by exactly one of dfa1 or dfa2.
    This is called the symmetric difference DFA.

    Args:
        dfa1, dfa2: Two DFA objects.

    Returns:
        A new DFA object representing the symmetric difference.

    Method:
        - The states of the new DFA are pairs (s1, s2), one from each original DFA.
        - The start state is the pair of both start states.
        - Transition with symbol a goes to (dfa1's next state on a, dfa2's next state on a)
        - Accepting states are those where exactly one of the originals is accepting (XOR).
        - Alphabet is the intersection of both DFA alphabets for safety.
    """
    states = []            # Each state is a tuple: (state_from_dfa1, state_from_dfa2)
    transitions = {}
    accept_states = []
    # Only use symbols common to both DFAs (their intersection)
    alphabet = dfa1.alphabet.intersection(dfa2.alphabet)
    # Build DFA states by all combinations of states of DFA1 and DFA2
    for s1 in dfa1.states:
        for s2 in dfa2.states:
            pair = (s1, s2)
            states.append(pair)
            transitions[pair] = {}
            for a in alphabet:
                ns1 = dfa1.transitions[s1].get(a)
                ns2 = dfa2.transitions[s2].get(a)
                if ns1 and ns2:
                    transitions[pair][a] = (ns1, ns2)
            # Accept if exactly one component is accepting ("in one but not the other")
            if ( (s1 in dfa1.accept_states) != (s2 in dfa2.accept_states) ):
                accept_states.append(pair)
    return DFA(
        states = states,
        alphabet = alphabet,
        start_state = (dfa1.start_state, dfa2.start_state),
        accept_states = accept_states,
        transitions = transitions
    )

if __name__ == "__main__":
    # Read input file containing two DFA encodings, one per line
    import sys
    if len(sys.argv) < 2:
        print("Usage: python equivalence.py input_file.txt")
        sys.exit(1)
    # Each line encodes one DFA in assignment format
    with open(sys.argv[1]) as f:
        lines = f.readlines()
    if len(lines) < 2:
        print("Error: input file must contain two lines (one for each DFA).")
        sys.exit(1)
    # Parse each DFA
    dfa1 = DFA.from_format(lines[0])
    dfa2 = DFA.from_format(lines[1])
    # Build symmetric difference DFA (strings accepted by one but not the other)
    sd_dfa = symmetric_difference_dfa(dfa1, dfa2)
    # Check if symmetric difference language is empty (languages are equal)
    empty, witness = is_empty(sd_dfa)
    if empty:
        print("yes")  # Languages of DFA1 and DFA2 are equal
    else:
        print("no")   # Languages differ
        print(witness)  # Print example string accepted by one DFA but not the other