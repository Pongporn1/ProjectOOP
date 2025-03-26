import { Minion } from "./minion";
import { MinionHex } from "./minionHex";

export interface Game {
    turn: number
    minionAmount: number
    hexAmount:number
    budget: number
    settings: Record<string, number>
    grid: string[][]
    minionGrid : MinionHex[][]
    buyableHexes : {row: number, col: number}[]
    state: string
}
