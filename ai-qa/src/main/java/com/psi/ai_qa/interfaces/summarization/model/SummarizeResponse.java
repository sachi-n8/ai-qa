package com.psi.ai_qa.interfaces.summarization.model;

import lombok.Data;

@Data
public class SummarizeResponse {
    private String documentId;
    private String summary;
}
