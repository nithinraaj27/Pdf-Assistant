package com.pdf.assistant.pdfAssistant;

import dev.langchain4j.chain.ConversationalRetrievalChain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ConversationalRetrievalChain conversationalRetrievalChain;

    @PostMapping()
    public String chatWithPdf(@RequestBody String text) {

        String systemPrompt = """
        You are an expert assistant that answers questions *only* based on the provided PDF context.
        If no relevant information is found in the document, respond exactly with:
        "Sorry, I couldn’t find relevant information in the uploaded PDF."
        """;

        String userQuery = """
        System: %s
        User: %s
        """.formatted(systemPrompt, text);

        var answer = conversationalRetrievalChain.execute(userQuery);

        log.info("Question: {}", text);
        log.info("Answer: {}", answer);

        return answer;
    }

    @GetMapping("/")
    public String chatWithPdf() {
        return "Working Fine, check the Post method";
    }
}