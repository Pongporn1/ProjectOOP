package com.example.main.dtos;

import com.example.main.models.MessageType;
import lombok.Getter;

@Getter
public class CreateChatMessageBody {
    private String message;
    private String sender;
    private MessageType type;
}
