package com.subbu.pdfchatassistant.controller;

import com.subbu.pdfchatassistant.dto.ChatHistoryResponse;
import com.subbu.pdfchatassistant.dto.ChatRequest;
import com.subbu.pdfchatassistant.dto.ChatResponse;
import com.subbu.pdfchatassistant.service.ChatService;
import com.subbu.pdfchatassistant.service.MemoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final MemoryService memoryService;


    public ChatController(ChatService chatService, MemoryService memoryService) {
        this.chatService = chatService;
        this.memoryService = memoryService;
    }

    /**
     * Chat endpoint using JSON request
     */
    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request) {

        String result = chatService.chat(request.getSessionId(), request.getMessage());

        return new ChatResponse(result);
    }

    /**
     * Get chat history
     */
    @GetMapping("/history")
    public List<ChatHistoryResponse> getHistory(@RequestParam String sessionId) {

        return memoryService.getHistory(sessionId);
    }
}