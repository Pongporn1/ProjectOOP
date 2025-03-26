package com.example.main.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatMessage {
    private String message;
    private String sender;
    private MessageType type;
    private LocalDateTime timestamp;

    public static ChatMessage buildChatmessage(String message , String sender , MessageType messageType){
        return new ChatMessage(message,sender,messageType , LocalDateTime.now());
    }
}
