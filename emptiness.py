"""
Implements Problem 1: determining if a DFA's language is empty, and finding a witness string if it is not.

Usage:
    python emptiness.py ex1.txt

Author(s): Nick Vandenberg
"""
from dfa import DFA
import collections

def is_empty(dfa):
    """
    Determines whether the given DFA accepts any strings (i.e., if the language is not empty).
    If so, returns a witness string accepted by the DFA (usually the shortest found).

    Args:
        dfa: The DFA object to check.

    Returns:
        (True, None) if DFA language is empty (accepts no strings).
        (False, witness_str) if DFA language is not empty, where witness_str is accepted string.

    Method:
        - Uses Breadth-First Search from start state.
        - Records the input string (path) along transitions.
        - If it reaches any accept state, immediately returns the corresponding input string.
        - If search finishes without finding an accept state, DFA language is empty.
    """
    # Queue for BFS: each item is (current_state, path_so_far)
    queue = collections.deque()
    visited = set()
    queue.append( (dfa.start_state, "") )
    while queue:
        state, path = queue.popleft()
        # Check if we've reached an accepting state
        if state in dfa.accept_states:
            # Found a shortest witness string, since BFS finds minimal path first
            return False, path
        # Mark this state as visited so we don't search it again
        if state in visited:
            continue
        visited.add(state)
        # For each symbol in the DFA's alphabet, follow transitions
        for symbol in dfa.alphabet:
            next_state = dfa.transitions[state].get(symbol)
            if next_state:
                # Enqueue next state and append the symbol to the path
                queue.append( (next_state, path + symbol) )
    # Finished BFS without hitting any final/accepting state => language is empty
    return True, None

if __name__ == "__main__":
    # Read DFA from file path supplied as argument
    import sys
    if len(sys.argv) < 2:
        print("Usage: python emptiness.py input_file.txt")
        sys.exit(1)
    # File should contain one line encoding the DFA as specified
    with open(sys.argv[1]) as f:
        line = f.readline()
    dfa = DFA.from_format(line)
    empty, witness = is_empty(dfa)
    if empty:
        print("yes")  # DFA's language is empty
    else:
        print("no")   # DFA accepts some string(s)
        print(witness)  # Print one accepted string (shortest found)