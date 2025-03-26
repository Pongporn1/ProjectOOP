package com.example.main.controllers;

import game.DataStructure.Pair;
import game.GameState.GameMode.Game;
import com.example.main.dtos.CreateBuyHexMessage;
import com.example.main.dtos.CreateJoinRoomMessageBody;
import com.example.main.dtos.CreateSpawnMinionMessage;
import com.example.main.models.GameData;
import com.example.main.models.RoomItem;
import com.example.main.repositories.GameRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.broker.AbstractBrokerMessageHandler;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.List;

@Controller
public class GameStateController {
    private final GameRoomRepository gameRoomRepository;
    private final SimpMessageSendingOperations messagingTemplate;


    @Autowired
    public GameStateController(@Qualifier("GameRoomRepository") GameRoomRepository gameRoomRepository, SimpMessageSendingOperations messagingTemplate ) {
        this.gameRoomRepository = gameRoomRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/game/spawn")
    public void SpawnMinion(CreateSpawnMinionMessage createSpawnMinionMessage, SimpMessageHeaderAccessor headerAccessor) {
        String minionType = createSpawnMinionMessage.getMinionType();
        int row = createSpawnMinionMessage.getRow();
        int col = createSpawnMinionMessage.getCol();
        String roomId = createSpawnMinionMessage.getRoomId();
        String owner = headerAccessor.getNativeHeader("username").get(0);
        Pair<Boolean, List<GameData>> m = gameRoomRepository.spawnMinion(roomId, owner, minionType, row, col);

        System.out.println(roomId);
        System.out.println(owner);
        System.out.println(minionType);
        System.out.println(row);
        System.out.println(col);
        if(!m.getFirst()){
            System.out.println("Error");
        }
        GameData gameData = gameRoomRepository.getGameData(roomId);

        messagingTemplate.convertAndSend("/topic/game-"+roomId, gameData, new HashMap<>(){{put("command", "update");}});
        messagingTemplate.convertAndSend("/topic/game-"+roomId, m.getSecond(), new HashMap<>(){{put("command", "test");}});
    }

    @MessageMapping("/game/start")
    public void StartGame(CreateJoinRoomMessageBody joinRoomMessageBody, SimpMessageHeaderAccessor headerAccessor) {
        System.out.println("Start Game: " + headerAccessor.getNativeHeader("username").get(0) + " on " + joinRoomMessageBody.getRoomId() );
        GameData gameData = gameRoomRepository.getGameData(joinRoomMessageBody.getRoomId());
        RoomItem room = gameRoomRepository.getRoom(joinRoomMessageBody.getRoomId());
        messagingTemplate.convertAndSend("/topic/user-"+headerAccessor.getNativeHeader("username").get(0), room.getMinions(), new HashMap<>(){{put("command", "minionls");}});
        messagingTemplate.convertAndSend("/topic/user-"+headerAccessor.getNativeHeader("username").get(0), gameData, new HashMap<>(){{put("command", "init");}});
    }

    @MessageMapping("/game/buy")
    public void BuyHex(CreateBuyHexMessage createBuyHexMessage, SimpMessageHeaderAccessor headerAccessor){
        int row = createBuyHexMessage.getRow();
        int col = createBuyHexMessage.getCol();
        String roomId = createBuyHexMessage.getRoomId();
        String owner = headerAccessor.getNativeHeader("username").get(0);
        Pair<Boolean, List<GameData>> m =gameRoomRepository.buyHex(roomId, row, col, owner);
        GameData gameData = gameRoomRepository.getGameData(roomId);
        messagingTemplate.convertAndSend("/topic/game-"+roomId, gameData, new HashMap<>(){{put("command", "update");}});
        messagingTemplate.convertAndSend("/topic/game-"+roomId, m.getSecond(), new HashMap<>(){{put("command", "test");}});
    }

    @MessageMapping("/game/skip")
    public void Skip(CreateJoinRoomMessageBody skipMessageBody, SimpMessageHeaderAccessor headerAccessor) {
        List<GameData> m = gameRoomRepository.skipState(skipMessageBody.getRoomId());
        GameData gameData = gameRoomRepository.getGameData(skipMessageBody.getRoomId());
        messagingTemplate.convertAndSend("/topic/game-"+skipMessageBody.getRoomId(), gameData, new HashMap<>(){{put("command", "update");}});
        messagingTemplate.convertAndSend("/topic/game-"+skipMessageBody.getRoomId(), m, new HashMap<>(){{put("command", "test");}});
    }
}
