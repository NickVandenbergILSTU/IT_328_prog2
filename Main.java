

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Entry point for both Problem 1 and Problem 2.
 *
 * Usage:
 *
 *   Problem 1 (emptiness of a single DFA):
 *     java edu.ilstu.it328.dfa.Main problem1 <inputFile>
 *
 *     where <inputFile> contains ONE line describing a DFA.
 *
 *   Output format:
 *     - If language is empty:       "yes"
 *     - If language is non-empty:   "no" on first line,
 *                                   witness string on second line.
 *
 *   Problem 2 (equivalence of two DFAs):
 *     java Main problem2 <inputFile>
 *
 *     where <inputFile> contains TWO lines, each describing a DFA.
 *
 *   Output format:
 *     - If languages are equal:     "yes"
 *     - If not equal:               "no" on first line,
 *                                   witness string on second line,
 *                                   and a line indicating which DFA accepts it:
 *                                   "accepted by: 1" or "accepted by: 2"
 */
public class Main {

    public static void main(String[] args) {
        if (args.length != 2) {
            printUsageAndExit();
        }

        String mode = args[0];
        String inputFile = args[1];

        try {
            if ("problem1".equalsIgnoreCase(mode)) {
                runProblem1(inputFile);
            } else if ("problem2".equalsIgnoreCase(mode)) {
                runProblem2(inputFile);
            } else {
                System.err.println("Unknown mode: " + mode);
                printUsageAndExit();
            }
        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.err.println("Input parsing error: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void runProblem1(String inputFile) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line = br.readLine();
            if (line == null) {
                throw new IllegalArgumentException("Input file is empty for problem1.");
            }
            DFA dfa = DFAParser.parseFromLine(line);

            DFAEmptinessChecker.EmptinessResult result = DFAEmptinessChecker.checkEmptiness(dfa);
            if (result.isEmpty) {
                System.out.println("yes, the language is empty");
            } else {
                System.out.println("no, the language is non-empty. The following string is accepted:");
                System.out.println("Accepted string: " + result.witness);
                System.out.println("Note: if the accepted string is empty, then the DFA accepts the empty string.");
            }
        }
    }

    private static void runProblem2(String inputFile) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line1 = br.readLine();
            String line2 = br.readLine();
            if (line1 == null || line2 == null) {
                throw new IllegalArgumentException("Input file must contain exactly two lines for problem2.");
            }
            DFA dfa1 = DFAParser.parseFromLine(line1);
            DFA dfa2 = DFAParser.parseFromLine(line2);

            DFAEquivalenceChecker.EquivalenceResult result =
                    DFAEquivalenceChecker.checkEquivalence(dfa1, dfa2);

            if (result.equivalent) {
                System.out.println("yes, the 2 languages are equal");
            } else {
                System.out.println("no, the 2 languages are not equal. The following string is accepted by 1 but not the other:");
                System.out.println("String: " + result.witness);
                System.out.println("accepted by DFA #" + (result.acceptedByFirst ? "1" : "2"));
            }
        }
    }

    private static void printUsageAndExit() {
        System.err.println("Usage:");
        System.err.println("  Problem 1 (emptiness):");
        System.err.println("    java Main problem1 <inputFile>");
        System.err.println();
        System.err.println("  Problem 2 (equivalence):");
        System.err.println("    java Main problem2 <inputFile>");
        System.exit(1);
    }
}