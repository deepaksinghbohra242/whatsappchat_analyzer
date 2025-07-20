package com.chatanalyzer.chatanalyzer.service;

import org.springframework.stereotype.Service;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class PythonAnalysisService {

    private static final Pattern EMOJI_PATTERN = Pattern.compile(
        "[\\x{1F600}-\\x{1F64F}]|[\\x{1F300}-\\x{1F5FF}]|[\\x{1F680}-\\x{1F6FF}]|[\\x{1F1E0}-\\x{1F1FF}]|[\\x{2600}-\\x{26FF}]|[\\x{2700}-\\x{27BF}]"
    );

    /**
     * Analyzes text and returns top words and emojis
     * This is a Java implementation that simulates Python text analysis
     */
    public Map<String, Object> analyzeText(String text) {
        Map<String, Object> results = new HashMap<>();
        
        try {
            // Analyze words
            List<List<Object>> topWords = getTopWords(text, 10);
            results.put("top_words", topWords);
            
            // Analyze emojis
            List<List<Object>> topEmojis = getTopEmojis(text, 10);
            results.put("top_emojis", topEmojis);
            
        } catch (Exception e) {
            System.err.println("Error in text analysis: " + e.getMessage());
            // Return empty lists on error
            results.put("top_words", new ArrayList<>());
            results.put("top_emojis", new ArrayList<>());
        }
        
        return results;
    }

    /**
     * Gets top words from text
     */
    private List<List<Object>> getTopWords(String text, int limit) {
        if (text == null || text.trim().isEmpty()) {
            return new ArrayList<>();
        }

        // Clean text and split into words
        String cleanText = text.toLowerCase()
                              .replaceAll("[^a-zA-Z\\s]", " ")
                              .replaceAll("\\s+", " ")
                              .trim();
        
        if (cleanText.isEmpty()) {
            return new ArrayList<>();
        }

        // Count word frequencies
        Map<String, Integer> wordCount = new HashMap<>();
        String[] words = cleanText.split("\\s+");
        
        for (String word : words) {
            if (word.length() > 2 && !isStopWord(word)) { // Filter out short words and stop words
                wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
            }
        }

        // Sort by frequency and return top words
        return wordCount.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> Arrays.<Object>asList(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * Gets top emojis from text
     */
    private List<List<Object>> getTopEmojis(String text, int limit) {
        if (text == null || text.trim().isEmpty()) {
            return new ArrayList<>();
        }

        Map<String, Integer> emojiCount = new HashMap<>();
        Matcher matcher = EMOJI_PATTERN.matcher(text);
        
        while (matcher.find()) {
            String emoji = matcher.group();
            emojiCount.put(emoji, emojiCount.getOrDefault(emoji, 0) + 1);
        }

        return emojiCount.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> Arrays.<Object>asList(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * Simple stop words filter
     */
    private boolean isStopWord(String word) {
        Set<String> stopWords = Set.of(
            "the", "and", "or", "but", "in", "on", "at", "to", "for", "of", "with", "by",
            "is", "are", "was", "were", "be", "been", "being", "have", "has", "had",
            "do", "does", "did", "will", "would", "could", "should", "may", "might",
            "a", "an", "this", "that", "these", "those", "i", "you", "he", "she", "it",
            "we", "they", "me", "him", "her", "us", "them", "my", "your", "his", "her",
            "its", "our", "their", "am", "can", "not", "no", "yes", "ok", "okay", "so"
        );
        return stopWords.contains(word.toLowerCase());
    }
}