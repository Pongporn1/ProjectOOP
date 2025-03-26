import { useDispatch } from "react-redux";
import SockJS from "sockjs-client";
import {
  Stomp,
  Client,
  Frame,
  CompatClient,
  Message,
  StompSubscription,
} from "@stomp/stompjs";
import {
  selectWebsocket,
  setConnectionStatus,
  setWebSocketClient,
} from "@/stores/slices/webSocketSlice";
import { useAppSelector } from "@/stores/hook";
import { selectUser } from "@/stores/slices/userSlice";

export const useWebSocket = () => {
  const dispatch = useDispatch();
  const { client, isConnected, sessionId } = useAppSelector(selectWebsocket);
  const user = useAppSelector(selectUser);
  const serverUrl = process.env.API_BASE_URL;

  const subscribe = (
    destination: string,
    callback: (payload: Message) => any
  ) => {
    if (client && isConnected) {
      const subscription = client.subscribe(destination, callback, {ack:'client'});
      console.log(`Subscribed to ${destination}`);
      return subscription;
    } else {
      console.log("No active WebSocket connection to disconnect.");
    }
  };

  const unsubscribe = (subscription: StompSubscription | undefined) => {
    if (client && isConnected && subscription) {
      client.unsubscribe(subscription.id);
      console.log(`Unsubscribed from ${subscription.id}`);
    } else {
      console.log("No active WebSocket connection to disconnect.");
    }
  };

  const sendMessage = (destination: string, message: any) => {
    if (client && isConnected) {
      console.log("send", JSON.stringify(message));
      client.publish({
        destination: `/app${destination}`,
        headers: { username: `${user?.username}` },
        body: JSON.stringify(message),
      });
    } else {
      console.log("No active WebSocket connection to disconect.");
    }
  };

  const connect = () => {
    try {
      const stompClient = new Client({
        webSocketFactory: () => new SockJS(`${serverUrl}/ws`),
        //brokerURL: `ws://10.171.10.63:8080/ws`,//
        onConnect: () => onConnected(stompClient),
        onDisconnect: () => disconnect(),
        disconnectHeaders: {"discon":"discon"},
        reconnectDelay: 2000,
      });

      console.log("connected, session id: " + sessionId);
      stompClient.activate();
    } catch (e) {
      console.log(e);
    }
  };
  const disconnect = () => {
    if (client && isConnected) {
      dispatch(setWebSocketClient(null));
      dispatch(setConnectionStatus(false));
      console.log("WebSocket disconnected");
    } else {
      console.log("No active WebSocket connection to disconnect.");
    }
  };

  const onConnected = (stompClient: Client) => {
    dispatch(setWebSocketClient(stompClient));
    dispatch(setConnectionStatus(true));
    console.log("WebSocket connected successfully");
  };

  const onUpdateRoom = (payload: Message) => {
    const newMessageObject = JSON.parse(payload.body);
    //dispatch(addMessageToRoom(newMessageObject));
    console.log("Receive new message object", newMessageObject);
  };

  const onReciveMessage = (payload: Message) => {
    const newMessageObject = JSON.parse(payload.body);
    console.log("Receive new user amount object", newMessageObject);
  };

  const onUpdateUser = (payload: Message) => {
    const newMessageObject = JSON.parse(payload.body);
    //dispatch(setUserAmount(newMessageObject));
    console.log("Receive new user amount object", newMessageObject);
  };

  return {
    connect,
    disconnect,
    sendMessage,
    subscribe,
    unsubscribe,
  };
};
