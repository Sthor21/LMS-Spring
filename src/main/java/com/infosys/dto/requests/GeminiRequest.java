package com.infosys.dto.requests;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeminiRequest {
    private List<GeminiContent> contents;
    private GeminiGenerationConfig generationConfig;
    private List<GeminiSafetySetting> safetySettings;
}