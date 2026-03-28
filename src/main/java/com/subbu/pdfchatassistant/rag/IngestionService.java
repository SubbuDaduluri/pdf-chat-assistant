package com.subbu.pdfchatassistant.rag;

import org.springframework.web.multipart.MultipartFile;

public interface IngestionService {

    void ingest(MultipartFile file);
}