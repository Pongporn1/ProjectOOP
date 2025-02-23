package com.example.main.controllers;

import com.example.main.dtos.CreateEditConfigMessageBody;
import com.example.main.dtos.CreateJoinRoomMessageBody;
import com.example.main.models.Room;
import com.example.main.models.RoomItem;
import com.example.main.repositories.GameRoomRepository;
import com.example.main.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

@Controller
public class CreateGameController {
    private final SimpMessageSendingOperations messagingTemplate;
    private final GameRoomRepository gameRoomRepository;
    @Autowired
    public CreateGameController(SimpMessageSendingOperations messagingTemplate, @Qualifier("GameRoomRepository") GameRoomRepository gameRoomRepository) {
        this.messagingTemplate = messagingTemplate;
        this.gameRoomRepository = gameRoomRepository;
    }

    @MessageMapping("/game/create")
    @SendToUser("/topic/games-create")
    public RoomItem createRoom(SimpMessageHeaderAccessor headerAccessor){
        Random rnd = new Random();
        String id = "";
        id += (char) ('A' + rnd.nextInt(26));
        id += (char) ('A' + rnd.nextInt(26));
        id += rnd.nextInt(10);
        id += rnd.nextInt(10);
        id += rnd.nextInt(10);
        id += rnd.nextInt(10);
        id += rnd.nextInt(10);

        System.out.println("createRoom");
        RoomItem room = gameRoomRepository.createRoom(id);
        messagingTemplate.convertAndSend("/topic/games", room);
        return room;
    }

    @MessageMapping("/game/join")
    @SendToUser("/topic/games-join")
    public RoomItem joinRoom(CreateJoinRoomMessageBody joinRoomMessageBody, SimpMessageHeaderAccessor headerAccessor){
        System.out.println("joinRoom");
        RoomItem room = gameRoomRepository.getRoom(joinRoomMessageBody.getRoomId());
        return room;
    }

    @MessageMapping("/room/editConfig")
    @SendTo("/topic/gamesConfig")
    public Map<String, Long> editConfig(CreateEditConfigMessageBody editConfigMessageBody, SimpMessageHeaderAccessor headerAccessor){
        RoomItem room = gameRoomRepository.getRoom(editConfigMessageBody.getRoomId());
        room.setSetting(editConfigMessageBody.getSetting(), editConfigMessageBody.getValue());
        System.out.println(room.getId());
        return room.getConfig();
    }

}
