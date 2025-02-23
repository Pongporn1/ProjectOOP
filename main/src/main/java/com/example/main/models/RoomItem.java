package com.example.main.models;

import DataStructure.Pair;
import GameState.Game.Minion;
import GameState.GameMode.Game;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
public class RoomItem {
    private String id;
    private Map<String, Long> config;
    private Map<String, Pair<Minion, Long>> minions;
    private List<String> users;
    private Game game;

    public void loadConfiguration() {
        try {
            File myObj = new File("configuration.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                config.put(data.split("=")[0], Long.parseLong(data.split("=")[1]));
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());

        }
    }

    public void setSetting(String setting, Long value) {
        if(config.containsKey(setting)) config.put(setting, value);
    }

    public static RoomItem buildRoom(String id){
        return new RoomItem(id, new HashMap<>(), new HashMap<>(), new ArrayList<>(), null);
    }
}
