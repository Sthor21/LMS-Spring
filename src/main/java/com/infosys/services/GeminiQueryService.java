package com.infosys.services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import com.infosys.dto.requests.QueryResult;
import com.infosys.dto.response.QueryResponse;


import java.util.List;
import java.util.Map;

@Service
public class GeminiQueryService {

    private final GeminiService geminiService;
    private final JdbcTemplate jdbcTemplate;
    private final DatabaseSchemaService schemaService;
    private final Logger logger = LoggerFactory.getLogger(GeminiQueryService.class);

    public GeminiQueryService(GeminiService geminiService,
                              JdbcTemplate jdbcTemplate,
                              DatabaseSchemaService schemaService) {
        this.geminiService = geminiService;
        this.jdbcTemplate = jdbcTemplate;
        this.schemaService = schemaService;
    }

    public QueryResponse processNaturalLanguageQuery(String userQuery) {
        if (userQuery == null || userQuery.trim().isEmpty()) {
            logger.error("Received an empty or null user query from the controller.");
            return new QueryResponse(
                    "I didn't receive a question. Please check the request body and try again.",
                    null, null, false
            );
        }

        try {
            String sqlQuery = generateSQLQuery(userQuery);
            validateQuerySafety(sqlQuery);
            QueryResult result = executeQuery(sqlQuery);
            String response = generateHumanResponse(userQuery, result);
            return new QueryResponse(response, result.getData(), sqlQuery, true);
        } catch (Exception e) {
            logger.error("Error processing query: " + userQuery, e);
            return new QueryResponse(
                    "I couldn't process that request. Please try rephrasing your question.",
                    null, null, false
            );
        }
    }

    private String generateSQLQuery(String userQuery) {
        String prompt = String.format("""
            You are a PostgreSQL expert for a library management system. Your task is to convert a natural language question from a librarian into a single, valid, and secure PostgreSQL query.
        
            DATABASE SCHEMA:
            ----------------
            Table: books
            - id (INTEGER, PRIMARY KEY)
            - title (TEXT)
            - author (TEXT)
            - description (TEXT)
            - publisher (TEXT)
            - page_count (INTEGER)
            - genres (TEXT)
            - isbn (VARCHAR)
            - language (VARCHAR)
            - published_date (DATE)
            - total_copies (INTEGER)
            - available_copies (INTEGER)
            - borrowed_copies (INTEGER)
        
            Table: users
            - id (INTEGER, PRIMARY KEY)
            - created_at (TIMESTAMP)
            - email (VARCHAR, UNIQUE)
            - first_name (VARCHAR)
            - last_name (VARCHAR)
            - password (VARCHAR)
            - phone_number (VARCHAR, UNIQUE)
            - role (ENUM: 'ADMIN', 'LIBRARIAN', 'MEMBER')
            - updated_at (TIMESTAMP)
        
            Table: borrow_records
            - id (INTEGER, PRIMARY KEY)
            - borrow_date (DATE)
            - due_date (DATE)
            - fine_amount (NUMERIC)
            - return_date (DATE)
            - status (ENUM: 'ACTIVE', 'RETURNED', 'OVERDUE', 'LOST')
            - book_id (INTEGER, FOREIGN KEY -> books.id)
            - user_id (INTEGER, FOREIGN KEY -> users.id)
        
            RELATIONSHIPS:
            - borrow_records.book_id references books.id
            - borrow_records.user_id references users.id
        
            IMPORTANT RULES & GUIDELINES:
            -----------------------------
            1. **SECURITY:** ONLY generate SELECT queries. Never generate INSERT, UPDATE, DELETE, DROP, or any other DDL/DML statements.
            2. **DIALECT:** The database is PostgreSQL. Use PostgreSQL-specific syntax (e.g., for dates, use CURRENT_DATE).
            3. **CASE-INSENSITIVE SEARCH:** For searching text fields like title, author, or user names, always use the `LOWER()` function on both the column and the search term (e.g., `LOWER(b.title) LIKE LOWER('%%the hobbit%%')`).
            4. **ALIASES:** Use clear and concise table aliases (e.g., `b` for books, `u` for users, `br` for borrow_records) to improve query readability.
            5. **OUTPUT FORMAT:** Return ONLY the raw SQL query. Do not include any explanations, markdown formatting, or semicolons.
            6. **SELECTION CRITERIA:** Query only thr required columns from the table dont include un necessary column.
        
            COMMON QUERY PATTERNS:
            ----------------------
            - "overdue books": These are records where `br.status = 'OVERDUE'`.
            - "available books": These are books where `b.available_copies > 0`.
            - "active loans": These are records where `br.status = 'ACTIVE'`.
            - "books borrowed by [user name]": This requires a JOIN between `users` and `borrow_records`. Match `LOWER(CONCAT(u.first_name, ' ', u.last_name))`.
            - "who has [book title]": This requires a JOIN between `books`, `borrow_records`, and `users` where `br.status = 'ACTIVE'`.
            - "popular books": Count borrow records per book: `JOIN books b ON br.book_id = b.id`, then `GROUP BY b.title` and `ORDER BY COUNT(br.id) DESC`.
        
            User Query: "%s"
        
            SQL Query:
            """, userQuery);

        logger.info("user query = ",userQuery);

        String sqlQuery = geminiService.generateContent(prompt, 0.1, 500);

        logger.info("Raw response from Gemini: '{}'", sqlQuery);

        return sqlQuery.replaceAll("```sql", "").replaceAll("```", "").replaceAll(";", "").trim();
    }

    private QueryResult executeQuery(String sqlQuery) {
        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sqlQuery);
            return new QueryResult(rows, null);
        } catch (Exception e) {
            throw new RuntimeException("Query execution failed: " + e.getMessage(), e);
        }
    }

    private void validateQuerySafety(String query) {
        String upperQuery = query.toUpperCase().trim();
        if (!upperQuery.startsWith("SELECT")) {
            throw new SecurityException("Only SELECT queries are allowed.");
        }
        String[] dangerousKeywords = { "INSERT", "UPDATE", "DELETE", "DROP", "CREATE", "ALTER", "TRUNCATE", "EXEC", "GRANT", "REVOKE" };
        for (String keyword : dangerousKeywords) {
            if (upperQuery.contains(keyword)) {
                throw new SecurityException("Query contains dangerous operation: " + keyword);
            }
        }
    }

    private String generateHumanResponse(String userQuery, QueryResult result) {
        String prompt = String.format("""
                You are a friendly librarian. Generate a natural, conversational response for a query result.
                
                The user asked: "%s"
                We found %d results.
                
                Guidelines:
                - Be conversational and helpful.
                - Mention the number of results found.
                - If no results are found, suggest rephrasing the question.
                - Keep it concise (2-3 sentences).
                - Do NOT mention technical details like SQL.
                - Generate only the response message.
                """,
                userQuery,
                result.getData() != null ? result.getData().size() : 0
        );
        return geminiService.generateContent(prompt, 0.7, 150);
    }
}
