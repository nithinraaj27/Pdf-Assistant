# 📄 PDF Chat Assistant (Full Stack RAG Application)

The **PDF Chat Assistant** is a **full-stack RAG (Retrieval-Augmented Generation)** application that enables users to chat interactively with the content of an uploaded PDF file.  
It combines **Spring Boot**, **LangChain4j**, and **DataStax AstraDB** to deliver a smooth, AI-powered PDF exploration experience.

---

## ✨ Features

- 🖥️ **Frontend:** Minimalist, responsive UI inspired by ChatGPT — clean layout, professional tone.  
- ⚙️ **Backend:** Spring Boot app orchestrating file uploads, embedding generation, and chat responses.  
- 🧠 **RAG Pipeline:** Built using **LangChain4j** for document chunking, embedding, and context retrieval.  
- 🗃️ **Vector Store:** Powered by **DataStax AstraDB** for efficient storage and retrieval of embeddings.  
- 🤖 **LLM Integration:** Uses **OpenAI** (or a compatible model) for natural conversational responses.    

---

## 🛠️ Technology Stack

| Component | Technology | Role |
|------------|-------------|------|
| **Frontend** | HTML, CSS, JavaScript | Static interface for file upload and chat. |
| **Backend** | Spring Boot, Java | API endpoints, PDF ingestion, chat orchestration. |
| **RAG/AI Layer** | LangChain4j, OpenAI | Chunking, embedding, and context-based answer generation. |
| **Vector DB** | DataStax AstraDB | Stores and retrieves vector embeddings. |
| **Embedding Model** | All-MiniLM-L6-V2 | Generates 384-dimensional text embeddings. |

---

## 🚀 Getting Started

### ✅ Prerequisites

- **Java 17+** (Required for Spring Boot)
- **Maven or Gradle**
- **DataStax AstraDB** Account (Database ID, region, keyspace, and Secure Connect Bundle)
- **OpenAI API Key** (or equivalent LLM provider key)

---

### 1️⃣ Frontend Setup

The frontend is a static HTML + CSS + JS client.

```bash
# Clone the repository
git clone https://github.com/nithinraaj27/Pdf-Assistant.git
cd Pdf-Assistant

# Open the app
open index.html
```

---

### 2️⃣ Backend Configuration (`application.properties`)

Configure the backend with your credentials in  
`src/main/resources/application.properties`

```properties
# --- OPENAI CONFIGURATION ---
spring.ai.openai.api-key=YOUR_OPENAI_API_KEY_HERE

# --- ASTRADB CONFIGURATION ---
astra.token=YOUR_ASTRA_TOKEN
astra.database-id=YOUR_DATABASE_ID
astra.database-region=us-east-2
astra.keyspace=YOUR_KEYSPACE_NAME
astra.table=vector_table_name
astra.dimension=384

# SCB Credentials (used by CqlSession)
astra.clientID=YOUR_ASTRA_CLIENT_ID
astra.secret=YOUR_ASTRA_SECRET
```

---

### 3️⃣ Build and Run the Backend

Build and start the Spring Boot backend:

```bash
./mvnw clean install
./mvnw spring-boot:run
```

> The server will start on **http://localhost:8080**

---

## 🔌 API Endpoints

| Endpoint | Method | Description |
|-----------|---------|-------------|
| `/upload-pdf` | `POST` | Uploads a PDF, clears old data, chunks content, and stores embeddings in AstraDB. |
| `/api/chat` | `POST` | Accepts a query, retrieves context, sends it to the LLM, and returns an answer. |

---

## 🧩 Backend Architecture Overview

### ⚙️ `PdfAssistantConfig.java`
Sets up LangChain4j components:

| Bean | Component | Purpose |
|------|------------|----------|
| `embeddingModel` | AllMiniLmL6V2EmbeddingModel | Converts text into 384-dimensional vectors. |
| `astraDbEmbeddingStore` | AstraDbEmbeddingStore | Handles AstraDB vector storage and retrieval. |
| `embeddingStoreIngestor` | EmbeddingStoreIngestor | Manages chunking (recursive(300,0)) and embedding ingestion. |
| `conversationalRetrievalChain` | ConversationalRetrievalChain | Core RAG pipeline for retrieval + LLM response. |
| `corsConfigurer` | WebMvcConfigurer | Enables CORS for the frontend (e.g., `http://localhost:5173`). |

---

### 🧰 `PdfProcessingService.java`

Handles administrative PDF tasks:

```java
@Service
public class PdfProcessingService {

    // ... dependencies (WebClient, CqlSession)

    public void clearPreviousPdfData() {
        // Executes: TRUNCATE <keyspace>.<tableName>;
        // Clears the vector table before new PDF ingestion
        // ensuring only the current document is used for chat.
    }
}
```

---

## 💡 UI Design Highlights

- Inspired by **ChatGPT’s modern minimalism**  
- **Responsive layout** for both desktop and mobile  
- Smooth **“paper-flying” loading animation** while waiting for model responses  
- Clean **light color palette** with subtle shadows and rounded cards  

---

## 🧠 Future Enhancements

- Support for **multiple PDFs** per session  
- **User authentication** for persistent chat sessions  
- **Dark mode** toggle in frontend  
- Replace OpenAI with **local LLM (Ollama)** for offline usage  

