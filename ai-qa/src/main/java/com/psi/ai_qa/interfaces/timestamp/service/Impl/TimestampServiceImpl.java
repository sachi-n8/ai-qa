package com.psi.ai_qa.interfaces.timestamp.service.Impl;

import com.psi.ai_qa.common.model.DocumentChunk;
import com.psi.ai_qa.common.repo.DocumentRepository;
import com.psi.ai_qa.common.util.VectorUtil;
import com.psi.ai_qa.interfaces.timestamp.model.TimestampRequest;
import com.psi.ai_qa.interfaces.timestamp.model.TimestampResponse;
import com.psi.ai_qa.interfaces.timestamp.service.TimestampService;
import dev.langchain4j.model.embedding.EmbeddingModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TimestampServiceImpl implements TimestampService {

    private final DocumentRepository repository;
    private final EmbeddingModel embeddingModel;

    @Override
    public TimestampResponse extract(TimestampRequest request) {

        DocumentChunk doc = repository.findById(request.getDocumentId())
                .orElseThrow(() -> new RuntimeException("Document not found"));

        if (!"MEDIA".equalsIgnoreCase(doc.getCategory())) {
            throw new RuntimeException("Timestamps only supported for audio/video files");
        }

        // 1️⃣ Embed question
        float[] qVector = embeddingModel
                .embed(request.getQuestion())
                .content()
                .vector();

        List<Float> qEmbedding = toList(qVector);

        // 2️⃣ Find best matching chunk
        DocumentChunk.Chunk best = null;
        double bestScore = -1;

        for (DocumentChunk.Chunk c : doc.getChunks()) {
            double score = VectorUtil.cosineSimilarity(c.getEmbedding(), qEmbedding);
            if (score > bestScore) {
                bestScore = score;
                best = c;
            }
        }

        if (best == null) {
            throw new RuntimeException("No relevant segment found");
        }

        // 3️⃣ Convert char offsets to seconds (approx)
        double duration = estimateDuration(doc);
        int totalChars = doc.getChunks()
                .get(doc.getChunks().size() - 1)
                .getEndChar();

        double startSec = (best.getStartChar() / (double) totalChars) * duration;
        double endSec = (best.getEndChar() / (double) totalChars) * duration;

        // 4️⃣ Build response
        TimestampResponse res = new TimestampResponse();
        res.setTopic(request.getQuestion());
        res.setStart(startSec);
        res.setEnd(endSec);

        return res;
    }

    private double estimateDuration(DocumentChunk doc) {
        // fallback: assume 10 chars/sec if duration not stored
        int totalChars = doc.getChunks()
                .get(doc.getChunks().size() - 1)
                .getEndChar();
        return totalChars / 10.0;
    }

    private List<Float> toList(float[] arr) {
        List<Float> list = new ArrayList<>();
        for (float f : arr) list.add(f);
        return list;
    }
}