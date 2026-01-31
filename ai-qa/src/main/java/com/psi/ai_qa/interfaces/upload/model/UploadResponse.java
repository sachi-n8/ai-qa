package com.psi.ai_qa.interfaces.upload.model;

import lombok.Data;

@Data
public class UploadResponse {

    private String documentID;
    private String fileName;
    private String status;
}
