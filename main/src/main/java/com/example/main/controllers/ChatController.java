package com.example.main.controllers;

import com.example.main.dtos.CreateChatMessageBody;
import com.example.main.models.ChatMessage;
import com.example.main.models.MessageType;
import com.example.main.models.User;
import com.example.main.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
    private final SimpMessageSendingOperations messagingTemplate;
    private final UserRepository userRepository;

    @Autowired
    public ChatController(SimpMessageSendingOperations messagingTemplate, @Qualifier("UserRepository") UserRepository userRepository){
        this.messagingTemplate = messagingTemplate;
        this.userRepository = userRepository;
    }

    @MessageMapping("/chat/sendMessage")
    @SendTo("/topic/messages")
    public ChatMessage sendMessage(CreateChatMessageBody createChatMessageBody){
        String username = createChatMessageBody.getSender();
        String message = createChatMessageBody.getMessage();
        MessageType messageType = createChatMessageBody.getType();
        return ChatMessage.buildChatmessage(message,username,messageType);
    }

    @MessageMapping("/chat/getUserAmount")
    @SendTo("/topic/users")
    public int getUserAmount(){
        messagingTemplate.convertAndSend("/topic/users", userRepository.getOnlineUser());
        System.out.println("CALL GET USER AMOUNT");
        return userRepository.getOnlineUser();
    }


    @MessageMapping("/chat/addUser")
    @SendToUser("/queue/connected")
    public User addUser(CreateChatMessageBody createChatMessageBody , SimpMessageHeaderAccessor headerAccessor){
        String username = "Dragon";//createChatMessageBody.getSender();
        User user = new User(username);
        String message = createChatMessageBody.getMessage();
        userRepository.addUser(user);
        MessageType messageType = createChatMessageBody.getType();
        headerAccessor.getSessionAttributes().put("username" , username);
        messagingTemplate.convertAndSend("/topic/users", userRepository.getOnlineUser());
        messagingTemplate.convertAndSend("/topic/messages" , ChatMessage.buildChatmessage(message,username,messageType));
        return user;
    }

}
