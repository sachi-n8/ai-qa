package com.psi.ai_qa.interfaces.timestamp.model;

import lombok.Data;

@Data
public class TimestampRequest {
    private String documentId;
    private String question;
}