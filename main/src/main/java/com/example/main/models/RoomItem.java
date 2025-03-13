package com.example.main.models;

import AST.Strategy;
import DataStructure.Pair;
import GameState.Game.Hex;
import GameState.Game.Minion;
import GameState.GameMode.AutoMode;
import GameState.GameMode.DuelMode;
import GameState.GameMode.Game;
import GameState.GameMode.SoloMode;
import GameState.Leader.Leader;
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

    private MinionHex hexToMinionHex(Hex hex){

        return new MinionHex(hex.hasMinionOnHex() ? hex.getMinionOnHex().getMinionType():"None", hex.hasMinionOnHex() ? hex.getMinionOnHex().getOwner().getLeaderName() : "None");
    }

//    public GameData getGameData(){
//        Hex[][] board = game.getBoard();
//        String[][] owner = (String[][]) Arrays.stream(board).map((r) -> Arrays.stream(r).map((c) -> c.getLeader().getLeaderName()).toArray()).toArray();
//        MinionHex[][] minionsHex = (MinionHex[][]) Arrays.stream(board).map((r) -> Arrays.stream(r).map(this::hexToMinionHex).toArray()).toArray();
//        Leader leader1 = game.getFirstLeader();
//        Leader leader2 = game.getSecondLeader();
//        GameData gameData = new GameData(owner, minionsHex, new HashMap<>(){{
//            put(leader1.getLeaderName(), new LeaderData(leader1.getLeaderName(), leader1.getBudget(), leader1.getMinionList().size(), leader1.getOwnHexAmount()));
//            put(leader2.getLeaderName(), new LeaderData(leader2.getLeaderName(), leader2.getBudget(), leader2.getMinionList().size(), leader2.getOwnHexAmount()));
//        }});
//        return gameData;
//    }

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
        return new RoomItem(id, new HashMap<>(), new HashMap<>(),new ArrayList<>() {{
            add(new MinionItem("Spear", 10, "", 1));
        }}
                , new ArrayList<>(), null, gameMode,"", false, "", false);
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
