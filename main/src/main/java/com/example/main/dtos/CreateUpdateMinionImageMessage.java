package com.example.main.dtos;

import lombok.Getter;

@Getter
public class CreateUpdateMinionImageMessage {
    private String roomId;
    private int imageId;
    private int index;
}
