export interface ChatMessage {
  type: "JOIN" | "CHAT" | "LEAVE";
  id: number;
  message: string;
  sender: string;
  timestamp: Date;
}
