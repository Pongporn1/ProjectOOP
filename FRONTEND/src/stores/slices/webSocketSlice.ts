import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { RootState } from "../store";
import  Stomp from 'stompjs'
import { Client, CompatClient } from "@stomp/stompjs";
import { access } from "fs";

interface WebSocketState {
  client: Client | null;
  isConnected: boolean;
  sessionId: string
}

const initialState: WebSocketState = {
  client: null,
  isConnected: false,
  sessionId: ""
};

const websocketSlice = createSlice({
  name: "websocket",
  initialState,
  reducers: {
    setWebSocketClient: (state, action: PayloadAction<Client | null>) => {
      state.client = action.payload;
    },
    setConnectionStatus: (state, action: PayloadAction<boolean>) => {
      state.isConnected = action.payload;
    },
    setSessionId: (state, action: PayloadAction<string>) => {
      state.sessionId = action.payload;
    },
  },
});

export const { setWebSocketClient, setConnectionStatus } =
  websocketSlice.actions;
export const selectWebsocket = (state: RootState) => state.websocket;
export default websocketSlice.reducer;
