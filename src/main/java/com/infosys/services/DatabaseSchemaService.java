package com.infosys.services;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DatabaseSchemaService {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseSchemaService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String getSchemaInformation() {
        return """
            DATABASE SCHEMA (PostgreSQL):
            
            Table: books
            - id (BIGINT, PRIMARY KEY)
            - title (VARCHAR)
            - author (VARCHAR)
            - isbn (VARCHAR)
            - publication_year (INT)
            - category (VARCHAR)
            - total_copies (INT)
            - available_copies (INT)
            
            Table: users
            - id (BIGINT, PRIMARY KEY)
            - name (VARCHAR)
            - email (VARCHAR, UNIQUE)
            - membership_type (ENUM: 'STUDENT', 'FACULTY', 'PUBLIC')
            
            Table: book_loans
            - id (BIGINT, PRIMARY KEY)
            - book_id (BIGINT, FOREIGN KEY -> books.id)
            - user_id (BIGINT, FOREIGN KEY -> users.id)
            - loan_date (DATE)
            - due_date (DATE)
            - return_date (DATE, NULL if not returned)
            - status (ENUM: 'ACTIVE', 'RETURNED', 'OVERDUE')
            
            RELATIONSHIPS:
            - book_loans.book_id → books.id
            - book_loans.user_id → users.id
            
            BUSINESS RULES:
            - Books are overdue if return_date IS NULL and due_date < CURRENT_DATE.
            - Available copies = total_copies - (count of active loans for that book).
            """;
    }

    public List<String> getTableNames() {
        String sql = "SELECT table_name FROM information_schema.tables WHERE table_schema = DATABASE()";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    public List<Map<String, Object>> getTableStructure(String tableName) {
        String sql = "SELECT column_name, data_type, is_nullable FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = ?";
        return jdbcTemplate.queryForList(sql, tableName);
    }
}
