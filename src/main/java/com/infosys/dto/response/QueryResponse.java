package com.infosys.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryResponse {
    private String message;
    private List<Map<String, Object>> data;
    private String sqlQuery;
    private boolean success;
}
