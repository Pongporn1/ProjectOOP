import { LeaderData } from "./leaderData"
import { MinionHex } from "./minionHex"

export interface GameData{
    turn: number
    owner: string[][]
    minionHexes: MinionHex[][]
    leader: Record<string, LeaderData>
}