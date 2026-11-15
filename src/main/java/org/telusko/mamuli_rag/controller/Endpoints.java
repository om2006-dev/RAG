package org.telusko.mamuli_rag.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telusko.mamuli_rag.kaam.ChatService;

@RestController
@RequestMapping("/rag")
public class Endpoints {

    private final ChatService chatService;

    public Endpoints(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/query")
    public String response(@RequestBody String message) {
        return chatService.rag_service(message);
    }
}
