Problem 2: DFA Equivalence Checker

Algorithm Overview:
Two DFAs are equivalent if they accept exactly the same set of strings. The algorithm constructs a product DFA that accepts strings where the two original DFAs disagree, then checks if this difference DFA is empty.

======================================================

Major Steps:

- Step 1: Build Symmetric Difference DFA

Location: DFAEquivalenceChecker.java, lines 68-120

The symmetric difference DFA has states that are pairs (p, q) from the two original DFAs. A pair is accepting if exactly one of p or q was accepting in its original DFA (using XOR logic).
javaboolean pAccept = dfa1.getAcceptStates().contains(p);
boolean qAccept = dfa2.getAcceptStates().contains(q);
if (pAccept ^ qAccept) {
    acceptStates.add(pq);
}

- Step 2: Explore Product States with BFS

Location: DFAEquivalenceChecker.java, lines 82-115

Starting from the pair of start states, we explore all reachable product states. For each symbol, we compute the next state in both DFAs and create the corresponding product state.
javafor (char symbol : new char[]{'a', 'b'}) {
    String pNext = dfa1.delta(p, symbol);
    String qNext = dfa2.delta(q, symbol);
    if (pNext == null || qNext == null) continue;
    String pqNext = pair(pNext, qNext);
    inner.put(symbol, pqNext);
    if (!visited.contains(pqNext)) {
        visited.add(pqNext);
        states.add(pqNext);
        queue.add(pqNext);
    }
}

- Step 3: Check Emptiness of Difference DFA

Location: DFAEquivalenceChecker.java, lines 48-60

We use the emptiness checker from Problem 1 on the difference DFA. If it's empty, the DFAs are equivalent. If not, the witness string shows where they differ.

javaDFAEmptinessChecker.EmptinessResult er = DFAEmptinessChecker.checkEmptiness(diff);
if (er.isEmpty) {
    return new EquivalenceResult(true, null, false);
} else {
    String w = er.witness;
    boolean firstAccepts = dfa1.accepts(w);
    return new EquivalenceResult(false, w, firstAccepts);
}

======================================================

Worked Example: Problem 2, Test Case 2

Input:

q0q1q2fq3,q0bq0,q0aq1,q1bq2,q1aq3,q2bq2,q2aq3,q3aq3,q3bq3
q0q1q2fq3q4,q0bq0,q0aq1,q1aq3,q1bq2,q2bq2,q2aq4,q3aq3,q3bq4,q4bq2,q4aq3

DFA 1:
- States: {q0, q1, q2, q3}
- Accept: {q2}
- Start: q0
- Key transitions: q0 --a--> q1, q1 --a--> q3

DFA 2:
- States: {q0, q1, q2, q3, q4}
- Accept: {q2}
- Start: q0
- Key transitions: q0 --a--> q1, q1 --a--> q3, q2 --a--> q4, q4 --b--> q2

Execution Trace:

1. Build Product DFA: Start with (q0|q0)

2. Product State (q0|q0):
   - Neither q0 is accepting -> not an accepting product state
   - On 'a': (q0|q0) -> (q1|q1)
   - On 'b': (q0|q0) -> (q0|q0)

3. Product State (q1|q1):
   - Neither q1 is accepting -> not an accepting product state
   - On 'a': (q1|q1) -> (q3|q3)
   - On 'b': (q1|q1) -> (q2|q2)

4. Product State (q2|q2):
   - Both q2 are accepting -> not an accepting product state (XOR = false)
   - On 'a': (q2|q2) -> (q3|q4)
   - On 'b': (q2|q2) -> (q2|q2)

5. Product State (q3|q4):
   - DFA1's q3 is NOT accepting, DFA2's q4 is NOT accepting
   - On 'b': (q3|q4) -> (q3|q2)

6. Product State (q3|q2):
   - DFA1's q3 is NOT accepting, DFA2's q2 IS accepting
   - This is an accepting product state! (XOR = true)
   - Path to reach it: start -> a -> a -> b -> b = "aabb"

7. Check Emptiness: The difference DFA is non-empty, witness = "aabb"

8. Verify: 
   - DFA1.accepts("aabb") = false
   - DFA2.accepts("aabb") = true

Output:

no, the 2 languages are not equal. The following string is accepted by 1 but not the other:
String: aabb
accepted by DFA #2