package com.example.main.dtos;

import lombok.Getter;

@Getter
public class CreateUpdateMinionDefenseMessage {
    private String roomId;
    private long defense;
    private int index;
}
