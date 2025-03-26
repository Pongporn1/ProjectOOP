package com.example.main.dtos;

import lombok.Getter;

@Getter
public class CreateSpawnMinionMessage {
    String roomId;
    String minionType;
    Integer row;
    Integer col;
}
