package com.sygep.ejb;

import com.sygep.dto.AIAnalysisResult;
import jakarta.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Stateless
public class AIService {

    public AIAnalysisResult analyze(String title, String text) {
        String safeTitle = title == null ? "" : title.trim();
        String safeText = text == null ? "" : text.trim();
        int wordCount = countWords(safeText);
        int score = 0;
        List<String> suggestions = new ArrayList<>();

        if (safeTitle.length() >= 12) {
            score += 15;
        } else {
            suggestions.add("Preciser le titre pour mieux annoncer le sujet et le contexte.");
            score += 5;
        }

        boolean lengthAcceptable = wordCount >= 80 && wordCount <= 700;
        if (lengthAcceptable) {
            score += 35;
        } else if (wordCount >= 40) {
            score += 20;
            suggestions.add("Allonger le resume avec la problematique, la methode et les resultats attendus.");
        } else {
            score += 5;
            suggestions.add("Le texte est trop court pour une evaluation academique fiable.");
        }

        String lowerText = safeText.toLowerCase(Locale.ROOT);
        score += scoreKeyword(lowerText, "problematique", "Ajouter clairement la problematique.", suggestions);
        score += scoreKeyword(lowerText, "objectif", "Ajouter les objectifs du projet.", suggestions);
        score += scoreKeyword(lowerText, "method", "Decrire la methodologie ou l'approche technique.", suggestions);
        score += scoreKeyword(lowerText, "resultat", "Expliquer les resultats attendus.", suggestions);
        score += scoreKeyword(lowerText, "innovation", "Mettre en avant l'innovation ou la valeur ajoutee.", suggestions);

        int sentenceCount = countSentences(safeText);
        if (sentenceCount > 0 && wordCount / sentenceCount <= 35) {
            score += 15;
        } else {
            suggestions.add("Raccourcir certaines phrases pour ameliorer la lisibilite.");
            score += 5;
        }

        if (safeText.length() > 0 && safeText.length() <= 6000) {
            score += 10;
        }

        if (suggestions.isEmpty()) {
            suggestions.add("Le texte est structure. Relire une derniere fois les objectifs et les livrables.");
        }

        return new AIAnalysisResult(Math.min(score, 100), wordCount, lengthAcceptable, suggestions);
    }

    private int scoreKeyword(String text, String keyword, String suggestion, List<String> suggestions) {
        if (text.contains(keyword)) {
            return 5;
        }
        suggestions.add(suggestion);
        return 0;
    }

    private int countWords(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        return text.trim().split("\\s+").length;
    }

    private int countSentences(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        return Math.max(1, text.split("[.!?]+").length);
    }
}
