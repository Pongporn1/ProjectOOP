package com.example.main.dtos;

import lombok.Getter;

@Getter
public class CreateConfirmConfigMessage {
    private String username;
    private String roomId;
    private boolean confirmed;
}
