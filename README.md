# AI-QA API Reference

Comprehensive documentation for all REST APIs exposed by the AI-QA service. This document details functionality, endpoints, authentication, request/response bodies, status codes, and example payloads.

Base URL: /api/ai-qa

Note: Actual endpoint suffixes are defined in com.psi.ai_qa.common.constants.Endpoints. The paths below reflect the effective routes used by controllers.

Authentication
- Some endpoints require a Bearer JWT token in the Authorization header: Authorization: Bearer <token>
- Obtain a token using the Auth endpoint.

Controllers and Endpoints

1) Auth
- POST /api/ai-qa/auth
- Purpose: Issue a JWT token for a user (placeholder implementation creates a default user and returns a token).
- Auth: Not required.
- Request: none (no body)
- Response (application/json):
  {
    "jwtToken": "<token>",
    "statusCode": "SUCCESS",
    "statusMessage": "JWT Token generated"
  }
- Notes: The implementation constructs a new User and delegates to AuthSerivce.authenticate.

2) Login
- POST /api/ai-qa/login
- Purpose: Log in a user using email/password (exact validation logic is in LoginService).
- Auth: Not required.
- Request (application/json):
  {
    "email": "user@example.com",
    "password": "string"
  }
- Response (application/json) example:
  {
    "email": "user@example.com",
    "name": "User Name",
    "jwtToken": "<token>",
    "statusCode": "SUCCESS",
    "statusMessage": "Login successful"
  }
- Notes: Controller logs the email and delegates to LoginService.login.

3) Chat
- POST /api/ai-qa/chat
- Purpose: Send a chat request to the assistant for a response.
- Auth: Recommended (depending on security configuration).
- Request (application/json) example (ChatRequest):
  {
    "message": "Explain vector databases",
    "documentId": "optional-doc-id",
    "context": "optional context"
  }
- Response (application/json) example (ChatResponse):
  {
    "response": "Vector databases store embeddings...",
    "sources": ["doc1", "doc2"],
    "meta": {
      "tokens": 123
    }
  }
- Notes: Controller delegates to ChatService.chat.

4) Document Dashboard
- GET /api/ai-qa/document
- Purpose: List a user's documents and dashboard aggregates.
- Auth: Required (Bearer token).
- Headers:
  - Authorization: Bearer <jwt>
- Request: none
- Response (application/json) example (DocumentDashboardResponse):
  {
    "documents": [
      {
        "id": "abc123",
        "fileName": "file.pdf",
        "type": "pdf",
        "status": "PROCESSED",
        "createdAt": "2024-11-01T12:34:56Z"
      }
    ],
    "summaryStats": {
      "total": 10,
      "pdf": 6,
      "media": 4
    }
  }
- Notes: Controller extracts user email from JWT using JwtUtil.extractEmail and delegates to DocumentService.listDocuments.

5) Media Streaming
- GET /api/ai-qa/document/{documentId}
- Purpose: Stream media (audio/video) for a document by ID. Returns partial content with appropriate media type.
- Auth: Depending on security configuration.
- Path Params:
  - documentId (string)
- Query Params:
  - start (long, default=0) — start byte or timestamp hint (implementation currently sets headers and returns resource; accepts-range: bytes)
- Request: none
- Response: 206 Partial Content with body as a file Resource.
  - Headers:
    - Content-Type: video/mp4 | audio/mpeg | audio/wav | application/octet-stream
    - Accept-Ranges: bytes
- Error Responses:
  - 404/500 RuntimeException("Document not found") if the document ID is missing in repository
  - 404/500 RuntimeException("Media file not found") if file path is missing on disk
- Notes: Media type is inferred from file extension (.mp4/.mp3/.wav).

6) Upload
- POST /api/ai-qa/upload
- Purpose: Upload a file (PDF or media). Triggers background processing based on type.
- Auth: Required (Bearer token).
- Headers:
  - Authorization: Bearer <jwt>
  - Content-Type: multipart/form-data
- Request (multipart/form-data):
  - file: binary file
- Behavior:
  - Extracts email from JWT
  - service.upload stores metadata and returns DocumentChunk
  - If type is media (mp3/mp4/wav), triggers service.triggerTranscription(documentId)
  - If type is pdf, triggers documentProcessingService.processPdfAsync(documentId)
- Response (application/json) example (UploadResponse):
  {
    "documentID": "abc123",
    "fileName": "myfile.pdf",
    "status": "UPLOADED"
  }
- Notes: Accepted media types are determined by DocumentType.isMedia and by controller logic for "pdf".

7) Transcription
- POST /api/ai-qa/transcribe/{documentId}
- Purpose: Start transcription for a media document.
- Auth: Depending on security configuration.
- Path Params:
  - documentId (string)
- Request: none
- Response (application/json):
  {
    "documentId": "abc123",
    "status": "TRANSCRIBED"
  }
- Notes: Controller invokes TranscriptionService.transcribe and returns a simple confirmation payload.

8) Summarization
- POST /api/ai-qa/summarize/{documentId}
- Purpose: Generate a summary for a document.
- Auth: Depending on security configuration.
- Path Params:
  - documentId (string)
- Request: none
- Response (application/json):
  {
    "documentId": "abc123",
    "summary": "Concise summary text"
  }
- Notes: Controller calls SummarizationService.summarize and returns the summary.

9) Timestamp Extraction
- POST /api/ai-qa/timestamp
- Purpose: Extract timestamps (e.g., speaker diarization or semantic timestamps) from given text or context.
- Auth: Depending on security configuration.
- Request (application/json) example (TimestampRequest):
  {
    "documentId": "abc123",
    "text": "optional text for analysis",
    "language": "en"
  }
- Response (application/json) example (TimestampResponse):
  {
    "documentId": "abc123",
    "segments": [
      { "start": 0.0, "end": 5.2, "text": "Hello" }
    ]
  }
- Notes: Controller delegates to TimestampService.extract.

Common Models (selected)
- DocumentChunk (Mongo document):
  {
    "id": "string",
    "userId": "string",
    "fileName": "string",
    "type": "pdf|mp3|mp4|wav",
    "category": "PDF|MEDIA",
    "filePath": "string",
    "createdAt": "ISO-8601",
    "uploadTime": 0,
    "status": "enum DocumentStatus",
    "rawText": "string",
    "summary": "string",
    "transcriptSegments": [ {"start": 0.0, "end": 1.0, "text": "..."} ],
    "chunks": [ {"text": "...", "embedding": [0.1, ...], "startChar": 0, "endChar": 10} ]
  }

Security and Headers
- Authorization: Several endpoints expect Bearer tokens. The JwtUtil is used to extract the user email from the token.
- Content negotiation: JSON for request/response unless multipart/form-data for uploads. Media endpoint returns binary with appropriate Content-Type.

Status Codes
- 200 OK: Successful operations (login, chat, upload response, summarize, transcribe confirm, timestamp extract)
- 206 Partial Content: Media streaming endpoint
- 4xx/5xx: Error conditions, including missing documents or files on disk for media playback

Examples
1) Get token
curl -X POST http://localhost:8080/api/ai-qa/auth

2) Login
curl -X POST http://localhost:8080/api/ai-qa/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"secret"}'

3) Chat
curl -X POST http://localhost:8080/api/ai-qa/chat \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"message":"Explain RAG","documentId":"abc123"}'

4) List documents
curl -X GET http://localhost:8080/api/ai-qa/document \
  -H "Authorization: Bearer <token>"

5) Stream media
curl -i -X GET http://localhost:8080/api/ai-qa/document/abc123

6) Upload file (PDF)
curl -X POST http://localhost:8080/api/ai-qa/upload \
  -H "Authorization: Bearer <token>" \
  -F file=@/path/to/file.pdf

7) Transcribe
curl -X POST http://localhost:8080/api/ai-qa/transcribe/abc123

8) Summarize
curl -X POST http://localhost:8080/api/ai-qa/summarize/abc123

9) Timestamp
curl -X POST http://localhost:8080/api/ai-qa/timestamp \
  -H "Content-Type: application/json" \
  -d '{"documentId":"abc123","text":"Hello world"}'

Notes and Assumptions
- Request/response examples are inferred from controller method signatures and typical DTO fields in the repository. Exact fields may vary slightly depending on service implementations and models.
- Endpoints are mounted under the base path /api/ai-qa as defined by @RequestMapping(Endpoints.AI_QA) in controllers.
- For security (Spring Security), ensure proper configuration for which endpoints require authentication.
