package com.psi.ai_qa.interfaces.chat.model;

import lombok.Data;

@Data
public class ChatRequest {
    private String documentId;
    private String question;
}