package com.example.main.controllers;

import GameState.GameMode.Game;
import com.example.main.dtos.CreateSpawnMinionMessage;
import com.example.main.models.GameData;
import com.example.main.repositories.GameRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.broker.AbstractBrokerMessageHandler;
import org.springframework.stereotype.Controller;

@Controller
public class GameStateController {
    private final GameRoomRepository gameRoomRepository;
    private final SimpMessageSendingOperations messagingTemplate;
    private final AbstractBrokerMessageHandler simpleBrokerMessageHandler;

    @Autowired
    public GameStateController(@Qualifier("GameRoomRepository") GameRoomRepository gameRoomRepository, SimpMessageSendingOperations messagingTemplate, @Qualifier("simpleBrokerMessageHandler") AbstractBrokerMessageHandler simpleBrokerMessageHandler) {
        this.gameRoomRepository = gameRoomRepository;
        this.messagingTemplate = messagingTemplate;
        this.simpleBrokerMessageHandler = simpleBrokerMessageHandler;
    }

    @MessageMapping("/game/spawn")
    public void SpawnMinion(CreateSpawnMinionMessage createSpawnMinionMessage, SimpMessageHeaderAccessor headerAccessor) {
        String minionType = createSpawnMinionMessage.getMinionType();
        int row = createSpawnMinionMessage.getRow();
        int col = createSpawnMinionMessage.getCol();
        String roomId = createSpawnMinionMessage.getRoomId();
        String owner = headerAccessor.getNativeHeader("username").get(0);
        gameRoomRepository.spawnMinion(roomId, owner, minionType, row, col);
        System.out.println(roomId);
        System.out.println(owner);
        System.out.println(minionType);
        System.out.println(row);
        System.out.println(col);
        GameData gameData = gameRoomRepository.getGameData(roomId);
//        gameRoomRepository.getRoom(roomId).getGame();
//        messagingTemplate.convertAndSend("/topic/game-"+roomId+"/spawn", minionType);
    }
}
