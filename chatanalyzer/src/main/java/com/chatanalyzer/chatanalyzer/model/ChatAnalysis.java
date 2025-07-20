package com.chatanalyzer.chatanalyzer.model;

import java.util.List;
import java.util.Map;

public class ChatAnalysis {
    private int totalMessages;
    private int totalWords;
    private int mediaMessages;
    private Map<String, Integer> userMessageCounts;
    private String mostActiveUser;
    private int mostActiveUserCount;
    private Map<String, Integer> timeline;
    private List<List<Object>> topWords;
    private List<List<Object>> topEmojis;

    // Default constructor
    public ChatAnalysis() {}

    // Getters and Setters
    public int getTotalMessages() {
        return totalMessages;
    }

    public void setTotalMessages(int totalMessages) {
        this.totalMessages = totalMessages;
    }

    public int getTotalWords() {
        return totalWords;
    }

    public void setTotalWords(int totalWords) {
        this.totalWords = totalWords;
    }

    public int getMediaMessages() {
        return mediaMessages;
    }

    public void setMediaMessages(int mediaMessages) {
        this.mediaMessages = mediaMessages;
    }

    public Map<String, Integer> getUserMessageCounts() {
        return userMessageCounts;
    }

    public void setUserMessageCounts(Map<String, Integer> userMessageCounts) {
        this.userMessageCounts = userMessageCounts;
    }

    public String getMostActiveUser() {
        return mostActiveUser;
    }

    public void setMostActiveUser(String mostActiveUser) {
        this.mostActiveUser = mostActiveUser;
    }

    public int getMostActiveUserCount() {
        return mostActiveUserCount;
    }

    public void setMostActiveUserCount(int mostActiveUserCount) {
        this.mostActiveUserCount = mostActiveUserCount;
    }

    public Map<String, Integer> getTimeline() {
        return timeline;
    }

    public void setTimeline(Map<String, Integer> timeline) {
        this.timeline = timeline;
    }

    public List<List<Object>> getTopWords() {
        return topWords;
    }

    public void setTopWords(List<List<Object>> topWords) {
        this.topWords = topWords;
    }

    public List<List<Object>> getTopEmojis() {
        return topEmojis;
    }

    public void setTopEmojis(List<List<Object>> topEmojis) {
        this.topEmojis = topEmojis;
    }

    @Override
    public String toString() {
        return "ChatAnalysis{" +
                "totalMessages=" + totalMessages +
                ", totalWords=" + totalWords +
                ", mediaMessages=" + mediaMessages +
                ", userMessageCounts=" + userMessageCounts +
                ", mostActiveUser='" + mostActiveUser + '\'' +
                ", mostActiveUserCount=" + mostActiveUserCount +
                ", timeline=" + timeline +
                ", topWords=" + topWords +
                ", topEmojis=" + topEmojis +
                '}';
    }
}