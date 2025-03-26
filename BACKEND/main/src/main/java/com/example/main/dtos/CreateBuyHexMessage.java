package com.example.main.dtos;

import lombok.Getter;

@Getter
public class CreateBuyHexMessage {
    private int row;
    private int col;
    private String roomId;
}
