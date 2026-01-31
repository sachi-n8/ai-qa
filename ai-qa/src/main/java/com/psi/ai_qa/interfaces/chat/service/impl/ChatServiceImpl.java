package com.psi.ai_qa.interfaces.chat.service.impl;

import com.psi.ai_qa.common.model.DocumentChunk;
import com.psi.ai_qa.common.repo.DocumentRepository;
import com.psi.ai_qa.common.util.VectorUtil;
import com.psi.ai_qa.interfaces.chat.model.ChatRequest;
import com.psi.ai_qa.interfaces.chat.model.ChatResponse;
import com.psi.ai_qa.interfaces.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;

import java.util.ArrayList;
import java.util.List;

import static com.psi.ai_qa.common.enums.DocumentStatus.COMPLETED;


@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final DocumentRepository repository;
    private final EmbeddingModel embeddingModel;
    private final ChatLanguageModel chatModel;

    @Override
    public ChatResponse chat(ChatRequest request) {


        if (request.getDocumentId() == null || request.getDocumentId().isBlank()) {
            throw new IllegalArgumentException("documentId is required");
        }

        if (request.getQuestion() == null || request.getQuestion().isBlank()) {
            throw new IllegalArgumentException("question is required");
        }


        DocumentChunk doc = repository.findById(request.getDocumentId())
                .orElseThrow(() -> new RuntimeException("Document not found"));



        if (!doc.getStatus().equals(COMPLETED)) {
            ChatResponse response = new ChatResponse();
            response.setAnswer("Document is still being processed. Please wait.");
            return response;
        }


        var questionEmbedding = embeddingModel
                .embed(request.getQuestion())
                .content()
                .vector();


        List<DocumentChunk.Chunk> topChunks = doc.getChunks()
                .stream()
                .sorted((a, b) -> Double.compare(
                        VectorUtil.cosineSimilarity(b.getEmbedding(), toList(questionEmbedding)),
                        VectorUtil.cosineSimilarity(a.getEmbedding(), toList(questionEmbedding))
                ))
                .limit(3)
                .toList();


        StringBuilder context = new StringBuilder();
        for (DocumentChunk.Chunk c : topChunks) {
            context.append(c.getText()).append("\n");
        }


        String prompt = """
                Answer the question using ONLY the context below.
                If the answer is not present, say "Not found in document".

                Context:
                %s

                Question:
                %s
                """.formatted(context, request.getQuestion());

        String answer = chatModel.generate(prompt);


        ChatResponse response = new ChatResponse();
        response.setAnswer(answer);

        List<ChatResponse.Reference> refs = new ArrayList<>();
        for (DocumentChunk.Chunk c : topChunks) {
            ChatResponse.Reference r = new ChatResponse.Reference();
            r.setStartChar(c.getStartChar());
            r.setEndChar(c.getEndChar());
            refs.add(r);
        }
        response.setReferences(refs);

        return response;
    }

    private List<Float> toList(float[] arr) {
        List<Float> list = new ArrayList<>();
        for (float f : arr) list.add(f);
        return list;
    }
}
