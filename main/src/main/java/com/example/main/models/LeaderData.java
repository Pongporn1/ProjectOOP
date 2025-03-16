package com.example.main.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class LeaderData {
    private String state;
    private String name;
    private double budget;
    private int minionAmount;
    private int hexAmount;
    private List<HexPos> buyableHexes;
}
