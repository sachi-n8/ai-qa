package com.psi.ai_qa.common.enums;

public enum DocumentType {
    PDF,
    AUDIO,
    VIDEO;

    // Convert String → enum safely
    public static DocumentType from(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Document type cannot be null");
        }
        return DocumentType.valueOf(value.toUpperCase());
    }

    // Media check
    public boolean isMedia() {
        return this == AUDIO || this == VIDEO;
    }

    // ✅ One-liner helper (MOST IMPORTANT)
    public static boolean isMedia(String value) {
        try {
            return from(value).isMedia();
        } catch (Exception e) {
            return false;
        }
    }
}
