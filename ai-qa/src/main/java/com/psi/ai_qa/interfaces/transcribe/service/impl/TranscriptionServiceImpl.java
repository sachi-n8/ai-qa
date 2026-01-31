package com.psi.ai_qa.interfaces.transcribe.service.impl;

import com.psi.ai_qa.common.enums.DocumentStatus;
import com.psi.ai_qa.common.model.DocumentChunk;
import com.psi.ai_qa.common.repo.DocumentRepository;
import com.psi.ai_qa.interfaces.transcribe.service.TranscriptionService;
import com.theokanning.openai.audio.CreateTranscriptionRequest;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TranscriptionServiceImpl implements TranscriptionService {

    private final DocumentRepository repo;
    private final OpenAiService openAiService;

    @Async
    @Override
    public void transcribe(String documentId) {

        DocumentChunk doc = repo.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        if (!"MEDIA".equalsIgnoreCase(doc.getCategory())) {
            throw new RuntimeException("Only media files can be transcribed");
        }

        File file = new File(doc.getFilePath());

        CreateTranscriptionRequest req = CreateTranscriptionRequest.builder()
                .model("whisper-1")
                .responseFormat("verbose_json") // ⭐ timestamps
                .build();

        var response = openAiService.createTranscription(req, file);

        doc.setRawText(response.getText());

        List<DocumentChunk.TranscriptSegment> segments = new ArrayList<>();
        response.getSegments().forEach(s -> {
            DocumentChunk.TranscriptSegment seg = new DocumentChunk.TranscriptSegment();
            seg.setStart(s.getStart());
            seg.setEnd(s.getEnd());
            seg.setText(s.getText());
            segments.add(seg);
        });

        doc.setTranscriptSegments(segments);
        doc.setStatus(DocumentStatus.valueOf("TRANSCRIBED"));

        repo.save(doc);
    }
}
