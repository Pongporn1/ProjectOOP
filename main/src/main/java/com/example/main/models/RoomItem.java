package com.example.main.models;

import game.AST.Strategy;
import game.DataStructure.Pair;
import game.GameState.Game.Hex;
import game.GameState.GameMode.AutoMode;
import game.GameState.GameMode.DuelMode;
import game.GameState.GameMode.Game;
import game.GameState.GameMode.SoloMode;
import game.GameState.Leader.Leader;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
public class RoomItem {
    private String id;
    private Map<String, Long> config;
    private Map<String, Pair<Integer, String>> editing;
    private List<MinionItem> minions;
    private List<String> users;
    private Game game;
    private String gameMode;

    private String leader1;
    private boolean leader1Confirm;
    private String leader2;
    private boolean leader2Confirm;


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

    public void changeMinionName(int index, String name) {
        minions.get(index).setName(name);
    }



    public void createGame(Map<String, Pair<Strategy, Long>> minions) {
        if(gameMode.equals("auto")){
            game = new AutoMode(minions) ;
        }
        if(gameMode.equals("solitaire")){
            game = new SoloMode(minions, leader1);
        }
        if(gameMode.equals("duel")){
            game = new DuelMode(minions, leader1, leader2);
        }
        game.setConfig(config);
    }

    public List<MinionItem> getMinionsList(){
        return minions;
    }

    public void changeMinionDefense(int index, long defense) {
        minions.get(index).setDefense(defense);
    }

    public void changeMinionScript(int index, String script) {
        minions.get(index).setScript(script);
    }

    public void changeMinionImage(int index, int imageId) {
        minions.get(index).setImageId(imageId);
    }

    public void setSetting(String setting, Long value) {
        if (config.containsKey(setting)) config.put(setting, value);
    }

    public List<RoomEditing>  setEditing(String player, int index, String field) {
        List<RoomEditing> editList = new ArrayList<>();
        editing.put(player, Pair.of(index, field));
        for(Map.Entry<String, Pair<Integer, String>> entry : editing.entrySet()) {
            editList.add(new RoomEditing(entry.getKey(), entry.getValue().getFirst(), entry.getValue().getSecond()));
        }
        return editList;
    }

    public static RoomItem buildRoom(String id, String gameMode) {
        String leader1Name = gameMode.equals("auto") ? "Bot1" : "";
        String leader2Name = gameMode.equals("auto") ? "Bot2" : gameMode.equals("solitaire") ? "Bot" : "";
        return new RoomItem(id, new HashMap<>(), new HashMap<>(),new ArrayList<>() {{
            add(new MinionItem("Spear", 10, "", 1));
        }}
                , new ArrayList<>(), null, gameMode,leader1Name, false, leader2Name, false);
    }

    public void joinRoom(String username) {
        users.add(username);
        if (Objects.equals(leader1, "")) leader1 = username;
        else if (Objects.equals(leader2, "")) leader2 = username;
    }

    public int getUserAmount() {
        return users.size();
    }

    public  MinionItem removeMinion(int index){
        return minions.remove(index);
    }

    public void addMinion(MinionItem minion) {
        minions.add(minion);
    }

    public boolean isConfirm(){
        if(!gameMode.equals("duel")) return leader1Confirm;
        return leader1Confirm && leader2Confirm;
    }

    public void resetConfirm(){
        leader1Confirm = false;
        leader2Confirm = false;
    }
}
