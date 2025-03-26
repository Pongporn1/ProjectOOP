import { RootState } from "../store";
import { Room } from "@/types/room";
import { ChatMessage } from "@/types/chat_message";
import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { RoomMinions } from "@/types/roomMinions";
import { Minion } from "@/types/minion";
import { act } from "react";
import { EditingRoom } from "@/types/editingRoom";

const initialState: { room: Room, minions: RoomMinions } = {
  room: {
    id: "",
    config: {},
    users: [],
    leader1: "",
    leader2: "",
    leader1Confirm: false,
    leader2Confirm: false,    
    otherEdit: {userName:"", mindex:-1, field:""},
  },
  minions: {
    minions: []
  }
};

const roomSlice = createSlice({
  name: "room",
  initialState,
  reducers: {
    setRoom(state, action: PayloadAction<Room>) {
      state.room = action.payload;
    },
    changeConfig(state, action: PayloadAction<{setting:string, value:number}>){
      state.room.config[action.payload.setting] = action.payload.value
    },
    setConfig(state, action: PayloadAction<Record<string, number>>){
      state.room.config = action.payload
    },
/*     setLeader(state, action: PayloadAction<{index: 1 | 2, user: string}>){
      if(action.payload.index === 1){
        state.room.leader1 = action.payload.user
      }else{
        state.room.leader2 = action.payload.user
      }
    }, */
    setMinions(state, action: PayloadAction<Minion[]>){
      state.minions.minions = action.payload
    }
    ,setOtherEditing(state, action: PayloadAction<EditingRoom>){
      console.log(action.payload)
      state.room.otherEdit = action.payload
    }
/*     addMessageToRoom(state, action: PayloadAction<ChatMessage>) {
      state.room.messages.push(action.payload);
    },
    setUserAmount(state, action:PayloadAction<number>){
      state.room.users = action.payload
    } */
  },
});

export const { setRoom, changeConfig, setConfig, setMinions, setOtherEditing} = roomSlice.actions;
export const selectRoom = (state: RootState) => state.room.room;
export const selectRoomMinion = (state: RootState) => state.room.minions
export default roomSlice.reducer;
