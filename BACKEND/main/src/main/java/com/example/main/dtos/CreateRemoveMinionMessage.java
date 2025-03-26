package com.example.main.dtos;

import lombok.Getter;

@Getter
public class CreateRemoveMinionMessage {
    private String roomId;
    private int index;
}
