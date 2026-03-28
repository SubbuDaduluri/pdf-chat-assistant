# 📄 PDF Chat Assistant (Spring AI + Ollama + Qdrant)

The **PDF Chat Assistant** is a full-stack **RAG (Retrieval-Augmented Generation)** application that allows users to upload a PDF and interactively ask questions about its content.

It leverages **Spring Boot + Spring AI + Ollama (local LLM) + Qdrant vector DB** to deliver fast, private, and context-aware responses.

---

## ✨ Features

- 📄 **PDF Upload & Processing** – Extracts text and converts into embeddings
- 🧠 **RAG Pipeline** – Retrieves relevant chunks for accurate answers
- 🤖 **Local LLM Support (Ollama)** – Run fully offline (no API cost)
- ☁️ **Optional OpenAI Support** – Switch to cloud LLM easily
- 🗃️ **Vector Search (Qdrant)** – Fast similarity search
- 💬 **Session-based Chat Memory** – Maintains conversational context
- ⚡ **Smart Query Rewriting** – Improves retrieval quality automatically

---

## 🛠️ Technology Stack

| Component        | Technology                         | Role |
|-----------------|-----------------------------------|------|
| Backend         | Spring Boot, Java                 | API, orchestration |
| AI Layer        | Spring AI                         | RAG pipeline |
| LLM             | Ollama / OpenAI                   | Answer generation |
| Embedding Model | nomic-embed-text (Ollama)         | Text embeddings |
| Vector DB       | Qdrant                            | Stores embeddings |
| PDF Processing  | Spring AI PDF Reader              | Extracts document text |

---

## 🚀 Getting Started

### ✅ Prerequisites

- Java 21
- Spring Boot 3.5.x
- Maven
- Docker (for Qdrant)
- Ollama installed locally

---

## 🧠 Setup Ollama (Local LLM)

Install and run Ollama:

```bash
ollama serve

Pull required models:

ollama pull llama3
ollama pull nomic-embed-text


# 📄 PDF Chat Assistant (Spring AI + Ollama + Qdrant)

The **PDF Chat Assistant** is a full-stack **RAG (Retrieval-Augmented Generation)** application that allows users to upload a PDF and interactively ask questions about its content.

It leverages **Spring Boot + Spring AI + Ollama (local LLM) + Qdrant vector DB** to deliver fast, private, and context-aware responses.

---

## 🗃️ 2️⃣ Start Qdrant (Vector DB)

Run Qdrant using Docker:

```bash
docker run -p 6333:6333 -p 6334:6334 qdrant/qdrant
```

---

## ⚙️ 3️⃣ Configure Application

Update `application.yml`:

```yaml
server:
  port: 8080

spring:
  application:
    name: pdf-chat-assistant

  ai:
    ollama:
      base-url: http://localhost:11434
      chat:
        options:
          model: llama3
      embedding:
        options:
          model: nomic-embed-text

    openai:
      api-key: YOUR_OPENAI_API_KEY   # optional
      chat:
        options:
          model: gpt-4o-mini
      embedding:
        enabled: false

    vectorstore:
      qdrant:
        host: localhost
        port: 6334
        collection-name: pdf_docs
        initialize-schema: true

app:
  llm:
    provider: ollama   # or openai
  rag:
    top-k: 5
    chunk-size: 1000
    chunk-overlap: 200
```

---

## ▶️ 4️⃣ Run the Application

```bash
./mvnw clean install
./mvnw spring-boot:run
```

Server starts at:

```
http://localhost:8080
```

---

## 🔌 API Endpoints

### 📄 Upload PDF
POST /api/pdf/upload

### 💬 Chat with PDF
POST /api/chat

Request:
```json
{
  "sessionId": "user1",
  "message": "What is the candidate's name?"
}
```

Response:
```json
{
  "response": "From the resume, the name is 👉 John Doe"
}
```

### 🕘 Get Chat History
GET /api/chat/history?sessionId=user1

---

## 🧩 Architecture Overview

### 🔄 RAG Flow

- PDF Upload → Extract text using PagePdfDocumentReader
- Chunking → TokenTextSplitter
- Embedding → nomic-embed-text
- Storage → Qdrant
- Query → Retrieve + LLM Answer

---

## 🧠 Key Components

- ChatServiceImpl → Query rewriting + response generation
- IngestionServiceImpl → PDF parsing + embedding
- RetrievalServiceImpl → Vector search
- MemoryServiceImpl → Chat history

---

## 💡 Design Highlights

- Offline support via Ollama
- Fast vector retrieval
- Context-aware responses
- Session memory

---

## 🚀 Future Enhancements

- Multi-PDF support
- Redis memory
- Streaming responses
- Frontend UI
- Kubernetes deployment

---

## 🧪 Use Cases

- Resume analysis
- Legal Q&A
- Research assistant

---

## 👨‍💻 Author

Subramanyam D
