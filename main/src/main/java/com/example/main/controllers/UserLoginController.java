package com.example.main.controllers;

import com.example.main.dtos.CreateChatMessageBody;
import com.example.main.dtos.CreateUserLoginBody;
import com.example.main.models.ChatMessage;
import com.example.main.models.MessageType;
import com.example.main.models.User;
import com.example.main.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Controller
public class UserLoginController {
    private final SimpMessageSendingOperations messagingTemplate;
    private final UserRepository userRepository;

    @Autowired
    public UserLoginController(SimpMessageSendingOperations messagingTemplate, @Qualifier("UserRepository") UserRepository userRepository){
        this.messagingTemplate = messagingTemplate;
        this.userRepository = userRepository;
    }

    @MessageMapping("/login")
    public void login(CreateUserLoginBody createUserLoginBody, SimpMessageHeaderAccessor headerAccessor){
        System.out.println("Login");
        if (userRepository.haveUsername(createUserLoginBody.getUsername())){
            User user = new User("Username already exists");
            messagingTemplate.convertAndSend("/topic/connected-"+createUserLoginBody.getUsername(),user );
            return;
        }
        User user = new User(createUserLoginBody.getUsername());
        userRepository.addUser(user);
        messagingTemplate.convertAndSend("/topic/connected-"+createUserLoginBody.getUsername(),user );
    }

    public void confirmLogin(){

    }
}