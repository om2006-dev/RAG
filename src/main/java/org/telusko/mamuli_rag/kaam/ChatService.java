package org.telusko.mamuli_rag.kaam;


import jakarta.annotation.PostConstruct;
import org.springframework.core.io.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ai.document.Document;


import java.util.ArrayList;
import java.util.List;

@Service
public class ChatService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    @Value("classpath*:knowledge/*.pdf")
    private Resource[] policy_files;

    public ChatService(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        this.chatClient = chatClientBuilder.build();
        this.vectorStore = vectorStore;
    }

    @PostConstruct
    public void load_knowledge()
    {
        List<Document> allChunks=new ArrayList<>();

        TokenTextSplitter splitter= TokenTextSplitter.builder()
                .withChunkSize(300)
                .build();

        for(Resource resource:policy_files)
        {
            PagePdfDocumentReader reader=new PagePdfDocumentReader(resource);
            List<Document> pages = reader.read();
            List<Document> chunks = splitter.apply(pages);
            allChunks.addAll(chunks);
        }
        vectorStore.add(allChunks);
    }
    public String rag_service(String message) {
        List<Document> relevantChunks = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(message)
                        .topK(4)
                        .build()
        );

        StringBuilder context = new StringBuilder();

        for (Document document : relevantChunks) {

            context.append(document.getText())
                    .append("\n\n");
        }

        String finalContext = context.toString();

        String prompt = """
                You are an AI customer support assistant for our e-commerce company.

                Answer the customer using ONLY the company information provided below.

                If the answer is not available in the provided information, say:
                "I don't have that information in the company documents."
                
                COMPANMY INFORMATION
                %s
                
                """.formatted(finalContext);

        return chatClient.prompt()
                .system(prompt)
                .user(message)
                .call()
                .content();
    }
}
