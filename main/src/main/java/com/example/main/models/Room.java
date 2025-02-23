package com.example.main.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class Room {
    private String id;

    public static Room buildRoom(String id){
        return new Room(id);
    }
}
