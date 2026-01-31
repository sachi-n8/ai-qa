package com.psi.ai_qa.interfaces.document;

import com.psi.ai_qa.common.constants.Endpoints;
import com.psi.ai_qa.common.util.JwtUtil;
import com.psi.ai_qa.interfaces.document.model.DocumentDashboardResponse;
import com.psi.ai_qa.interfaces.document.model.DocumentResponse;
import com.psi.ai_qa.interfaces.document.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Endpoints.AI_QA)
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final JwtUtil jwtUtil;

    @GetMapping(Endpoints.DOCUMENT)
    public DocumentDashboardResponse listDocuments(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        String userEmail = jwtUtil.extractEmail(token);

        return documentService.listDocuments(userEmail);
    }
}
