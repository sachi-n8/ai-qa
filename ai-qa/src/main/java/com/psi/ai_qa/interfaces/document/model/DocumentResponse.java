package com.psi.ai_qa.interfaces.document.model;

import lombok.Data;

@Data
public class DocumentResponse {
    private String id;
    private String fileName;
    private String category;
    private String summary;
    private Long uploadTime;
}
