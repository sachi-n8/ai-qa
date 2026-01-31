package com.psi.ai_qa.interfaces.transcribe.model;

import lombok.Data;

@Data
public class TranscriptionResponse {
    private String documentId;
    private String status;
    private int segmentCount;
}