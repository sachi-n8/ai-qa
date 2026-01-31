package com.psi.ai_qa.interfaces.transcribe;

import com.psi.ai_qa.common.constants.Endpoints;
import com.psi.ai_qa.interfaces.transcribe.model.TranscriptionResponse;
import com.psi.ai_qa.interfaces.transcribe.service.TranscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Endpoints.AI_QA)
@RequiredArgsConstructor
public class TranscriptionController {

    private final TranscriptionService service;

    @PostMapping(Endpoints.TRANSCRIBE)
    public ResponseEntity<TranscriptionResponse> transcribe(
            @PathVariable String documentId) {

        service.transcribe(documentId);

        TranscriptionResponse res = new TranscriptionResponse();
        res.setDocumentId(documentId);
        res.setStatus("TRANSCRIBED");

        return ResponseEntity.ok(res);
    }
}