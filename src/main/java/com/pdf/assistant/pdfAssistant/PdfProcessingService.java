package com.pdf.assistant.pdfAssistant;


import com.datastax.oss.driver.api.core.CqlSession;
import com.fasterxml.jackson.databind.JsonNode;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;



@Service
public class PdfProcessingService {

    private final WebClient webClient;
    private final CqlSession cqlSession;

    @Value("${astra.token}")
    private String astraToken;

    @Value("${astra.table}")
    private String tableName;

    @Value("${astra.keyspace}")
    private String keyspace;

    public PdfProcessingService(WebClient webClient, CqlSession cqlSession) {
        this.webClient = webClient;
        this.cqlSession = cqlSession;
    }

    public void clearPreviousPdfData(){


        String cql = String.format("TRUNCATE %s.%s;", keyspace, tableName);
        cqlSession.execute(cql);
        System.out.println("Table " + keyspace + "." + tableName + " truncated successfully!");

        System.out.println("All Rows Deleted Successfully");
    }
}
