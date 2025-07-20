package com.chatanalyzer.chatanalyzer.controller;

import com.chatanalyzer.chatanalyzer.model.ChatAnalysis;
import com.chatanalyzer.chatanalyzer.service.ChatAnalyzerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ChatAnalyzerController {

    @Autowired
    private ChatAnalyzerService chatAnalyzerService;

    // File upload endpoint
    @PostMapping(value = "/analyze", consumes = "multipart/form-data")
    public ResponseEntity<?> analyzeChatFile(@RequestParam("chatFile") MultipartFile file) {
        try {
            // Validate file
            if (file.isEmpty()) {
                return createErrorResponse("File is empty", HttpStatus.BAD_REQUEST);
            }

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".txt")) {
                return createErrorResponse("Only .txt files are allowed", HttpStatus.BAD_REQUEST);
            }

            if (file.getSize() > 10 * 1024 * 1024) { // 10MB limit
                return createErrorResponse("File size exceeds 10MB limit", HttpStatus.BAD_REQUEST);
            }

            // Read file content
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);

            if (content.trim().isEmpty()) {
                return createErrorResponse("File content is empty", HttpStatus.BAD_REQUEST);
            }

            // Analyze chat
            ChatAnalysis analysis = chatAnalyzerService.analyzeChat(content);
            return ResponseEntity.ok(analysis);
            
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error: " + e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            System.err.println("Service error: " + e.getMessage());
            return createErrorResponse("Error analyzing chat: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return createErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Text content endpoint (for testing with raw text)
    @PostMapping(value = "/analyze/text", consumes = "application/json")
    public ResponseEntity<?> analyzeChatText(@RequestBody Map<String, String> request) {
        try {
            String content = request.get("content");
            if (content == null || content.trim().isEmpty()) {
                return createErrorResponse("Content cannot be empty", HttpStatus.BAD_REQUEST);
            }

            // Analyze chat
            ChatAnalysis analysis = chatAnalyzerService.analyzeChat(content);
            return ResponseEntity.ok(analysis);
            
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error: " + e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            System.err.println("Service error: " + e.getMessage());
            return createErrorResponse("Error analyzing chat: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return createErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Alternative file upload endpoint with optional parameter
    @PostMapping("/analyze/upload")
    public ResponseEntity<?> analyzeChatUpload(
            @RequestParam(value = "chatFile", required = false) MultipartFile file,
            @RequestParam(value = "content", required = false) String textContent) {
        
        try {
            String content = null;
            
            // Check if file was provided
            if (file != null && !file.isEmpty()) {
                String originalFilename = file.getOriginalFilename();
                if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".txt")) {
                    return createErrorResponse("Only .txt files are allowed", HttpStatus.BAD_REQUEST);
                }
                
                if (file.getSize() > 10 * 1024 * 1024) { // 10MB limit
                    return createErrorResponse("File size exceeds 10MB limit", HttpStatus.BAD_REQUEST);
                }
                
                content = new String(file.getBytes(), StandardCharsets.UTF_8);
            } 
            // Check if text content was provided
            else if (textContent != null && !textContent.trim().isEmpty()) {
                content = textContent;
            } 
            // Neither file nor text provided
            else {
                return createErrorResponse("Either chatFile or content parameter must be provided", HttpStatus.BAD_REQUEST);
            }

            if (content.trim().isEmpty()) {
                return createErrorResponse("Content cannot be empty", HttpStatus.BAD_REQUEST);
            }

            // Analyze chat
            ChatAnalysis analysis = chatAnalyzerService.analyzeChat(content);
            return ResponseEntity.ok(analysis);
            
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error: " + e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            System.err.println("Service error: " + e.getMessage());
            return createErrorResponse("Error analyzing chat: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return createErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Chat Analyzer service is running");
        response.put("timestamp", java.time.Instant.now().toString());
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Map<String, String>> createErrorResponse(String message, HttpStatus status) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", message);
        errorResponse.put("status", status.toString());
        errorResponse.put("timestamp", java.time.Instant.now().toString());
        return ResponseEntity.status(status).body(errorResponse);
    }
}