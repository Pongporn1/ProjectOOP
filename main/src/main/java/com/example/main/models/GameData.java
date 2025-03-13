package com.example.main.models;

import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class GameData {
    private String[][] owner;
    private MinionHex[][] minions;
    Map<String, LeaderData> leaders;
}
