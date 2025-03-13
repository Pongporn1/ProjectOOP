package com.example.main.dtos;

import lombok.Getter;

@Getter
public class CreateRoomEditMessage {
    private String roomId;
    private int index;
    private String field;
}
