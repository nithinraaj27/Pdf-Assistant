package com.pdf.assistant.pdfAssistant;

import org.springframework.boot.SpringApplication;

public class TestPdfAssistantApplication {

	public static void main(String[] args) {
		SpringApplication.from(PdfAssistantApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
