/**
 * Simple model class representing a dictionary entry (word + optional definition).
 *
 * This class is immutable in spirit for the purposes of this small project, but
 * setters are provided to keep the original API. Comparison is case-insensitive
 * and is based on the word field so instances can be sorted and searched.
 */
public class Word implements Comparable<Word> {
    // stored word (may be null in unusual cases)
    private String word;
    // optional definition or additional data for the word
    private String definition;

    /**
     * Default constructor creates a placeholder entry.
     */
    public Word() {
        this.word = "none";
        this.definition = "none";
    }

    /**
     * Create a Word from a word string and an optional definition.
     *
     * @param word the word text (e.g. "APPLE")
     * @param definition human-readable definition (may be empty)
     */
    public Word(final String word, final String definition) {
        this.word = word;
        this.definition = definition;
    }

    // --- accessors ---
    public String getWord() {
        return word;
    }

    public void setWord(final String word) {
        this.word = word;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(final String definition) {
        this.definition = definition;
    }

    @Override
    public String toString() {
        return word + ": " + definition;
    }

    /**
     * Compare two Word objects case-insensitively by their word text.
     * Nulls are handled deterministically so sorting and searching are stable.
     */
    @Override
    public int compareTo(final Word other) {
        if (other == null) return 1; // non-null is greater than null
        if (this.word == null && other.word == null) return 0;
        if (this.word == null) return -1;
        if (other.word == null) return 1;
        return this.word.compareToIgnoreCase(other.word);
    }
}
