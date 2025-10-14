public class Word implements Comparable<Word>{
    private String word;
    private String definition;

    public Word() {
        this.word = "none";
        this.definition = "none";
    }

    public Word(final String word, String definition) {
        this.word = word;
        this.definition = definition;
    }

    // getters and setters
    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    @Override
    public String toString() {
        return word + ": " + definition;
    }

    @Override
    public int compareTo(Word other) {
        if (other == null) return 1;
        if (this.word == null && other.word == null) return 0;
        if (this.word == null) return -1;
        if (other.word == null) return 1;
        return this.word.compareToIgnoreCase(other.word);
    }
}
