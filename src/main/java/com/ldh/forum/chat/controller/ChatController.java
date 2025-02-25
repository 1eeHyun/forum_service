package com.ldh.forum.chat.controller;

import com.ldh.forum.chat.model.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashSet;
import java.util.Set;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final Set<String> onlineUsers = new HashSet<>();

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }


    @MessageMapping("/addUser")
    @SendTo("/topic/onlineUsers")
    public Set<String> addUser(ChatMessage message) {
        onlineUsers.removeIf(user -> user.startsWith("User_") && !user.equals(message.getSender()));
        onlineUsers.add(message.getSender());
        return onlineUsers;
    }

    @MessageMapping("/removeUser")
    @SendTo("/topic/onlineUsers")
    public Set<String> removeUser(ChatMessage message) {
        onlineUsers.remove(message.getSender());
        return onlineUsers;
    }


    @MessageMapping("/sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        return chatMessage;
    }

}
