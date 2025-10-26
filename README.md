📄 PDF Chat Assistant (Full Stack RAG Application)The PDF Chat Assistant is a full-stack application that leverages Spring Boot, LangChain4j, and DataStax AstraDB to enable users to chat interactively with the content of an uploaded PDF file.✨ FeaturesFrontend: Minimalist, responsive UI with a subtle, professional design.Backend: Spring Boot application powered by LangChain4j for the RAG (Retrieval-Augmented Generation) pipeline.Vector Database: Uses DataStax AstraDB as a highly scalable vector store for PDF embeddings.LLM Integration: Utilizes OpenAI (or a compatible model) via the LangChain4j library for chat responses.UX: Includes an upload progress bar and a smooth loading animation in the chat window.🛠️ Technology StackComponentTechnologyRoleFrontendHTML, CSS, JavaScriptStatic client interface for file upload and chat.BackendSpring Boot, JavaHandles API requests, file processing, and orchestration.RAG/AILangChain4j, OpenAIFramework for chunking, embedding, and generating conversational answers.Vector DBDataStax AstraDBStores and retrieves vector embeddings of the PDF content.Embedding ModelAll-MiniLM-L6-V2Generates the numerical vector representations.🚀 Getting StartedPrerequisitesJava 17+ (Required for Spring Boot).Maven or Gradle (For build management).DataStax AstraDB Account: Requires a database ID, region, keyspace, and a secure connection bundle (SCB) file.OpenAI API Key (or an equivalent LLM provider).1. Frontend SetupThe frontend is a static application.Clone the repository:Bashgit clone https://github.com/nithinraaj27/Pdf-Assistant.git
cd Pdf-Assistant
Open locally: Open the index.html file in your web browser.2. Backend ConfigurationThe backend requires several secrets and configuration details. These must be set in your src/main/resources/application.properties file (or via environment variables).⚠️ SECURITY WARNING: Do NOT commit your actual secrets (keys/tokens) to Git. Use environment variables or ensure your .gitignore correctly excludes this file.Replace the placeholder values in application.properties:Properties# --- OPENAI CONFIGURATION ---
spring.ai.openai.api-key=YOUR_OPENAI_API_KEY_HERE

# --- ASTRADB CONFIGURATION ---
astra.token=YOUR_ASTRA_TOKEN
astra.database-id=YOUR_DATABASE_ID
astra.database-region=us-east-2
astra.keyspace=YOUR_KEYSPACE_NAME
astra.table=vector_table_name
astra.dimension=384

# SCB Credentials (used by CqlSession for administrative tasks)
astra.clientID=YOUR_ASTRA_CLIENT_ID
astra.secret=YOUR_ASTRA_SECRET
3. Build and Run the BackendBuild the project using Maven or Gradle.Bash# Using Maven
./mvnw clean install
Run the application:Bash./mvnw spring-boot:run
The server should start on the default port 8080.🔌 API EndpointsThe frontend is configured to communicate with the following two backend endpoints provided by your Spring Boot application:EndpointMethodRole/upload-pdfPOSTIngestion Pipeline. Receives the PDF file, clears any old data from AstraDB (via PdfProcessingService.clearPreviousPdfData()), chunks the document, generates embeddings, and stores the new data in the vector table./api/chatPOSTRAG Pipeline. Receives the text query, uses the ConversationalRetrievalChain to retrieve relevant document chunks from AstraDB, sends the context to the LLM, and returns the final answer.🧩 Backend Architecture (PdfAssistantConfig.java)The core RAG setup is managed through the @Configuration class, setting up the LangChain4j beans:BeanLangChain4j ComponentPurposeembeddingModelAllMiniLmL6V2EmbeddingModelConverts text into 384-dimensional vectors.astraDbEmbeddingStoreAstraDbEmbeddingStoreManages the connection and CRUD operations for the vector data in AstraDB.embeddingStoreIngestorEmbeddingStoreIngestorOrchestrates the chunking (recursive(300, 0)), embedding, and saving of PDF content upon upload.conversationalRetrievalChainConversationalRetrievalChainThe combined chain that handles the complete conversation flow (Retrieval $\rightarrow$ Context Augmentation $\rightarrow$ LLM Generation).cqlSessionCqlSessionDirect driver connection used by PdfProcessingService for administrative tasks (like TRUNCATE).💡 Frontend UX & DesignThe frontend implements a Subtle & Professional look:Accent Color: Muted Teal/Green (#008080).Loading: Uses a smooth Pulse Animation to indicate the bot is typing.Upload Progress: A simulated progress bar is shown in script.js from the moment the user clicks "Upload" until the server returns a final response.CORS ConfigurationThe PdfAssistantConfig.java includes a CORS configuration to ensure that the frontend (running on a different origin, e.g., http://localhost:5173 or a public domain) can communicate with the backend:Java@Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
            .allowedOrigins("http://localhost:5173") // CHANGE THIS to your frontend URL
            .allowedMethods("GET", "POST", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true);
}
Remember to update allowedOrigins to your public frontend URL when deploying.
