package com.chatanalyzer.chatanalyzer.model;

import java.time.LocalDate;

public class ChatMessage {
    private LocalDate date;
    private String time;
    private String author;
    private String text;
    private boolean mediaMessage;

    // Default constructor
    public ChatMessage() {}

    // Constructor with all fields
    public ChatMessage(LocalDate date, String time, String author, String text, boolean mediaMessage) {
        this.date = date;
        this.time = time;
        this.author = author;
        this.text = text;
        this.mediaMessage = mediaMessage;
    }

    // Getters and Setters
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isMediaMessage() {
        return mediaMessage;
    }

    public void setMediaMessage(boolean mediaMessage) {
        this.mediaMessage = mediaMessage;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "date=" + date +
                ", time='" + time + '\'' +
                ", author='" + author + '\'' +
                ", text='" + text + '\'' +
                ", mediaMessage=" + mediaMessage +
                '}';
    }
}