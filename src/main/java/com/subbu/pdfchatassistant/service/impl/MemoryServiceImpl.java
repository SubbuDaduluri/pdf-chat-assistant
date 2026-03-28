package com.subbu.pdfchatassistant.service.impl;

import com.subbu.pdfchatassistant.service.MemoryService;
import org.springframework.stereotype.Service;

import java.util.*;


import com.subbu.pdfchatassistant.dto.ChatHistoryResponse;

import java.util.*;

@Service
public class MemoryServiceImpl implements MemoryService {

    private final Map<String, List<ChatHistoryResponse>> memory = new HashMap<>();

    @Override
    public void addMessage(String sessionId, String type, String message) {
        memory.computeIfAbsent(sessionId, k -> new ArrayList<>())
                .add(new ChatHistoryResponse(type, message));
    }

    @Override
    public List<ChatHistoryResponse> getHistory(String sessionId) {
        return memory.getOrDefault(sessionId, new ArrayList<>());
    }

    @Override
    public void clear(String sessionId) {
        memory.remove(sessionId);
    }
}