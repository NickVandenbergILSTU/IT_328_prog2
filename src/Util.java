/*
 * Util.java
 * ---------
 * Common utility functions for file IO and parsing.
 * Can be imported for reading input files, skipping comments, and more.
 *
 * Author(s): (put your names here)
 */

import java.io.*;
import java.util.*;

public class Util {
    /**
     * Reads the first non-comment, non-blank line from a file (for single-DFA inputs).
     * 
     * @param filename File to read
     * @return First valid line, or "" if none found
     */
    public static String readSingleDfaLine(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        do {
            line = br.readLine();
        } while (line != null && (line.startsWith("#") || line.trim().isEmpty()));
        br.close();
        return line == null ? "" : line.trim();
    }

    /**
     * Reads all non-comment, non-blank lines from a file (for multi-DFA inputs).
     * 
     * @param filename File to read
     * @return List of all valid lines
     */
    public static List<String> readAllDfaLines(String filename) throws IOException {
        List<String> lines = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            lines.add(line);
        }
        br.close();
        return lines;
    }

    /**
     * Prints usage message and exits program.
     * @param msg Custom error message
     */
    public static void errorExit(String msg) {
        System.out.println(msg);
        System.exit(1);
    }
}