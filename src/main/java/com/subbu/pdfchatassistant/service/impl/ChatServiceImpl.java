package com.subbu.pdfchatassistant.service.impl;

import com.subbu.pdfchatassistant.service.ChatService;
import com.subbu.pdfchatassistant.service.RetrievalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChatServiceImpl implements ChatService {

    private static final int MAX_CONTEXT_CHUNKS = 5;

    private final RetrievalService retrievalService;
    private final ChatClient chatClient;

    public ChatServiceImpl(RetrievalService retrievalService,
                           ChatClient chatClient) {
        this.retrievalService = retrievalService;
        this.chatClient = chatClient;
    }

    @Override
    public String chat(String sessionId, String question) {

        log.info("Incoming Question: {}", question);
        log.info("Session ID: {}", sessionId);

        // Smart Query Rewrite (ONLY when needed)
        String refinedQuery = question;

        if (question.split(" ").length > 2) {
            refinedQuery = chatClient.prompt()
                    .user("""
                        Rewrite this question into a clear search query.
                        - Resolve words like "his", "it"
                        - Include subject if missing

                        Question: %s
                        """.formatted(question))
                    .advisors(a -> a.param("conversationId", sessionId))
                    .call()
                    .content();

            log.info("Refined Query: {}", refinedQuery);
        } else {
            log.info("Using Original Query: {}", refinedQuery);
        }

        // Retrieval
        List<Document> docs = retrievalService.retrieve(refinedQuery);

        log.info("Retrieved Docs Count (refined): {}",
                docs != null ? docs.size() : 0);

        // Fallback
        if (docs == null || docs.isEmpty()) {
            log.warn("No results for refined query, trying original...");
            docs = retrievalService.retrieve(question);

            log.info("Retrieved Docs Count (fallback): {}",
                    docs != null ? docs.size() : 0);
        }

        if (docs == null || docs.isEmpty()) {
            log.warn("No documents found for question: {}", question);
            return "I don't know";
        }

        // Build context
        String context = docs.stream()
                .distinct()
                .limit(MAX_CONTEXT_CHUNKS)
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));

        log.debug("Context Used:\n{}", context);

        //  Final Answer
        String response = chatClient.prompt()
                .user(buildPrompt(context, question))
                .advisors(a -> a.param("conversationId", sessionId))
                .call()
                .content();

        log.info("Raw Response: {}", response);

        String finalResponse = sanitize(response);

        log.info("Final Response: {}", finalResponse);

        return finalResponse;
    }

    private String buildPrompt(String context, String question) {
        return """
                Answer the question using ONLY the given context.

                Rules:
                - Do not guess or add extra information
                - If answer is not present → say: I don't know
                - Use history only to resolve references like "his", "it"
                - Keep answer short and natural

                Answer Style:
                - Start with a small context phrase (e.g., "From the resume")
                - Use exact words from context for key details
                - List multiple values clearly if present

                Context:
                %s

                Question:
                %s

                Answer:
                """.formatted(context, question);
    }

    private String sanitize(String response) {
        if (response == null || response.isBlank()) return "I don't know";
        return response.trim();
    }
}