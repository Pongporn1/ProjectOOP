package com.example.main.repositories;

import GameState.GameMode.DuelMode;
import GameState.GameMode.Game;
import GameState.Leader.Leader;
import com.example.main.models.GameData;
import com.example.main.models.MinionItem;
import com.example.main.models.RoomItem;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public boolean spawnMinion(String roomId, String owner, String minionType, int row, int col){
        RoomItem room = getRoom(roomId);
        if(room == null) return false;
        Leader leader = null;
        if(room.getLeader1().equals(owner)) {
            leader = room.getGame().getFirstLeader();
        }else if(room.getLeader2().equals(owner)) {
            leader = room.getGame().getSecondLeader();
        }
        return null != room.getGame().spawnMinion(row, col, minionType, leader);
    }

    public GameData getGameData(String roomId) {
        RoomItem room = getRoom(roomId);
        return room.getGameData();
    }
}
