package com.psi.ai_qa.interfaces.upload.service;

import com.psi.ai_qa.common.model.DocumentChunk;
import org.springframework.web.multipart.MultipartFile;

public interface UploadService {
    DocumentChunk upload(MultipartFile file, String userId) throws Exception;

    void triggerTranscription(String documentId);
}
