package com.pdf.assistant.pdfAssistant;

import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;



@RestController
public class PdfUploadController {

    private final EmbeddingStoreIngestor embeddingStoreIngestor;
    private final PdfProcessingService pdfProcessingService;

    public PdfUploadController(EmbeddingStoreIngestor embeddingStoreIngestor, PdfProcessingService pdfProcessingService) {
        this.embeddingStoreIngestor = embeddingStoreIngestor;
        this.pdfProcessingService = pdfProcessingService;
    }

    @PostMapping("/upload-pdf")
    public ResponseEntity<String> upload(MultipartFile file) {
        if(file.isEmpty() || file.getContentType() == null || !file.getContentType().equalsIgnoreCase("application/pdf")) {
            return ResponseEntity.badRequest().body("Please upload a valid PDF File");
        }
        try{

            pdfProcessingService.clearPreviousPdfData();
            System.out.println("All Rows Deleted Successfully");
            ApachePdfBoxDocumentParser parser = new ApachePdfBoxDocumentParser();

            Document document = parser.parse(file.getInputStream());

            embeddingStoreIngestor.ingest(document);

            // Optional: Log success or return file details
            String message = String.format("Successfully processed and ingested PDF: %s (Size: %d bytes)",
                    file.getOriginalFilename(), file.getSize());

            return ResponseEntity.ok(message);
        } catch(Exception e){
            return ResponseEntity.internalServerError().body("Error during RAG Ingestion " + e.getMessage());
        }
    }
}
