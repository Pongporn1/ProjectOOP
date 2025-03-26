import { ChatMessage } from "./chat_message";
import { EditingRoom } from "./editingRoom";

export interface Room {
  id: string
  config: Record<string, number>
  leader1: string
  leader2: string
  leader1Confirm: boolean
  leader2Confirm: boolean
  users: string[]
  otherEdit: EditingRoom
  /*   messages: ChatMessage[];

  users: number */
}
