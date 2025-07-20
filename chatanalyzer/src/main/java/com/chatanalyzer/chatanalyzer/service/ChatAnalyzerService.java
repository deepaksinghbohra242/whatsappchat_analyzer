package com.chatanalyzer.chatanalyzer.service;

import com.chatanalyzer.chatanalyzer.model.ChatAnalysis;
import com.chatanalyzer.chatanalyzer.model.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
public class ChatAnalyzerService {

    @Autowired
    private PythonAnalysisService pythonAnalysisService;

    private static final Pattern MESSAGE_PATTERN = Pattern.compile(
        "^(\\d{1,2}/\\d{1,2}/\\d{2,4}),?\\s+(\\d{1,2}:\\d{2}(?:\\s*(?:AM|PM|am|pm))?)\\s*-\\s*([^:]+):\\s*(.*)$"
    );

    public ChatAnalysis analyzeChat(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Chat content cannot be null or empty");
        }
        
        try {
            List<ChatMessage> messages = parseMessages(content);
            return buildAnalysis(messages);
        } catch (Exception e) {
            System.err.println("Error analyzing chat: " + e.getMessage());
            throw new RuntimeException("Failed to analyze chat content", e);
        }
    }

    private List<ChatMessage> parseMessages(String content) {
        List<ChatMessage> messages = new ArrayList<>();
        String[] lines = content.split("\n");
        
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            
            Matcher matcher = MESSAGE_PATTERN.matcher(line.trim());
            if (matcher.matches()) {
                try {
                    String dateStr = matcher.group(1);
                    String timeStr = matcher.group(2);
                    String author = matcher.group(3).trim();
                    String text = matcher.group(4).trim();
                    
                    // Parse date
                    LocalDate date = parseDate(dateStr);
                    
                    ChatMessage message = new ChatMessage();
                    message.setDate(date);
                    message.setTime(timeStr);
                    message.setAuthor(author);
                    message.setText(text);
                    message.setMediaMessage(isMediaMessage(text));
                    
                    messages.add(message);
                } catch (Exception e) {
                    // Skip malformed messages but log them
                    System.err.println("Failed to parse message: " + line + " - " + e.getMessage());
                }
            }
        }
        
        if (messages.isEmpty()) {
            throw new IllegalArgumentException("No valid chat messages found in the provided content");
        }
        
        return messages;
    }

    private LocalDate parseDate(String dateStr) {
        // Try different date formats
        String[] formats = {
            "M/d/yyyy", "MM/dd/yyyy", "d/M/yyyy", "dd/MM/yyyy",
            "M/d/yy", "MM/dd/yy", "d/M/yy", "dd/MM/yy"
        };
        
        for (String format : formats) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                return LocalDate.parse(dateStr, formatter);
            } catch (DateTimeParseException e) {
                // Try next format
            }
        }
        
        // Default to current date if parsing fails
        System.err.println("Could not parse date: " + dateStr + ", using current date");
        return LocalDate.now();
    }

    private boolean isMediaMessage(String text) {
        if (text == null) return false;
        
        return text.contains("<Media omitted>") || 
               text.contains("image omitted") || 
               text.contains("video omitted") || 
               text.contains("audio omitted") ||
               text.contains("document omitted") ||
               text.contains("sticker omitted");
    }

    private ChatAnalysis buildAnalysis(List<ChatMessage> messages) {
        ChatAnalysis analysis = new ChatAnalysis();
        
        try {
            // Basic statistics
            analysis.setTotalMessages(messages.size());
            
            // User message counts
            Map<String, Integer> userCounts = new HashMap<>();
            int totalWords = 0;
            int mediaCount = 0;
            Map<String, Integer> dailyMessageCount = new HashMap<>();
            
            for (ChatMessage message : messages) {
                // User counts
                String author = message.getAuthor();
                if (author != null && !author.isEmpty()) {
                    userCounts.put(author, userCounts.getOrDefault(author, 0) + 1);
                }
                
                // Word counts
                if (!message.isMediaMessage() && message.getText() != null && !message.getText().isEmpty()) {
                    String[] words = message.getText().split("\\s+");
                    totalWords += words.length;
                }
                
                // Media count
                if (message.isMediaMessage()) {
                    mediaCount++;
                }
                
                // Daily message count
                if (message.getDate() != null) {
                    String dateKey = message.getDate().toString();
                    dailyMessageCount.put(dateKey, dailyMessageCount.getOrDefault(dateKey, 0) + 1);
                }
            }
            
            analysis.setUserMessageCounts(userCounts);
            analysis.setTotalWords(totalWords);
            analysis.setMediaMessages(mediaCount);
            analysis.setTimeline(dailyMessageCount);
            
            // Find most active user
            if (!userCounts.isEmpty()) {
                Map.Entry<String, Integer> mostActive = userCounts.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .orElse(null);
                
                if (mostActive != null) {
                    analysis.setMostActiveUser(mostActive.getKey());
                    analysis.setMostActiveUserCount(mostActive.getValue());
                }
            }
            
            // Use Python service for advanced analysis
            try {
                String allText = messages.stream()
                    .filter(m -> m != null && !m.isMediaMessage() && m.getText() != null)
                    .map(ChatMessage::getText)
                    .reduce("", (a, b) -> a + " " + b);
                
                if (pythonAnalysisService != null) {
                    Map<String, Object> pythonResults = pythonAnalysisService.analyzeText(allText);
                    
                    if (pythonResults != null) {
                        analysis.setTopWords((List<List<Object>>) pythonResults.get("top_words"));
                        analysis.setTopEmojis((List<List<Object>>) pythonResults.get("top_emojis"));
                    } else {
                        setDefaultAnalysisResults(analysis);
                    }
                } else {
                    setDefaultAnalysisResults(analysis);
                }
            } catch (Exception e) {
                System.err.println("Python analysis failed: " + e.getMessage());
                setDefaultAnalysisResults(analysis);
            }
            
        } catch (Exception e) {
            System.err.println("Error building analysis: " + e.getMessage());
            throw new RuntimeException("Failed to build chat analysis", e);
        }
        
        return analysis;
    }

    private void setDefaultAnalysisResults(ChatAnalysis analysis) {
        if (analysis.getTopWords() == null) {
            analysis.setTopWords(new ArrayList<>());
        }
        if (analysis.getTopEmojis() == null) {
            analysis.setTopEmojis(new ArrayList<>());
        }
    }
}