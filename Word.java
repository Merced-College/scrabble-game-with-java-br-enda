import java.util.Locale;
import java.util.Objects;

/**
 * Word class for the dictionary.
 * Each word has an optional definition.
 * Compare and match words without case sensitivity.
 */
public class Word implements Comparable<Word> {
    private String word;
    private String definition;

    public Word() {
        this.word = "none";
        this.definition = "none";
    }

    public Word(String word, String definition) {
        this.word = clean(word);
        this.definition = clean(definition);
    }

    public String getWord() { return word; }
    public void setWord(String word) { this.word = clean(word); }

    public String getDefinition() { return definition; }
    public void setDefinition(String definition) { this.definition = clean(definition); }

    @Override
    public String toString() {
        return word + (definition.isEmpty() ? "" : ": " + definition);
    }

    // Sort alphabetically (ignore case)
    @Override
    public int compareTo(Word other) {
        if (other == null) return 1;
        return this.word.compareToIgnoreCase(other.word);
    }

    // Words are equal if text matches (ignore case)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Word)) return false;
        Word other = (Word) o;
        return word.equalsIgnoreCase(other.word);
    }

    // Hash code also ignores case
    @Override
    public int hashCode() {
        return Objects.hash(word.toLowerCase(Locale.ROOT));
    }

    private static String clean(String s) {
        return (s == null) ? "" : s.trim();
    }
}
