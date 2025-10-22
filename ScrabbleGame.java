import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Simplified ScrabbleGame: loads a dictionary file (wordsWithDefs.txt) where each line
 * contains a word and optionally a definition separated by a tab or two+ spaces.
 * Prints the first 100 entries and a count. Kept intentionally small and error-free.
 *
 * --- IMPROVEMENT: WORD SCORING SYSTEM ---
 * This version awards points to the user for valid words based on word length:
 * - 1 point per letter in the word
 * - Bonus: +5 points for words of 6+ letters, +10 for 8+ letters
 * The score is displayed after a valid word is found.
 *
 * See comments in code for details of the scoring logic.
 */
public class ScrabbleGame {

    // in-memory dictionary: loaded from wordsWithDefs.txt and kept sorted
    private static List<Word> dictionary = new ArrayList<>();

    public static void main(String[] args) {
        // Attempt to read the dictionary file from the working directory.
        File f = new File("wordsWithDefs.txt");
        if (!f.exists()) {
            System.err.println("Dictionary file wordsWithDefs.txt not found in working directory.");
            System.err.println("Create a wordsWithDefs.txt file or place one in the working directory.");
            return;
        }

        // Read file into Word objects
        try (Scanner in = new Scanner(f)) {
            while (in.hasNextLine()) {
                String line = in.nextLine();
                if (line == null) continue;
                line = line.trim();
                if (line.isEmpty()) continue;

                String word = null;
                String def = "";

                int tab = line.indexOf('\t');
                if (tab >= 0) {
                    word = line.substring(0, tab).trim();
                    def = line.substring(tab + 1).trim();
                } else {
                    String[] parts = line.split("\\s{2,}", 2);
                    word = parts[0].trim();
                    if (parts.length == 2) def = parts[1].trim();
                }

                if (word != null && !word.isEmpty()) dictionary.add(new Word(word, def));
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
            return;
        }

        // Sort the dictionary so we can binary-search it later.
        Collections.sort(dictionary);

        System.out.println("Loaded " + dictionary.size() + " dictionary entries.");

        /*
         * Pick 4 random uppercase letters and prompt the user to form a word.
         * The candidate is checked to ensure it uses only the provided letters
         * (counts respected). If it passes that test we run a handwritten
         * binary search over the sorted `dictionary` to determine whether the
         * word is present.
         */
        final char[] letters = pickRandomLetters(4);
        System.out.printf("Your letters are: %c %c %c %c%n", letters[0], letters[1], letters[2], letters[3]);

        // Prompt the user for a single word (case-insensitive)
        try (Scanner stdin = new Scanner(System.in)) {
            System.out.print("Enter a word you can make from those letters: ");
            String candidate = stdin.hasNextLine() ? stdin.nextLine().trim() : "";
            if (candidate.isEmpty()) {
                System.out.println("No word entered. Exiting.");
                return;
            }

            // verify candidate uses only letters from the pool
            if (!usesOnlyLetters(candidate, letters)) {
                System.out.println("The word '" + candidate + "' cannot be formed from the given letters.");
                return;
            }

            // run a handwritten binary search
            final int idx = binarySearchWord(dictionary, candidate);
            if (idx >= 0) {
                final Word found = dictionary.get(idx);
                System.out.println("Valid word found: " + found.getWord());
                final String def = found.getDefinition();
                if (def != null && !def.isEmpty()) System.out.println("Definition: " + def);

                // --- IMPROVEMENT: SCORING SYSTEM ---
                // Award points for valid words based on their length.
                int score = calculateScore(found.getWord());
                System.out.println("You scored " + score + " points for this word!");
            } else {
                System.out.println("Word '" + candidate + "' not found in dictionary.");
            }
        }
    }

    /**
     * Improvement: Calculate score for a word based on its length.
     * - 1 point per letter
     * - Bonus: +5 points for words of 6+ letters, +10 for 8+ letters
     * @param word the word to score
     * @return total score
     */
    private static int calculateScore(String word) {
        if (word == null) return 0;
        int len = word.trim().length();
        int score = len; // 1 point per letter
        // Bonus for longer words
        if (len >= 8) score += 10;
        else if (len >= 6) score += 5;
        return score;
    }

    // choose n random uppercase letters A-Z
    private static char[] pickRandomLetters(int n) {
        Random r = new Random();
        char[] out = new char[n];
        for (int i = 0; i < n; i++) out[i] = (char) ('A' + r.nextInt(26));
        return out;
    }

    // verify candidate uses only letters from pool (counts respected), case-insensitive
    private static boolean usesOnlyLetters(String candidate, char[] pool) {
        int[] cnt = new int[26];
        for (char c : pool) {
            if (Character.isLetter(c)) cnt[Character.toUpperCase(c) - 'A']++;
        }
        for (char ch : candidate.toCharArray()) {
            if (!Character.isLetter(ch)) return false;
            int idx = Character.toUpperCase(ch) - 'A';
            if (idx < 0 || idx >= 26) return false;
            if (cnt[idx] == 0) return false;
            cnt[idx]--;
        }
        return true;
    }

    // Handwritten binary search over sorted list of Word objects. Case-insensitive compare.
    private static int binarySearchWord(List<Word> list, String target) {
        if (target == null) return -1;
        target = target.trim();
        if (target.isEmpty()) return -1;
        int lo = 0;
        int hi = list.size() - 1;
        while (lo <= hi) {
            int mid = (lo + hi) >>> 1;
            Word midW = list.get(mid);
            String midWord = midW == null ? null : midW.getWord();
            if (midWord == null) {
                // treat null as less than target
                lo = mid + 1;
                continue;
            }
            int cmp = midWord.compareToIgnoreCase(target);
            if (cmp == 0) return mid;
            if (cmp < 0) {
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }
        return -1;
    }
}