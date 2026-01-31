package com.psi.ai_qa.interfaces.chat;

import com.psi.ai_qa.common.constants.Endpoints;
import com.psi.ai_qa.interfaces.chat.model.ChatRequest;
import com.psi.ai_qa.interfaces.chat.model.ChatResponse;
import com.psi.ai_qa.interfaces.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Endpoints.AI_QA)
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping(Endpoints.CHAT)
    public ChatResponse chat(@RequestBody ChatRequest request) {
        return chatService.chat(request);
    }
}