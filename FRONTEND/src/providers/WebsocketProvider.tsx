"use client";

import { useEffect } from "react";
import { useWebSocket } from "@/hooks/useWebsocket";

export const WebSocketProvider = ({ children }: { children: React.ReactNode }) => {
  const { connect } = useWebSocket();
  useEffect(() => {
    connect();
  }, []);

  return <>{children}</>;
};

export default WebSocketProvider;
