package com.example.main.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class MinionErrMessage {
    private int index;
    private String field;
    private String message;
    private RoomItem room;
}
