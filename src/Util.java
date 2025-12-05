/**
 * Util.java
 * Miscellaneous utility functions.
 */

import java.io.*;
import java.util.*;

public class Util {
    // Read a list of lines from a file (UTF-8)
    public static List<String> readLines(String filename) throws IOException {
        List<String> lines = new ArrayList<>();
        BufferedReader r = new BufferedReader(new FileReader(filename));
        String ln;
        while ((ln = r.readLine()) != null)
            if (!ln.trim().isEmpty())
                lines.add(ln.trim());
        r.close();
        return lines;
    }

    // For demo: parse DFA files, print output
    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("Usage: java Util empty <file> | equiv <file>");
            System.exit(1);
        }
        String cmd = args[0];
        if (cmd.equals("empty")) {
            String dfaDescr = readLines(args[1]).get(0);
            DFA dfa = new DFA(dfaDescr);
            System.out.println(EmptinessChecker.checkEmptiness(dfa));
        } else if (cmd.equals("equiv")) {
            List<String> lines = readLines(args[1]);
            if (lines.size() < 2) { System.err.println("Need two DFA descriptions."); return;}
            DFA dfa1 = new DFA(lines.get(0));
            DFA dfa2 = new DFA(lines.get(1));
            System.out.println(EquivalenceChecker.checkEquivalence(dfa1, dfa2));
        } else {
            System.err.println("Unknown command: " + cmd);
        }
    }
}