package com.infosys.dto.requests;

import com.infosys.dto.requests.GeminiPart;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeminiContent {
    private String role = "user";
    private List<GeminiPart> parts;
}
