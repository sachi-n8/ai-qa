package com.psi.ai_qa.interfaces.summarization.service.impl;


import com.psi.ai_qa.common.enums.DocumentStatus;
import com.psi.ai_qa.common.model.DocumentChunk;
import com.psi.ai_qa.common.repo.DocumentRepository;
import com.psi.ai_qa.interfaces.summarization.service.SummarizationService;
import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SummarizationServiceImpl implements SummarizationService {

    private final DocumentRepository repo;
    private final ChatLanguageModel chatModel;

    public String summarize(String documentId) {

        DocumentChunk doc = repo.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        if (doc.getRawText() == null) {
            throw new RuntimeException("No text available for summarization");
        }

        if (doc.getSummary() != null) {
            return doc.getSummary();
        }

        String prompt = """
                Summarize the following content in 5 bullet points:

                %s
                """.formatted(doc.getRawText()
                .substring(0, Math.min(12000, doc.getRawText().length())));

        String summary = chatModel.generate(prompt);

        doc.setSummary(summary);
        doc.setStatus(DocumentStatus.valueOf("SUMMARIZED"));
        repo.save(doc);

        return summary;
    }
}