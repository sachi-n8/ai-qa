package com.psi.ai_qa.interfaces.upload.service.impl;

import com.psi.ai_qa.common.enums.DocumentStatus;
import com.psi.ai_qa.common.model.DocumentChunk;
import com.psi.ai_qa.common.repo.DocumentRepository;
import dev.langchain4j.model.embedding.EmbeddingModel;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentProcessingService {

    private final DocumentRepository repo;
    private final EmbeddingModel embeddingModel;

    @Async
    public void processPdfAsync(String documentId) {

        DocumentChunk doc = repo.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        try {
            doc.setStatus(DocumentStatus.valueOf("PROCESSING"));
            repo.save(doc);

            String rawText = doc.getRawText();
            if (rawText == null || rawText.isBlank()) {
                throw new RuntimeException("No text found in PDF");
            }

            int chunkSize = 800;
            int overlap = 100;

            List<DocumentChunk.Chunk> chunks = new ArrayList<>();
            int start = 0;

            while (start < rawText.length()) {
                int end = Math.min(start + chunkSize, rawText.length());
                String chunkText = rawText.substring(start, end).trim();

                if (!chunkText.isEmpty()) {
                    float[] embeddingVector = embeddingModel
                            .embed(chunkText)
                            .content()
                            .vector();

                    DocumentChunk.Chunk chunk = new DocumentChunk.Chunk();
                    chunk.setText(chunkText);
                    chunk.setEmbedding(floatArrayToList(embeddingVector));
                    chunk.setStartChar(start);
                    chunk.setEndChar(end);

                    chunks.add(chunk);
                }

                start = end - overlap;
                if (start < 0) start = end;
            }

            doc.setChunks(chunks);
            doc.setStatus(DocumentStatus.valueOf("COMPLETED"));
            repo.save(doc);

        } catch (Exception e) {
            doc.setStatus(DocumentStatus.valueOf("FAILED"));
            repo.save(doc);
            e.printStackTrace();
        }
    }

    private List<Float> floatArrayToList(float[] arr) {
        List<Float> list = new ArrayList<>();
        for (float f : arr) list.add(f);
        return list;
    }
}
