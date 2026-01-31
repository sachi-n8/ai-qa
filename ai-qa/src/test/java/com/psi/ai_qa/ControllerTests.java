import com.psi.ai_qa.common.model.DocumentChunk;
import com.psi.ai_qa.common.model.User;
import com.psi.ai_qa.common.repo.DocumentRepository;
import com.psi.ai_qa.common.util.JwtUtil;
import com.psi.ai_qa.interfaces.auth.AuthController;
import com.psi.ai_qa.interfaces.auth.service.AuthSerivce;
import com.psi.ai_qa.interfaces.chat.ChatController;
import com.psi.ai_qa.interfaces.chat.model.ChatRequest;
import com.psi.ai_qa.interfaces.chat.model.ChatResponse;
import com.psi.ai_qa.interfaces.chat.service.ChatService;
import com.psi.ai_qa.interfaces.document.DocumentController;
import com.psi.ai_qa.interfaces.document.model.DocumentDashboardResponse;
import com.psi.ai_qa.interfaces.document.service.DocumentService;
import com.psi.ai_qa.interfaces.login.LoginController;
import com.psi.ai_qa.interfaces.login.model.UserRequest;
import com.psi.ai_qa.interfaces.login.model.UserResponse;
import com.psi.ai_qa.interfaces.login.service.LoginService;
import com.psi.ai_qa.interfaces.media.MediaController;
import com.psi.ai_qa.interfaces.summarization.SummarizationController;
import com.psi.ai_qa.interfaces.summarization.model.SummarizeResponse;
import com.psi.ai_qa.interfaces.summarization.service.SummarizationService;
import com.psi.ai_qa.interfaces.timestamp.TimestampController;
import com.psi.ai_qa.interfaces.timestamp.model.TimestampRequest;
import com.psi.ai_qa.interfaces.timestamp.model.TimestampResponse;
import com.psi.ai_qa.interfaces.timestamp.service.TimestampService;
import com.psi.ai_qa.interfaces.transcribe.TranscriptionController;
import com.psi.ai_qa.interfaces.transcribe.model.TranscriptionResponse;
import com.psi.ai_qa.interfaces.transcribe.service.TranscriptionService;
import com.psi.ai_qa.interfaces.upload.UploadController;
import com.psi.ai_qa.interfaces.upload.model.UploadResponse;
import com.psi.ai_qa.interfaces.upload.service.UploadService;
import com.psi.ai_qa.interfaces.upload.service.impl.DocumentProcessingService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ControllerTests {

    @Nested
    class AuthControllerTest {
        @Mock AuthSerivce authSerivce;
        @InjectMocks AuthController controller;

        @Test
        void auth_returns_token_and_success_status() {
            when(authSerivce.authenticate(any(User.class))).thenReturn("jwt123");
            var res = controller.auth();
            assertEquals("jwt123", res.getJwtToken());
            assertNotNull(res.getStatusCode());
            assertNotNull(res.getStatusMessage());
            verify(authSerivce).authenticate(any(User.class));
        }
    }

    @Nested
    class ChatControllerTest {
        @Mock ChatService service;
        @InjectMocks ChatController controller;

        @Test
        void chat_delegates_to_service() {
            ChatRequest req = new ChatRequest();
            ChatResponse expected = new ChatResponse();
            when(service.chat(req)).thenReturn(expected);
            var res = controller.chat(req);
            assertSame(expected, res);
            verify(service).chat(req);
        }
    }

    @Nested
    class LoginControllerTest {
        @Mock LoginService service;
        @InjectMocks LoginController controller;

        @Test
        void login_delegates_and_logs() {
            UserRequest req = new UserRequest();
            req.setEmail("user@example.com");
            UserResponse expected = new UserResponse();
            when(service.login(req)).thenReturn(expected);
            var res = controller.login(req);
            assertSame(expected, res);
            verify(service).login(req);
        }
    }

    @Nested
    class MediaControllerTest {
        @Mock DocumentRepository repository;
        @InjectMocks MediaController controller;

        @Test
        void playMedia_returns_partial_content_with_media_type() throws Exception {
            DocumentChunk chunk = new DocumentChunk();
            File temp = File.createTempFile("media", ".mp4");
            temp.deleteOnExit();
            chunk.setId("doc1");
            chunk.setFilePath(temp.getAbsolutePath());
            chunk.setType("mp4");
            when(repository.findById("doc1")).thenReturn(Optional.of(chunk));

            ResponseEntity<Resource> res = controller.playMedia("doc1", 0);
            assertEquals(206, res.getStatusCode().value());
            assertEquals("bytes", res.getHeaders().getFirst(HttpHeaders.ACCEPT_RANGES));
            assertEquals(MediaType.valueOf("video/mp4"), res.getHeaders().getContentType());
            assertTrue(res.getBody().exists());
        }

        @Test
        void playMedia_throws_when_missing_doc() {
            when(repository.findById("missing")).thenReturn(Optional.empty());
            assertThrows(RuntimeException.class, () -> controller.playMedia("missing", 0));
        }
    }

    @Nested
    class UploadControllerTest {
        @Mock UploadService service;
        @Mock JwtUtil jwtUtil;
        @Mock DocumentProcessingService processingService;
        @InjectMocks UploadController controller;

        @Test
        void upload_triggers_transcription_for_media() throws Exception {
            MockMultipartFile file = new MockMultipartFile("file", "a.mp3", "audio/mpeg", new byte[]{1});
            when(jwtUtil.extractEmail("token")).thenReturn("user@example.com");

            DocumentChunk chunk = new DocumentChunk();
            chunk.setId("doc1");
            chunk.setFileName("a.mp3");
            chunk.setStatus(com.psi.ai_qa.common.enums.DocumentStatus.UPLOADED);
            chunk.setType("mp3");
            when(service.upload(any(), eq("user@example.com"))).thenReturn(chunk);

            ResponseEntity<UploadResponse> res = controller.upload(file, "Bearer token");
            assertEquals(200, res.getStatusCode().value());
            assertEquals("doc1", res.getBody().getDocumentID());
            verify(service).triggerTranscription("doc1");
            verify(processingService, never()).processPdfAsync(any());
        }

        @Test
        void upload_triggers_pdf_processing_for_pdf() throws Exception {
            MockMultipartFile file = new MockMultipartFile("file", "a.pdf", MediaType.APPLICATION_PDF_VALUE, new byte[]{1});
            when(jwtUtil.extractEmail("token")).thenReturn("user@example.com");

            DocumentChunk chunk = new DocumentChunk();
            chunk.setId("doc2");
            chunk.setFileName("a.pdf");
            chunk.setStatus(com.psi.ai_qa.common.enums.DocumentStatus.UPLOADED);
            chunk.setType("pdf");
            when(service.upload(any(), eq("user@example.com"))).thenReturn(chunk);

            ResponseEntity<UploadResponse> res = controller.upload(file, "Bearer token");
            assertEquals("doc2", res.getBody().getDocumentID());
            verify(processingService).processPdfAsync("doc2");
            verify(service, never()).triggerTranscription(any());
        }
    }

    @Nested
    class DocumentControllerTest {
        @Mock DocumentService service;
        @Mock JwtUtil jwtUtil;
        @InjectMocks DocumentController controller;

        @Test
        void listDocuments_extracts_email_and_delegates() {
            when(jwtUtil.extractEmail("token")).thenReturn("me@ex.com");
            DocumentDashboardResponse resp = new DocumentDashboardResponse();
            when(service.listDocuments("me@ex.com")).thenReturn(resp);
            var res = controller.listDocuments("Bearer token");
            assertSame(resp, res);
            verify(service).listDocuments("me@ex.com");
        }
    }

    @Nested
    class TimestampControllerTest {
        @Mock TimestampService service;
        @InjectMocks TimestampController controller;

        @Test
        void extract_delegates() {
            TimestampRequest req = new TimestampRequest();
            TimestampResponse resp = new TimestampResponse();
            when(service.extract(req)).thenReturn(resp);
            var out = controller.extract(req);
            assertSame(resp, out);
            verify(service).extract(req);
        }
    }

    @Nested
    class TranscriptionControllerTest {
        @Mock TranscriptionService service;
        @InjectMocks TranscriptionController controller;

        @Test
        void transcribe_calls_service_and_returns_ok() {
            ResponseEntity<TranscriptionResponse> res = controller.transcribe("doc123");
            assertEquals(200, res.getStatusCode().value());
            assertEquals("doc123", res.getBody().getDocumentId());
            verify(service).transcribe("doc123");
        }
    }

    @Nested
    class SummarizationControllerTest {
        @Mock SummarizationService service;
        @InjectMocks SummarizationController controller;

        @Test
        void summarize_returns_response_from_service() {
            when(service.summarize("doc1")).thenReturn("short summary");
            ResponseEntity<SummarizeResponse> res = controller.summarize("doc1");
            assertEquals(200, res.getStatusCode().value());
            assertEquals("doc1", res.getBody().getDocumentId());
            assertEquals("short summary", res.getBody().getSummary());
            verify(service).summarize("doc1");
        }
    }
}
