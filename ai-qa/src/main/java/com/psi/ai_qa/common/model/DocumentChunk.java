package com.psi.ai_qa.common.model;

import com.psi.ai_qa.common.enums.DocumentStatus;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Document(collection = "documents")
public class DocumentChunk {

    @Id
    private String id;

    private String userId;
    private String fileName;
    private String type;        // pdf, mp4, mp3
    private String category;    // PDF / MEDIA
    private String filePath;

    @CreatedDate
    private Instant createdAt;
    private Long uploadTime;

    // ---------- Processing State ----------
    private DocumentStatus status;

    // ---------- Text Data ----------
    private String rawText;
    private String summary;

    // ---------- Whisper Timestamps ----------
    private List<TranscriptSegment> transcriptSegments;

    // ---------- Vector Chunks ----------
    private List<Chunk> chunks;


    @Data
    public static class TranscriptSegment {
        private double start;
        private double end;
        private String text;
    }

    @Data
    public static class Chunk {
        private String text;
        private List<Float> embedding;
        private int startChar;
        private int endChar;
    }
}
