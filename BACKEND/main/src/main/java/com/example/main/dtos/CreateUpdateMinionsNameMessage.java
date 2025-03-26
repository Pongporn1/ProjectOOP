package com.example.main.dtos;

import lombok.Getter;

@Getter
public class CreateUpdateMinionsNameMessage {
    private String roomId;
    private String minionName;
    private int index;
}
