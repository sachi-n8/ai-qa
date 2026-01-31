package com.psi.ai_qa.interfaces.upload;

import com.psi.ai_qa.common.constants.Endpoints;
import com.psi.ai_qa.common.enums.DocumentType;
import com.psi.ai_qa.common.enums.ResponseStatusAndMessage;
import com.psi.ai_qa.common.exception.WalletException;
import com.psi.ai_qa.common.model.DocumentChunk;
import com.psi.ai_qa.common.util.JwtUtil;
import com.psi.ai_qa.interfaces.upload.model.UploadResponse;
import com.psi.ai_qa.interfaces.upload.service.UploadService;
import com.psi.ai_qa.interfaces.upload.service.impl.DocumentProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(Endpoints.AI_QA)
@RequiredArgsConstructor
public class UploadController {

    private final UploadService service;
    private final JwtUtil jwtUtil;
    private final DocumentProcessingService documentProcessingService;

    @PostMapping(
            value = Endpoints.UPLOAD,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<UploadResponse> upload(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String authHeader) throws Exception {

        String token = authHeader.substring(7);
        String userEmail = jwtUtil.extractEmail(token);
         DocumentChunk doc = service.upload(file, userEmail);

        if (DocumentType.isMedia(doc.getType())) {
            service.triggerTranscription(doc.getId());
        } else if ("pdf".equalsIgnoreCase(doc.getType())) {
            documentProcessingService.processPdfAsync(doc.getId());
        }

        UploadResponse response = new UploadResponse();
        response.setDocumentID(doc.getId());
        response.setFileName(doc.getFileName());
        response.setStatus(String.valueOf(doc.getStatus()));

        return ResponseEntity.ok(response);
    }
}

