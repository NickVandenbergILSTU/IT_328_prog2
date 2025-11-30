## Demo Instructions

### DFA Emptiness Check
To test if a DFA accepts any strings:
1. Compile all .java files in src/:
   ```
   javac src/*.java
   ```
2. Run the emptiness checker with an example DFA file:
   ```
   java -cp src EmptinessChecker examples/ex1.txt
   ```
   - Output "no" and a witness string (if language not empty).
   - Or "yes" if empty.

### DFA Equivalence Check
To test if two DFAs have the same language:
1. Run the equivalence checker with a sample input file of two DFAs:
   ```
   java -cp src EquivalenceChecker examples/ex2.txt
   ```
   - Output "yes" if languages are the same, else "no" and a separating string.

### Usage Notes
- Input files may have comments ('#') which are skipped.
- See examples/ for valid DFA formats.
- List all team members in team.txt and use report.md to match rubric.
