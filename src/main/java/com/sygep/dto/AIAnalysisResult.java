package com.sygep.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AIAnalysisResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int score;
    private final int wordCount;
    private final boolean lengthAcceptable;
    private final List<String> suggestions;

    public AIAnalysisResult(int score, int wordCount, boolean lengthAcceptable, List<String> suggestions) {
        this.score = score;
        this.wordCount = wordCount;
        this.lengthAcceptable = lengthAcceptable;
        this.suggestions = new ArrayList<>(suggestions);
    }

    public int getScore() {
        return score;
    }

    public int getWordCount() {
        return wordCount;
    }

    public boolean isLengthAcceptable() {
        return lengthAcceptable;
    }

    public List<String> getSuggestions() {
        return Collections.unmodifiableList(suggestions);
    }

    public String getSuggestionsText() {
        return String.join("\n", suggestions);
    }

    public String getQualityLabel() {
        if (score >= 80) {
            return "Tres bon";
        }
        if (score >= 60) {
            return "Correct";
        }
        if (score >= 40) {
            return "A renforcer";
        }
        return "Insuffisant";
    }
}
