package com.example.main.dtos;

import lombok.Getter;

@Getter
public class CreateEditConfigMessageBody {
    private String roomId;
    private String setting;
    private Long value;
}
