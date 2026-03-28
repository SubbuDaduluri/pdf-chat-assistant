package com.subbu.pdfchatassistant.service.impl;

import com.subbu.pdfchatassistant.config.AppProperties;
import com.subbu.pdfchatassistant.service.RetrievalService;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RetrievalServiceImpl implements RetrievalService {

    private final VectorStore vectorStore;
    private final AppProperties appProperties;

    public RetrievalServiceImpl(VectorStore vectorStore,
                                AppProperties appProperties) {
        this.vectorStore = vectorStore;
        this.appProperties = appProperties;
    }

    @Override
    public List<Document> retrieve(String query) {

        int topK = appProperties.getRag().getTopK();

        return vectorStore.similaritySearch(query)
                .stream()
                .limit(topK)
                .toList();
    }
}