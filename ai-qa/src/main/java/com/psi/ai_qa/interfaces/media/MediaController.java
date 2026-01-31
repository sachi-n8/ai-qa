package com.psi.ai_qa.interfaces.media;

import com.psi.ai_qa.common.constants.Endpoints;
import com.psi.ai_qa.common.model.DocumentChunk;
import com.psi.ai_qa.common.repo.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@RestController
@RequestMapping(Endpoints.AI_QA)
@RequiredArgsConstructor
public class MediaController {

    private final DocumentRepository repository;

    @GetMapping(Endpoints.DOCUMENT_ID)
    public ResponseEntity<Resource> playMedia(
            @PathVariable String documentId,
            @RequestParam(defaultValue = "0") long start) {

        DocumentChunk doc = repository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        File file = new File(doc.getFilePath());
        if (!file.exists()) {
            throw new RuntimeException("Media file not found");
        }

        Resource resource = new FileSystemResource(file);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(resolveMediaType(file.getName()));
        headers.set(HttpHeaders.ACCEPT_RANGES, "bytes");

        return ResponseEntity
                .status(HttpStatus.PARTIAL_CONTENT)
                .headers(headers)
                .body(resource);
    }

    private MediaType resolveMediaType(String filename) {
        if (filename.endsWith(".mp4")) return MediaType.valueOf("video/mp4");
        if (filename.endsWith(".mp3")) return MediaType.valueOf("audio/mpeg");
        if (filename.endsWith(".wav")) return MediaType.valueOf("audio/wav");
        return MediaType.APPLICATION_OCTET_STREAM;
    }
}