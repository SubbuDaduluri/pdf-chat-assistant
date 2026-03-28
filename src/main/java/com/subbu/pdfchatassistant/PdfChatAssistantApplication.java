package com.subbu.pdfchatassistant;

import com.subbu.pdfchatassistant.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class PdfChatAssistantApplication {

	public static void main(String[] args) {
		SpringApplication.run(PdfChatAssistantApplication.class, args);
	}

}
