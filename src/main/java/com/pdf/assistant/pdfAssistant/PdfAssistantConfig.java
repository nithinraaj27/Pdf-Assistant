package com.pdf.assistant.pdfAssistant;


import com.datastax.oss.driver.api.core.CqlSession;
import dev.langchain4j.chain.ConversationalRetrievalChain;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.retriever.EmbeddingStoreRetriever;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.cassandra.AstraDbEmbeddingConfiguration;
import dev.langchain4j.store.embedding.cassandra.AstraDbEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.net.InetSocketAddress;
import java.nio.file.Paths;

@Configuration
public class PdfAssistantConfig {

    // Injecting values from application.properties
    @Value("${astra.token}")
    private String astraToken;

    @Value("${astra.database-id}")
    private String databaseID;

    @Value("${astra.database-region}")
    private String databaseRegion;

    @Value("${astra.keyspace}")
    private String keyspace;

    @Value("${astra.table}")
    private String table;

    @Value("${astra.dimension}")
    private int dimension;

    @Value("${spring.ai.openai.api-key}")
    private String openAiApiKey;

    @Value("${astra.clientID}")
    private String clientid;

    @Value("${astra.secret}")
    private String secret;


    @Bean
    public EmbeddingModel embeddingModel() {
        // Using AllMiniLmL6V2EmbeddingModel as specified in the original file
        return new AllMiniLmL6V2EmbeddingModel();
    }

    @Bean
    public AstraDbEmbeddingStore astraDbEmbeddingStore() {
        // Building configuration using the injected properties
        return new AstraDbEmbeddingStore(
                AstraDbEmbeddingConfiguration.builder()
                        .token(astraToken)
                        .databaseId(databaseID)
                        .databaseRegion(databaseRegion)
                        .keyspace(keyspace)
                        .table(table)
                        .dimension(dimension)
                        .build()
        );
    }

    @Bean
    public EmbeddingStoreIngestor embeddingStoreIngestor() {
        return EmbeddingStoreIngestor.builder()
                .documentSplitter(DocumentSplitters.recursive(300, 0))
                .embeddingModel(embeddingModel())
                .embeddingStore(astraDbEmbeddingStore())
                .build();
    }

    @Bean
    public ConversationalRetrievalChain conversationalRetrievalChain() {

        // Using the injected OpenAI API key
        return ConversationalRetrievalChain.builder()
                .chatLanguageModel(OpenAiChatModel.withApiKey(openAiApiKey))
                .retriever(EmbeddingStoreRetriever.from(astraDbEmbeddingStore(), embeddingModel()))
                .build();
    }

    @Bean
    public CqlSession cqlSession() {
        String scbDir = System.getProperty("user.home") + "/.astra/scb";

        File[] scbFiles = new File(scbDir).listFiles((d, name) -> name.startsWith("scb_") && name.endsWith(".zip"));
        if (scbFiles == null || scbFiles.length == 0) {
            throw new RuntimeException("No SCB file found in " + scbDir);
        }

        File scbFile = scbFiles[0];
        System.out.println("Using SCB file: " + scbFile.getAbsolutePath());

        // Build session using SCB (driver reads token automatically)
        return CqlSession.builder()
                .withAuthCredentials(clientid, secret)
                .withCloudSecureConnectBundle(scbFile.toPath())
                .build();
    }


    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("https://df4a7d82-9699-4269-a9d6-aafb59631cc1-us-east-2.apps.astra.datastax.com")
                .build();
    }


    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:5173") // frontend origin
                        .allowedMethods("GET", "POST", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
