package com.example.main.repositories;

import game.DataStructure.Pair;
import game.GameState.Game.Minion;
import game.GameState.GameMode.DuelMode;
import game.GameState.GameMode.Game;
import game.GameState.Leader.Leader;
import com.example.main.models.GameData;
import com.example.main.models.MinionItem;
import com.example.main.models.RoomItem;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository("GameRoomRepository")
public class GameRoomRepository {
    Map<String, RoomItem> rooms;

    public GameRoomRepository() {
        rooms = new HashMap<>();
    }

    public RoomItem createRoom(String id, String gameMode) {
        RoomItem room = RoomItem.buildRoom(id, gameMode);
        room.loadConfiguration();
        rooms.put(id, room);
        return room;
    }

    public RoomItem getRoom(String id) {
        return rooms.get(id);
    }

    public boolean joinRoom(String id, String username) {
        RoomItem room = getRoom(id);
        if(room == null || room.getUserAmount() > 1 || !room.getGameMode().equals("duel")) return false;
        room.joinRoom(username);
        return true;
    }

    public boolean confirmInRoom(String roomId, String username, boolean confirmed) {
        RoomItem room = getRoom(roomId);
        if(room != null) {
            if(room.getLeader1().equals(username)) {
                room.setLeader1Confirm(confirmed);
                return room.isLeader1Confirm();
            }else if(room.getLeader2().equals(username)) {
                room.setLeader2Confirm(confirmed);
                return room.isLeader2Confirm();
            }
        }
        return false;
    }

    public boolean addMinion(String id) {
        if(!rooms.containsKey(id)) return false;
        RoomItem room = rooms.get(id);
        if(room.getMinions().size() >= 5) return false;
        room.addMinion(new MinionItem("", 0, "", 0));
        return true;
    }

    public boolean removeMinion(String id, int index) {
        RoomItem room = getRoom(id);
        if(room == null) return false;
        if(room.getMinions().size() <= 1) return false;
        return room.removeMinion(index) != null;
    }

    public Pair<Boolean, List<GameData>> spawnMinion(String roomId, String owner, String minionType, int row, int col){
        RoomItem room = getRoom(roomId);
        Pair<Integer, String> state = room.getGame().getState();
        System.out.println("State");
        System.out.println(state.getFirst());
        System.out.println(state.getSecond());
        if(room == null) return Pair.of(false, null);
        Leader leader = null;
        if(room.getLeader1().equals(owner)) {
            if(state.getFirst() != 1 || !(state.getSecond().equals("spawning") || state.getSecond().equals("first_spawning"))) return Pair.of(false, null);
            leader = room.getGame().getFirstLeader();
        }else if(room.getLeader2().equals(owner)) {
            if(state.getFirst() != 2 || !(state.getSecond().equals("spawning") || state.getSecond().equals("first_spawning"))) return Pair.of(false, null);
            leader = room.getGame().getSecondLeader();
        }
        Pair<Boolean, List<GameData>> result = leader.spawnMinionAt(Pair.of((long) row, (long) col), minionType);
        return result;
    }

    public Pair<Boolean, List<GameData>> buyHex(String roomId, int row, int col, String leaderName){
        RoomItem room = getRoom(roomId);
        Pair<Integer, String> state = room.getGame().getState();
        if(room == null) return Pair.of(false, null);
        Leader leader = null;
        if(room.getLeader1().equals(leaderName)) {
            if(state.getFirst() != 1 || !state.getSecond().equals("buying")) return Pair.of(false, null);
            leader = room.getGame().getFirstLeader();
        }else if(room.getLeader2().equals(leaderName)) {
            if(state.getFirst() != 2 || !state.getSecond().equals("buying")) return Pair.of(false, null);
            leader = room.getGame().getSecondLeader();
        }
        Pair<Boolean, List<GameData>> result = leader.buyHex(Pair.of((long) row, (long) col));
        return result;
    }

    public GameData getGameData(String roomId) {
        RoomItem room = getRoom(roomId);
        if(room == null) return null;
        return room.getGame().getGameData();
    }

    public Pair<Boolean, List<GameData>> skipState(String roomId, String leaderName) {
        RoomItem room = getRoom(roomId);
        Pair<Integer, String> state = room.getGame().getState();
        if(room == null) return Pair.of(false, null);
        Leader leader = null;
        if(room.getLeader1().equals(leaderName)) {
            if(state.getFirst() != 1) return Pair.of(false, null);
            leader = room.getGame().getFirstLeader();
        }else if(room.getLeader2().equals(leaderName)) {
            if(state.getFirst() != 2) return Pair.of(false, null);
            leader = room.getGame().getSecondLeader();
        }
        return Pair.of(true, room.getGame().skipState());
    }

    public List<GameData> startGame(String roomId, SimpMessageSendingOperations sender) {
        RoomItem room = getRoom(roomId);
        if(room == null) return null;
        room.getGame().setSender(sender);
        room.getGame().setRoomId(roomId);
        if(room.getGameMode().equals("auto")) {
            List<GameData> states = room.getGame().processGame();
            return states;
        }
        room.getGame().sendGameData(roomId);
        return Arrays.asList(room.getGame().getGameData());
    }
}
