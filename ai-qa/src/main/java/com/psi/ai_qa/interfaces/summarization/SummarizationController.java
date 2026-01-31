package com.psi.ai_qa.interfaces.summarization;

import com.psi.ai_qa.common.constants.Endpoints;
import com.psi.ai_qa.interfaces.summarization.model.SummarizeResponse;
import com.psi.ai_qa.interfaces.summarization.service.SummarizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Endpoints.AI_QA)
@RequiredArgsConstructor
public class SummarizationController {

    private final SummarizationService service;

    @PostMapping(Endpoints.SUMMARIZE)
    public ResponseEntity<SummarizeResponse> summarize(
            @PathVariable String documentId) {

        String summary = service.summarize(documentId);

        SummarizeResponse res = new SummarizeResponse();
        res.setDocumentId(documentId);
        res.setSummary(summary);

        return ResponseEntity.ok(res);
    }
}
