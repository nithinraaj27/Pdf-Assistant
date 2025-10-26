package com.pdf.assistant.pdfAssistant;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration.class, scanBasePackages = "com.pdf.assistant.pdfAssistant")
public class PdfAssistantApplication {

	public static void main(String[] args) {
		SpringApplication.run(PdfAssistantApplication.class, args);
	}

}
