package com.psi.ai_qa.interfaces.chat.service;


import com.psi.ai_qa.interfaces.chat.model.ChatRequest;
import com.psi.ai_qa.interfaces.chat.model.ChatResponse;

public interface ChatService {
    ChatResponse chat(ChatRequest request);
}