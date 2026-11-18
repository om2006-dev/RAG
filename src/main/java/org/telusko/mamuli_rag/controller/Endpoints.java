package org.telusko.mamuli_rag.controller;

import org.springframework.web.bind.annotation.*;
import org.telusko.mamuli_rag.kaam.ChatService;

@RestController
@RequestMapping("/rag")
public class Endpoints {

    private final ChatService chatService;

    public Endpoints(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/query")
    public String response(@RequestBody String message) {
        return chatService.rag_service(message);
    }
}
