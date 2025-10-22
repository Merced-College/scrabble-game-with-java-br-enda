import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Mini Scrabble Game (4-letter version)
 * -------------------------------------
 * - Loads words and definitions from wordsWithDefs.txt
 * - Gives 4 random letters (more vowels)
 * - You type words you can make with those letters
 * - Uses a hand-written binary search to check if the word is valid
 *
 * --------------------------------------------------------------
 * IMPROVEMENT ADDED: GAME POINTS SYSTEM 🏆
 * --------------------------------------------------------------
 * - Each valid word now gives points based on word length.
 * - 1 point per letter in the word.
 * - BONUS: +3 points if the word uses all 4 letters.
 * - Total score is shown after every correct word.
 * - Added calculateScore() method and scoring print statements.
 * --------------------------------------------------------------
 *
 * Commands: new, rules, quit
 */
public class ScrabbleGame {

    // Game settings
    private static final int LETTER_COUNT = 4;

    // Sorted dictionary for binary search
    private static final List<Word> DICTIONARY = new ArrayList<>();
    private static final Random RNG = new Random();

    // Letter pools (vowel bias)
    private static final char[] VOWELS = {'A', 'E', 'I', 'O', 'U'};
    private static final char[] CONSONANTS = {
        'B','C','D','F','G','H','J','K','L','M','N','P','Q','R','S','T','V','W','X','Y','Z'
    };

    public static void main(String[] args) {
        if (!loadDictionary("wordsWithDefs.txt")) {
            System.err.println("Could not find wordsWithDefs.txt in this folder.");
            return;
        }

        System.out.println("\nDictionary loaded successfully!\n");
        printRules();

        char[] letters = pickRandomLetters(LETTER_COUNT);

        // --- IMPROVEMENT FEATURE: total game points tracker ---
        int totalScore = 0;

        try (Scanner stdin = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                System.out.println("\nYour letters: " + formatLetters(letters));
                System.out.print("Enter a word (or 'new', 'rules', 'quit'): ");
                String line = stdin.hasNextLine() ? stdin.nextLine().trim() : "";
                if (line.isEmpty()) continue;

                String cmd = line;

                // Quit game
                if (cmd.equalsIgnoreCase("quit") || cmd.equalsIgnoreCase("exit")) {
                    System.out.println("Goodbye! Final score: " + totalScore);
                    running = false;
                    break;
                }

                // Show rules
                if (cmd.equalsIgnoreCase("rules")) {
                    printRules();
                    continue;
                }

                // Get new letters
                if (cmd.equalsIgnoreCase("new")) {
                    letters = pickRandomLetters(LETTER_COUNT);
                    System.out.println("New letters ready!");
                    continue;
                }

                // Check letter validity
                if (!usesOnlyLetters(cmd, letters)) {
                    System.out.println("'" + cmd + "' can't be made with those letters.");
                    continue;
                }

                // Hand-written binary search
                int idx = binarySearchWord(DICTIONARY, cmd);
                if (idx >= 0) {
                    Word found = DICTIONARY.get(idx);
                    System.out.println("✅ Valid word: " + found.getWord());
                    if (!found.getDefinition().isEmpty()) {
                        System.out.println("Definition: " + found.getDefinition());
                    }

                    // --- IMPROVEMENT FEATURE: Scoring system applied here ---
                    int score = calculateScore(found.getWord());
                    totalScore += score;
                    System.out.println("You earned " + score + " points! Total: " + totalScore);
                    // -------------------------------------------------------

                    System.out.print("Type 'new' for new letters, or press Enter to keep the same ones: ");
                    String after = stdin.hasNextLine() ? stdin.nextLine().trim() : "";
                    if (after.equalsIgnoreCase("new")) {
                        letters = pickRandomLetters(LETTER_COUNT);
                        System.out.println("New letters ready!");
                    }
                } else {
                    System.out.println("❌ '" + cmd + "' not found in dictionary.");
                }
            }
        }
    }

    // Load dictionary into memory
    private static boolean loadDictionary(String fileName) {
        File f = new File(fileName);
        if (!f.exists()) return false;

        Set<String> seen = new HashSet<>();
        Pattern split = Pattern.compile("\\s{2,}");

        try (Scanner in = new Scanner(f)) {
            while (in.hasNextLine()) {
                String line = in.nextLine().trim();
                if (line.isEmpty()) continue;

                String word, def = "";
                int tab = line.indexOf('\t');
                if (tab >= 0) {
                    word = line.substring(0, tab).trim();
                    def = line.substring(tab + 1).trim();
                } else {
                    String[] parts = split.split(line, 2);
                    word = parts[0].trim();
                    if (parts.length == 2) def = parts[1].trim();
                }

                if (word.isEmpty()) continue;
                if (seen.add(word.toLowerCase())) DICTIONARY.add(new Word(word, def));
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return false;
        }

        Collections.sort(DICTIONARY);
        System.out.println("Loaded " + DICTIONARY.size() + " words.");
        return true;
    }

    /**
     * --- IMPROVEMENT FEATURE ---
     * Calculates the player’s score for a valid word:
     * - 1 point per letter
     * - +3 bonus if all 4 letters are used
     */
    private static int calculateScore(String word) {
        if (word == null) return 0;
        int len = 0;
        for (char c : word.toCharArray()) if (Character.isLetter(c)) len++;
        int score = len;
        if (len == LETTER_COUNT) score += 3; // full-use bonus
        return score;
    }

    // Generate random letters (50% vowels)
    private static char[] pickRandomLetters(int n) {
        char[] out = new char[n];
        for (int i = 0; i < n; i++) {
            out[i] = RNG.nextBoolean()
                    ? VOWELS[RNG.nextInt(VOWELS.length)]
                    : CONSONANTS[RNG.nextInt(CONSONANTS.length)];
        }
        return out;
    }

    private static String formatLetters(char[] letters) {
        StringBuilder sb = new StringBuilder();
        for (char c : letters) sb.append(c).append(' ');
        return sb.toString().trim();
    }

    // Check if user word can be made from current letters
    private static boolean usesOnlyLetters(String candidate, char[] pool) {
        int[] count = new int[26];
        for (char c : pool)
            if (Character.isLetter(c)) count[Character.toUpperCase(c) - 'A']++;
        for (char c : candidate.toCharArray()) {
            if (!Character.isLetter(c)) return false;
            int i = Character.toUpperCase(c) - 'A';
            if (i < 0 || i >= 26 || count[i] == 0) return false;
            count[i]--;
        }
        return true;
    }

    // Hand-written binary search
    private static int binarySearchWord(List<Word> list, String target) {
        if (target == null) return -1;
        target = target.trim();
        if (target.isEmpty()) return -1;

        int lo = 0, hi = list.size() - 1;
        while (lo <= hi) {
            int mid = (lo + hi) >>> 1;
            Word midW = list.get(mid);
            String midWord = (midW == null) ? "" : midW.getWord();

            int cmp = midWord.compareToIgnoreCase(target);
            if (cmp == 0) return mid;
            if (cmp < 0) lo = mid + 1; else hi = mid - 1;
        }
        return -1;
    }

    // Game instructions
    private static void printRules() {
        System.out.println("\n--- How to Play (4 Letters) ---");
        System.out.println("- You’ll get a random set of 4 letters (more vowels to help).");
        System.out.println("- Make a real English word using only those letters.");
        System.out.println("- Scoring: 1 point per letter.");
        System.out.println("- Bonus: +3 points if you use all 4 letters.");
        System.out.println();
        System.out.println("Commands:");
        System.out.println("  new    → new random letters");
        System.out.println("  rules  → show these instructions again");
        System.out.println("  quit   → end the game and show your final score");
        System.out.println();
        System.out.println("Press Enter (no input) to keep your current letters and try another word.\n");
    }
}
