package com.subbu.pdfchatassistant.service;

import org.springframework.ai.document.Document;
import java.util.List;

public interface RetrievalService {
    List<Document> retrieve(String query);
}