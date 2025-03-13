package com.example.main.controllers;

import AST.Strategy;
import DataStructure.Pair;
import Parser.StrategyParser;
import com.example.main.dtos.*;
import com.example.main.models.*;
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
import org.springframework.stereotype.Indexed;

import java.security.Principal;
import java.util.*;

@Controller
public class CreateGameController {
    private final SimpMessageSendingOperations messagingTemplate;
    private final GameRoomRepository gameRoomRepository;

    private static final String presetScript = """
            oppoLoc = opponent
            allyLoc = ally
            if (oppoLoc) then
            {
                if (oppoLoc / 10 - 1) then {
                    if(oppoLoc % 10 - 5) then move upleft
                    else if(oppoLoc % 10 - 4) then move downleft
                    else if(oppoLoc % 10 - 3) then move down
                    else if(oppoLoc % 10 - 2) then move downright
                    else if(oppoLoc % 10 - 1) then move upright
                    else move up
                } else {
                    if(oppoLoc % 10 - 5) then shoot upleft 10
                    else if(oppoLoc % 10 - 4) then shoot downleft 10
                    else if(oppoLoc % 10 - 3) then shoot down 10
                    else if(oppoLoc % 10 - 2) then shoot downright 10
                    else if(oppoLoc % 10 - 1) then shoot upright 10
                    else shoot up 10
                }
            }
            else {
                ranPos = 1 + (random % 6)
                if(ranPos - 5) then {
                    move up
                }else if (ranPos - 4) then {
                    move down
                }else if (ranPos - 3) then {
                    move downleft
                }else if (ranPos - 2) then {
                    move downright
                }else if (ranPos - 1) then {
                    move upleft
                }else {
                    move upright
                }
            }
            """;

    @Autowired
    public CreateGameController(SimpMessageSendingOperations messagingTemplate, @Qualifier("GameRoomRepository") GameRoomRepository gameRoomRepository) {
        this.messagingTemplate = messagingTemplate;
        this.gameRoomRepository = gameRoomRepository;
    }

    @MessageMapping("/game/create")
    @SendToUser("/topic/games-create")
    public RoomItem createRoom(CreateGameRoomMessage createGameRoomMessage, SimpMessageHeaderAccessor headerAccessor){
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
        String username = headerAccessor.getNativeHeader("username").get(0);
        RoomItem room = gameRoomRepository.createRoom(id, createGameRoomMessage.getGameMode());
        room.joinRoom(username);
        messagingTemplate.convertAndSend("/topic/games-create-"+username, room);
        return room;
    }

    @MessageMapping("/game/join")
    @SendToUser("/topic/games-join")
    public RoomItem joinRoom(CreateJoinRoomMessageBody joinRoomMessageBody, SimpMessageHeaderAccessor headerAccessor){
        System.out.println("joinRoom");
        RoomItem room = gameRoomRepository.getRoom(joinRoomMessageBody.getRoomId());
        boolean joinable = true;
        String username = headerAccessor.getNativeHeader("username").get(0);
        if(room == null) room = RoomItem.buildRoom("ER-NONE", "");
        else joinable = gameRoomRepository.joinRoom(joinRoomMessageBody.getRoomId(), username);
        if(!joinable) room = RoomItem.buildRoom("ER-FULL", "");
        messagingTemplate.convertAndSend("/topic/games-join-"+username, room);
        messagingTemplate.convertAndSend("/topic/room-"+joinRoomMessageBody.getRoomId(), room, new HashMap<>(){{put("command", "update");}});
        return room;
    }

    @MessageMapping("/room/editConfig")
    @SendTo("/topic/gamesConfig")
    public Map<String, Long> editConfig(CreateEditConfigMessageBody editConfigMessageBody, SimpMessageHeaderAccessor headerAccessor){
        RoomItem room = gameRoomRepository.getRoom(editConfigMessageBody.getRoomId());
        System.out.println(editConfigMessageBody.getValue());
        room.setSetting(editConfigMessageBody.getSetting(), editConfigMessageBody.getValue());
        System.out.println(room.getId());
        return room.getConfig();
    }

    @MessageMapping("/room/to-minion-select")
    public void toMinionSelect(CreateJoinRoomMessageBody createJoinRoomMessageBody, SimpMessageHeaderAccessor headerAccessor){
        messagingTemplate.convertAndSend("/topic/room-"+createJoinRoomMessageBody.getRoomId(), createJoinRoomMessageBody, new HashMap<>(){{put("command", "next");}});
    }


    @MessageMapping("/room/minion/get")
    public void getMinions(CreateGetMinionsMessage createGetMinionsMessage, SimpMessageHeaderAccessor headerAccessor){
        System.out.println("getMinions");
        String roomId = createGetMinionsMessage.getRoomId();
        RoomItem room = gameRoomRepository.getRoom(roomId);
        if(room != null) {
            messagingTemplate.convertAndSend("/topic/room-minions-"+roomId, room.getMinions(), new HashMap<>(){{put("command", "get");}});
        }
    }

    @MessageMapping("/room/minion/name")
    public void changeMinionName(CreateUpdateMinionsNameMessage createUpdateMinionsNameMessage, SimpMessageHeaderAccessor headerAccessor){
        String roomId = createUpdateMinionsNameMessage.getRoomId();
        int index = createUpdateMinionsNameMessage.getIndex();
        String name = createUpdateMinionsNameMessage.getMinionName();
        RoomItem room = gameRoomRepository.getRoom(roomId);
        if(room != null) {
            room.changeMinionName(index, name);
            messagingTemplate.convertAndSend("/topic/room-minions-"+roomId, room.getMinions(), new HashMap<>(){{put("command", "update");}});
        }
    }

    @MessageMapping("/room/minion/defense")
    public void changeMinionDefense(CreateUpdateMinionDefenseMessage createUpdateMinionDefenseMessage, SimpMessageHeaderAccessor headerAccessor){
        System.out.println("Change Minion Defense");
        String roomId = createUpdateMinionDefenseMessage.getRoomId();
        int index = createUpdateMinionDefenseMessage.getIndex();
        long defense = createUpdateMinionDefenseMessage.getDefense();
        RoomItem room = gameRoomRepository.getRoom(roomId);
        if(room != null) {
            room.changeMinionDefense(index, defense);
            messagingTemplate.convertAndSend("/topic/room-minions-"+roomId, room.getMinions(), new HashMap<>(){{put("command", "update");}});
        }
    }

    @MessageMapping("/room/minion/image")
    public void changeMinionImage(CreateUpdateMinionImageMessage createUpdateMinionImageMessage, SimpMessageHeaderAccessor headerAccessor){
        System.out.println("Change Minion Image");
        String roomId = createUpdateMinionImageMessage.getRoomId();
        int index = createUpdateMinionImageMessage.getIndex();
        int img = createUpdateMinionImageMessage.getImageId();
        if(img > 2) img = 0;
        if(img < 0) img = 2;
        RoomItem room = gameRoomRepository.getRoom(roomId);
        if(room != null) {
            room.changeMinionImage(index, img);
            messagingTemplate.convertAndSend("/topic/room-minions-"+roomId, room.getMinions(), new HashMap<>(){{put("command", "update");}});
        }
    }

    @MessageMapping("/room/minion/add")
    public void addMinion(CreateJoinRoomMessageBody createJoinRoomMessageBody, SimpMessageHeaderAccessor headerAccessor){
        System.out.println("addMinion");
        String roomId = createJoinRoomMessageBody.getRoomId();
        if (gameRoomRepository.addMinion(roomId)){
            RoomItem room = gameRoomRepository.getRoom(roomId);
            messagingTemplate.convertAndSend("/topic/room-minions-"+roomId, room.getMinions(), new HashMap<>(){{put("command", "update");}});
        }
    }

    @MessageMapping("/room/minion/remove")
    public void removeMinion(CreateRemoveMinionMessage createRemoveMinionMessage, SimpMessageHeaderAccessor headerAccessor){
        System.out.println("removeMinion");
        String roomId = createRemoveMinionMessage.getRoomId();
        int index = createRemoveMinionMessage.getIndex();
        if (gameRoomRepository.removeMinion(roomId, index)){
            RoomItem room = gameRoomRepository.getRoom(roomId);
            messagingTemplate.convertAndSend("/topic/room-minions-"+roomId, new RemoveMinion(room.getMinions(), index), new HashMap<>(){{put("command", "remove");}});
        }
    }

    @MessageMapping("/room/minion/script")
    public void changeMinionScript(CreateUpdateMinionScriptMessage createUpdateMinionScriptMessage, SimpMessageHeaderAccessor headerAccessor){
        System.out.println("Change Minion Script");
        String roomId = createUpdateMinionScriptMessage.getRoomId();
        int index = createUpdateMinionScriptMessage.getIndex();
        String script = createUpdateMinionScriptMessage.getScript();
        RoomItem room = gameRoomRepository.getRoom(roomId);
        if(room != null) {
            room.changeMinionScript(index, script);
            messagingTemplate.convertAndSend("/topic/room-minions-"+roomId, room.getMinions(), new HashMap<>(){{put("command", "update");}});
        }
    }

    @MessageMapping("/room/minion/preset")
    public void loadPresetScript(CreateLoadPresetMessage createLoadPresetMessage, SimpMessageHeaderAccessor headerAccessor){
        System.out.println("loadPreset");
        String roomId = createLoadPresetMessage.getRoomId();
        int index = createLoadPresetMessage.getIndex();
        RoomItem room = gameRoomRepository.getRoom(roomId);
        if(room != null) {
            room.changeMinionScript(index, presetScript);
            messagingTemplate.convertAndSend("/topic/room-minions-"+roomId, room.getMinions(), new HashMap<>(){{put("command", "update");}});
        }
    }

    @MessageMapping("/room/minion/edit")
    public void setEditing(CreateRoomEditMessage createRoomEditMessage, SimpMessageHeaderAccessor headerAccessor){
        String roomId = createRoomEditMessage.getRoomId();
        RoomItem room = gameRoomRepository.getRoom(roomId);
        System.out.println("Editing");
        if(room != null) {
            String username = headerAccessor.getNativeHeader("username").get(0);
            String field = createRoomEditMessage.getField();
            Integer mIndex = createRoomEditMessage.getIndex();
            System.out.println("mIndex: "+mIndex);
            List<RoomEditing>  editing = room.setEditing(username, mIndex , field);
            messagingTemplate.convertAndSend("/topic/room-minions-"+roomId, editing, new HashMap<>(){{put("command", "edit");}});
        }
    }

    @MessageMapping("/room/confirmConfig")
    public void confirmConfig(CreateConfirmConfigMessage createConfirmConfigMessage, SimpMessageHeaderAccessor headerAccessor){
        System.out.println("confirmConfig" + createConfirmConfigMessage.getUsername());
        String roomId = createConfirmConfigMessage.getRoomId();
        String user = createConfirmConfigMessage.getUsername();
        boolean conf = createConfirmConfigMessage.isConfirmed();
        System.out.println(roomId);
        System.out.println(user);
        System.out.println(conf);
        boolean confirmed = gameRoomRepository.confirmInRoom(roomId, user, conf);
        RoomItem room = gameRoomRepository.getRoom(roomId);
        if(room.isConfirm()){
            room.resetConfirm();
            messagingTemplate.convertAndSend("/topic/room-" + roomId, room, new HashMap<>(){{put("command", "next");}});
        }else
            messagingTemplate.convertAndSend("/topic/room-"+roomId, room, new HashMap<>(){{put("command", "update");}});
    }


    @MessageMapping("/room/minion/confirm")
    public void confirmMinion(CreateConfirmConfigMessage createConfirmConfigMessage, SimpMessageHeaderAccessor headerAccessor){
        System.out.println("confirm Minion" + createConfirmConfigMessage.getUsername());
        String roomId = createConfirmConfigMessage.getRoomId();
        String user = createConfirmConfigMessage.getUsername();
        boolean conf = createConfirmConfigMessage.isConfirmed();
        System.out.println(roomId);
        System.out.println(user);
        System.out.println(conf);
        boolean confirmed = gameRoomRepository.confirmInRoom(roomId, user, conf);
        RoomItem room = gameRoomRepository.getRoom(roomId);
        System.out.println("IsConfirm: " + room.isConfirm());
        if(room.isConfirm()){
            room.resetConfirm();
            List<MinionItem> minionItems = room.getMinions();
            String inValid = "";
            String field = "";
            HashSet<String> minionName = new HashSet<>();
            Map<String, Strategy> minions = new HashMap<>();
            Map<String, Pair<Strategy, Long>> minionKinds = new HashMap<>();
            for(int i = 0; i < minionItems.size(); i++){
                String name = minionItems.get(i).getName();
                Long defense = minionItems.get(i).getDefense();
                String script = minionItems.get(i).getScript();
                if(name.isEmpty())
                {
                    inValid = "Minion's name can not be empty";
                    field = "name";
                }
                if(minionName.contains(name)){
                    inValid = "Minion's name duplicate";
                    field = "name";
                }

                Strategy s = null;
                if(inValid.isEmpty()) {
                    try {
                        StrategyParser sp = new StrategyParser(minionItems.get(i).getScript());
                        s = sp.parse();
                        minions.put(name, s);
                    } catch (Exception e) {
                        inValid = "Script is Illegal";
                        field = "script";
                    }
                }

                if(inValid.isEmpty()){
                    minionName.add(name);
                    minionKinds.put(name, Pair.of(s, defense));
                }else{
                    messagingTemplate.convertAndSend("/topic/room-minions-"+roomId, new MinionErrMessage(i, field,inValid, room) ,new HashMap<>(){{put("command", "err");}});
                    return;
                }

                System.out.println("-----------------------------------------------");
            }
            room.createGame(minionKinds);
            messagingTemplate.convertAndSend("/topic/room-minions-" + roomId, room.getMinions(), new HashMap<>(){{put("command", "next");}});
        }else
            messagingTemplate.convertAndSend("/topic/room-minions-"+roomId, room, new HashMap<>(){{put("command", "confirm");}});
    }

}
