package com.subbu.pdfchatassistant.service;

import com.subbu.pdfchatassistant.dto.ChatHistoryResponse;

import java.util.List;

public interface MemoryService {

    void addMessage(String sessionId, String type, String message);

    List<ChatHistoryResponse> getHistory(String sessionId);

    void clear(String sessionId);
}