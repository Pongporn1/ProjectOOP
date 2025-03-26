import { RootState } from "../store";
import {Game} from "@/types/game"
import { GameData } from "@/types/gameData";
import { LeaderData } from "@/types/leaderData";
import {Minion} from "@/types/minion"
import { MinionHex } from "@/types/minionHex";
import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import exp from "constants";
import { axisBottom, range } from "d3";
import { a } from "framer-motion/client";

const initialState: { game: Game, stream: GameData[]} = {
  game: {
    turn: 0,
    minionAmount: 0,
    hexAmount: 0,
    budget: 0,
    settings: {},
    grid: range(8).map((i) => range(8).map((j) => "None")),
    minionGrid: range(8).map(() => range(8).map(() => {return {owner: "None", minionType: "None"}})),
    buyableHexes: []
    ,state: "other"
  },stream: [] 
};

const gameSlice = createSlice({
  name: "game",
  initialState,
  reducers: {
    setGame(state, action: PayloadAction<Game>) {
      state.game = action.payload;
    },
    nextTurn(state){
      state.game.turn += 1
    },
    receiveBudget(state, action: PayloadAction<number>){
      state.game.budget += action.payload
    },
    setBudget(state, action: PayloadAction<number>){
      state.game.budget = action.payload
    },
    setSettings(state, action: PayloadAction<Record<string, number>>){
      state.game.settings = action.payload
    },
    setGridCellOwner(state, action: PayloadAction<{row:number, col:number, owner: string}>){
      state.game.grid[action.payload.row][action.payload.col] = action.payload.owner
    },
    setMinionGrid(state, action: PayloadAction<MinionHex[][]>){
      state.game.minionGrid = action.payload
    },
    setOwnerGrid(state, action: PayloadAction<string[][]>){
      state.game.grid = action.payload
    },
    setMinionAmount(state, action: PayloadAction<number>){
      state.game.minionAmount = action.payload
    },
    setHexAmount(state, action: PayloadAction<number>){
      state.game.hexAmount = action.payload
    },setBuyableHex(state, action: PayloadAction<{row: number, col: number}[]>){
      state.game.buyableHexes = action.payload
    },
    setGameState(state, action: PayloadAction<string>){
      state.game.state = action.payload
    },
    setLeaderData(state, action: PayloadAction<LeaderData>){
      state.game.budget = action.payload.budget
      state.game.state = action.payload.state
      state.game.buyableHexes = action.payload.buyableHexes
      state.game.hexAmount = action.payload.hexAmount
      state.game.minionAmount = action.payload.minionAmount
    },
    setTurn(state, action: PayloadAction<number>){
      state.game.turn = action.payload
    },
    addGameStream(state, action: PayloadAction<GameData>){
      state.stream = [...state.stream, action.payload]
    },
    setGameStream(state, action:PayloadAction<GameData[]>){
      state.stream = action.payload
    },
    popGameStream(state){
      state.stream = state.stream.slice(1)
    }
  },
});

export const {setGameStream, addGameStream, popGameStream, setTurn,setLeaderData, setGameState, setOwnerGrid, setBuyableHex, setBudget, setHexAmount, setMinionAmount, setGridCellOwner, setGame, setMinionGrid, nextTurn, receiveBudget, setSettings} = gameSlice.actions;
export const selectGame = (state: RootState) => state.game.game;  
export const selectStream = (state: RootState) => state.game.stream;
export default gameSlice.reducer;
