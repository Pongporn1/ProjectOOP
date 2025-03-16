package com.example.main.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
@Setter
public class GameData {
    private int turn;
    private String[][] owner;
    private MinionHex[][] minionHexes;
    private Map<String, LeaderData> leaders;

}
