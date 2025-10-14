import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ScrabbleGame {

    // data structure to hold the dictionary of words
    private static List<Word> dictionary = new ArrayList<Word>();

    public static void main(String[] args) {

        // read in the text file of words and definitions into the arraylist
        File f = new File("wordsWithDefs.txt");
        if (!f.exists()) {
            System.err.println("Dictionary file wordsWithDefs.txt not found in working directory.");
            import java.io.File;
            import java.io.File;
            import java.io.FileNotFoundException;
            import java.util.ArrayList;
            import java.util.Collections;
            import java.util.HashMap;
            import java.util.HashSet;
            import java.util.List;
            import java.util.Map;
            import java.util.Random;
            import java.util.Scanner;
            import java.util.Set;

            public class ScrabbleGame {

                // simplified English tile distribution (letters only)
                private static final String[] TILE_DISTRIBUTION = {
                    "E","E","E","E","E","E","E","E","E",
                    "A","A","A","A","A","A","A",
                    "O","O","O","O","O","O",
                    "I","I","I","I","I","I",
                    "N","N","N","N","N","N",
                    "R","R","R","R","R","R",
                    "T","T","T","T","T","T",
                    "L","L","L","L",
                    "S","S","S","S",
                    "U","U","U","U",
                    "D","D","D",
                    "G","G",
                    "B","B",
                    "C","C",
                    "M","M",
                    "P","P",
                    "F","F",
                    "H","H",
                    "V","V",
                    "W","W",
                    "Y","Y",
                    "K",
                    "J",
                    "X",
                    "Q",
                    "Z"
                };

                // simple Scrabble letter scores
                private static final Map<Character,Integer> SCORES = new HashMap<>();
                static {
                    String ones = "AEILNORSTU";
                    for (char c : ones.toCharArray()) SCORES.put(c, 1);
                    for (char c : "DG".toCharArray()) SCORES.put(c, 2);
                    for (char c : "BCMP".toCharArray()) SCORES.put(c, 3);
                    for (char c : "FHVWY".toCharArray()) SCORES.put(c, 4);
                    SCORES.put('K', 5);
                    SCORES.put('J', 8);
                    SCORES.put('X', 8);
                    SCORES.put('Q', 10);
                    SCORES.put('Z', 10);
                }

                private final List<String> tileBag = new ArrayList<>();
                private final List<List<String>> playerRacks = new ArrayList<>();
                private final Set<String> dictionaryWords = new HashSet<>();
                private final Random rng = new Random();

                public static void main(String[] args) {
                    new ScrabbleGame().run();
                }

                private void run() {
                    loadDictionary();
                    buildTileBag();
                    Collections.shuffle(tileBag, rng);

                    int numPlayers = 2;
                    int rackSize = 7;
                    for (int i = 0; i < numPlayers; i++) playerRacks.add(new ArrayList<>());

                    // deal initial racks
                    for (int p = 0; p < numPlayers; p++) refillRack(playerRacks.get(p), rackSize);

                    System.out.println("Simple Scrabble (CLI) — Two players. Commands: show, play <player> <word>, swap <player> <letters>, quit");
                    System.out.println("Type 'show' to see racks. Words are validated against the loaded dictionary (if present).\n");

                    Scanner in = new Scanner(System.in);
                    while (true) {
                        System.out.print("> ");
                        if (!in.hasNextLine()) break;
                        String line = in.nextLine().trim();
                        if (line.isEmpty()) continue;
                        String[] parts = line.split("\\s+", 3);
                        String cmd = parts[0].toLowerCase();
                        if (cmd.equals("quit") || cmd.equals("exit")) break;
                        if (cmd.equals("show")) { showState(); continue; }
                        if (cmd.equals("play") && parts.length >= 3) {
                            int player = parsePlayer(parts[1]);
                            if (player < 1 || player > playerRacks.size()) { System.out.println("Invalid player"); continue; }
                            String word = parts[2].toUpperCase();
                            playWord(player-1, word);
                            continue;
                        }
                        if (cmd.equals("swap") && parts.length >= 3) {
                            int player = parsePlayer(parts[1]);
                            if (player < 1 || player > playerRacks.size()) { System.out.println("Invalid player"); continue; }
                            String tiles = parts[2].toUpperCase();
                            swapTiles(player-1, tiles);
                            continue;
                        }
                        System.out.println("Unknown command");
                    }

                    System.out.println("Goodbye");
                }

                private int parsePlayer(String s) { try { return Integer.parseInt(s); } catch (NumberFormatException e) { return -1; } }

                private void showState() {
                    for (int i = 0; i < playerRacks.size(); i++) System.out.println("Player " + (i+1) + " rack: " + playerRacks.get(i));
                    System.out.println("Tiles remaining in bag: " + tileBag.size());
                }

                private void playWord(int playerIndex, String word) {
                    List<String> rack = playerRacks.get(playerIndex);
                    List<String> temp = new ArrayList<>(rack);
                    for (char ch : word.toCharArray()) { String s = String.valueOf(ch); if (!temp.remove(s)) { System.out.println("Player " + (playerIndex+1) + " does not have the letters for " + word); return; } }
                    if (!dictionaryWords.isEmpty() && !dictionaryWords.contains(word)) { System.out.println("Word not found in dictionary: " + word); return; }
                    int score = scoreWord(word);
                    System.out.println("Player " + (playerIndex+1) + " played '" + word + "' for " + score + " points.");
                    for (char ch : word.toCharArray()) rack.remove(String.valueOf(ch));
                    refillRack(rack, 7);
                }

                private void swapTiles(int playerIndex, String tiles) {
                    List<String> rack = playerRacks.get(playerIndex);
                    List<String> toSwap = new ArrayList<>();
                    for (char ch : tiles.toCharArray()) toSwap.add(String.valueOf(ch));
                    List<String> copy = new ArrayList<>(rack);
                    for (String t : toSwap) { if (!copy.remove(t)) { System.out.println("Player " + (playerIndex+1) + " does not have tile " + t); return; } }
                    for (String t : toSwap) { rack.remove(t); tileBag.add(t); }
                    Collections.shuffle(tileBag, rng);
                    refillRack(rack, 7);
                    System.out.println("Player " + (playerIndex+1) + " swapped " + toSwap.size() + " tiles.");
                }

                private int scoreWord(String word) { int sum = 0; for (char ch : word.toCharArray()) sum += SCORES.getOrDefault(ch, 0); return sum; }

                private void refillRack(List<String> rack, int rackSize) { while (rack.size() < rackSize && !tileBag.isEmpty()) rack.add(tileBag.remove(tileBag.size()-1)); }

                private void buildTileBag() { tileBag.clear(); for (String t : TILE_DISTRIBUTION) tileBag.add(t); }

                private void loadDictionary() {
                    File f = new File("wordsWithDefs.txt");
                    if (!f.exists()) { System.out.println("No dictionary file found; word validation will be skipped."); return; }
                    try (Scanner in = new Scanner(f)) {
                        while (in.hasNextLine()) {
                            String line = in.nextLine();
                            if (line.trim().isEmpty()) continue;
                            int tab = line.indexOf('\t');
                            String word;
                            if (tab >= 0) word = line.substring(0, tab).trim();
                            else { String[] parts = line.split("\\s{2,}", 2); word = parts[0].trim(); }
                            if (!word.isEmpty()) dictionaryWords.add(word.toUpperCase());
                        }
                    } catch (FileNotFoundException e) { System.out.println("Error reading dictionary: " + e.getMessage()); }
                    System.out.println("Loaded dictionary words: " + dictionaryWords.size());
                }
            }