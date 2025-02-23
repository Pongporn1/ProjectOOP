package com.example.main.repositories;

import GameState.GameMode.DuelMode;
import GameState.GameMode.Game;
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

    public RoomItem createRoom(String id) {
        RoomItem room = RoomItem.buildRoom(id);
        room.loadConfiguration();
        rooms.put(id, room);
        return room;
    }

    public RoomItem getRoom(String id) {
        return rooms.get(id);
    }
}
