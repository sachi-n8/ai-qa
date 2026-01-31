package com.psi.ai_qa.interfaces.chat.model;

import lombok.Data;

import java.util.List;

@Data
public class ChatResponse {
    private String answer;
    private List<Reference> references;

    @Data
    public static class Reference {
        private int startChar;
        private int endChar;
    }
}