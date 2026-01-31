package com.psi.ai_qa.interfaces.upload.service.impl;

import com.psi.ai_qa.common.enums.DocumentStatus;
import com.psi.ai_qa.common.model.DocumentChunk;
import com.psi.ai_qa.common.repo.DocumentRepository;
import com.psi.ai_qa.interfaces.transcribe.service.TranscriptionService;
import com.psi.ai_qa.interfaces.upload.service.UploadService;
import dev.langchain4j.model.embedding.EmbeddingModel;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class UploadServiceImpl implements UploadService {

    private final DocumentRepository repo;
    private final TranscriptionService transcriptionService;
    private final EmbeddingModel embeddingModel;

    @Value("${upload.dir}")
    private String uploadDir;

    @Override
    public DocumentChunk upload(MultipartFile file, String userId) throws Exception {

        String docId = UUID.randomUUID().toString();
        Path savedPath = saveFile(file, userId, docId);

        String rawText = isPdf(file) ? extractPdf(savedPath) : null;

        DocumentChunk doc = new DocumentChunk();
        doc.setId(docId);
        doc.setUserId(userId);
        doc.setFileName(file.getOriginalFilename());
        doc.setType(getExt(file.getOriginalFilename()));
        doc.setCategory(isPdf(file) ? "PDF" : "MEDIA");
        doc.setFilePath(savedPath.toString());
        doc.setRawText(rawText);
        doc.setStatus(DocumentStatus.valueOf("UPLOADED"));
        doc.setUploadTime(System.currentTimeMillis());

        return repo.save(doc);
    }

    private Path saveFile(MultipartFile file, String user, String id) throws Exception {
        Path dir = Paths.get(uploadDir, user);
        Files.createDirectories(dir);
        Path path = dir.resolve(id + "_" + file.getOriginalFilename());
        file.transferTo(path);
        return path;
    }

    private String extractPdf(Path path) throws Exception {
        try (PDDocument doc = Loader.loadPDF(path.toFile())) {
            return new PDFTextStripper().getText(doc);
        }
    }

    private boolean isPdf(MultipartFile f) {
        return f.getOriginalFilename().toLowerCase().endsWith(".pdf");
    }

    private String getExt(String name) {
        return name.substring(name.lastIndexOf('.') + 1);
    }

    public void triggerTranscription(String documentId) {
        transcriptionService.transcribe(documentId);
    }


}
