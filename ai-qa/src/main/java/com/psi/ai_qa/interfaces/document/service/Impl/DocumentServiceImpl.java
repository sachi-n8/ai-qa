package com.psi.ai_qa.interfaces.document.service.Impl;

import com.psi.ai_qa.common.model.DocumentChunk;
import com.psi.ai_qa.common.repo.DocumentRepository;
import com.psi.ai_qa.interfaces.document.model.DocumentDashboardResponse;
import com.psi.ai_qa.interfaces.document.model.DocumentResponse;
import com.psi.ai_qa.interfaces.document.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository repository;

    @Override
    public DocumentDashboardResponse listDocuments(String userId) {

        var docs = repository.findByUserIdOrderByUploadTimeDesc(userId);

        if (docs.isEmpty()) {
            return new DocumentDashboardResponse(null, List.of());
        }

        // Latest uploaded document
        DocumentResponse latest = map(docs.get(0));

        // Remaining documents
        List<DocumentResponse> others = docs.stream()
                .skip(1)
                .map(this::map)
                .toList();

        return new DocumentDashboardResponse(latest, others);
    }

    private DocumentResponse map(DocumentChunk doc) {
        DocumentResponse res = new DocumentResponse();
        res.setId(doc.getId());
        res.setFileName(doc.getFileName());
        res.setCategory(doc.getCategory());
        res.setSummary(doc.getSummary());
        res.setUploadTime(doc.getUploadTime());
        return res;
    }
}
