package com.infosys.controller;

import com.infosys.dto.requests.*;
import com.infosys.dto.response.QueryResponse;
import com.infosys.services.DatabaseSchemaService;
import com.infosys.services.GeminiQueryService;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/chat")
public class GeminiChatController {

    private final GeminiQueryService queryService;
    private final DatabaseSchemaService schemaService;

    public GeminiChatController(GeminiQueryService queryService,
                                DatabaseSchemaService schemaService) {
        this.queryService = queryService;
        this.schemaService = schemaService;
    }

    @PostMapping("/query")
    public ResponseEntity<QueryResponse> processQuery(@RequestBody ChatRequest request) {
        try {
            QueryResponse response = queryService.processNaturalLanguageQuery(request.getMessage());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            QueryResponse errorResponse = new QueryResponse(
                    "I encountered an error processing your request. Please try again with a different phrasing.",
                    null, null, false
            );
            return ResponseEntity.ok(errorResponse);
        }
    }

    @GetMapping("/examples")
    public ResponseEntity<List<QueryExample>> getQueryExamples() {
        List<QueryExample> examples = Arrays.asList(
                new QueryExample("Show me all overdue books",
                        "Find books that are past their due date and haven't been returned yet"),
                new QueryExample("Find books by Stephen King",
                        "Search for all books written by a specific author"),
                new QueryExample("Which users have the most books checked out?",
                        "Show users ranked by number of active loans"),
                new QueryExample("What are the most popular books this month?",
                        "List books with the most loans in the current month"),
                new QueryExample("Show me recent book additions",
                        "Display books that were recently added to the library"),
                new QueryExample("Find all mystery books",
                        "Search books by category or genre"),
                new QueryExample("Who currently has 'The Great Gatsby'?",
                        "Find which user has a specific book checked out"),
                new QueryExample("Show users with outstanding fines",
                        "List users who owe money for late returns"),
                new QueryExample("What books are available right now?",
                        "Show books that can be borrowed immediately"),
                new QueryExample("How many books were borrowed last week?",
                        "Get statistics on recent borrowing activity"),
                new QueryExample("Find books that have never been borrowed",
                        "Identify books that might need promotion"),
                new QueryExample("Show all reservations for tomorrow",
                        "View upcoming book pickups")
        );
        return ResponseEntity.ok(examples);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "healthy");
        status.put("ai_service", "gemini");
        status.put("timestamp", new Date().toString());
        return ResponseEntity.ok(status);
    }
}
