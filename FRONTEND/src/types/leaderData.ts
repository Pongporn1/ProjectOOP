import { MinionHex } from "./minionHex"

export interface LeaderData{
    state: string
    name: string
    budget: number
    minionAmount: number
    hexAmount: number
    buyableHexes: {row:number, col:number}[]
}