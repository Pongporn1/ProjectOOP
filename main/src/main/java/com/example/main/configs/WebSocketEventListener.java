package com.example.main.configs;

import com.example.main.models.ChatMessage;
import com.example.main.models.MessageType;
import com.example.main.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@AllArgsConstructor
public class WebSocketEventListener {
    private final SimpMessageSendingOperations messagingTemplate;
    private final UserRepository userRepository;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event){
        System.out.println("GOOD CONNECT");
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        userRepository.removeUserBySessionId(event.getSessionId());
        messagingTemplate.convertAndSend("/topic/users", userRepository.getOnlineUser());
        if(username  != null) {
            ChatMessage chatMessage = ChatMessage.buildChatmessage(username + "has left the chat." , username , MessageType.LEAVE);
            userRepository.removeUser(username);
            messagingTemplate.convertAndSend("/topic/messages" , chatMessage);
        }
    }

    @EventListener
    public void handleWebSocketConnectedListener(SessionConnectEvent event){
        System.out.println("BAD CONNECT");

        userRepository.addOnlineUser();
        System.out.println(userRepository.getOnlineUser());
        messagingTemplate.convertAndSend("/topic/users", userRepository.getOnlineUser());
    }
}

