  "use client";
  import { useEffect, useState } from "react";
  import { DndContext } from "@dnd-kit/core";
  import { Draggable } from "@/app/components/DND/Draggable";
  import { DroppableHex } from "@/app/components/DND/DroppableHex";
  import { range } from "@/app/utils/Range";
  import { Minion1, Minion2, Minion3 } from "@/app/components/minion";
  import { useAppSelector } from "@/stores/hook";
  import {
    selectGame,
    setOwnerGrid,
    setMinionGrid,
    setLeaderData,
    setTurn,
    setGameStream,
    selectStream,
    popGameStream,
    addGameStream,
  } from "@/stores/slices/gameSlice";
  import { Button_v5, Button_v6 } from "@/app/components/EButton";
  import { motion } from "framer-motion";
  import NumberFlow, { continuous } from "@number-flow/react";
  import { useDispatch } from "react-redux";
  import { TextFade } from "@/app/components/FadeUp";
  import { Separator } from "@/app/components/Seperator";
  import {
    selectRoom,
    selectRoomMinion,
  } from "@/stores/slices/roomSlice";
  import { useWebSocket } from "@/hooks/useWebsocket";
  import { selectUser } from "@/stores/slices/userSlice";
  import { Message, StompSubscription } from "@stomp/stompjs";
  import { useKeyboard } from "@/hooks/useKeyboard";

  export default function Home() {
    const cols = 8;
    const rows = 8;

    const [speed, setSpeed] = useState<number>(1);

    const game = useAppSelector(selectGame);
    const dispatch = useDispatch();
    const minions = useAppSelector(selectRoomMinion);
    const { sendMessage, subscribe } = useWebSocket();
    const room = useAppSelector(selectRoom);
    const user = useAppSelector(selectUser);
    const stream = useAppSelector(selectStream);

    const [gameSub, setGameSub] = useState<StompSubscription>();
    const [userSub, setUserSub] = useState<StompSubscription>();

    const minionTypeToImageId = (type: string) => {
      const m = minions.minions.find((m) => m.name === type);
      if (m === undefined) return -1;
      console.log(type, m.imageId);
      return m.imageId;
    };

    const minionsImage = [
      (col: string, scale: number) => <Minion1 col={col} scale={scale} />,
      (col: string, scale: number) => <Minion2 col={col} scale={scale} />,
      (col: string, scale: number) => <Minion3 col={col} scale={scale} />,
    ];

    const getMinionImage = (index: number) => {
      if (index >= 0 && index < minionsImage.length) {
        return minionsImage[index];
      }
      return (col: string, scale: number) => <></>;
    };

    const getAdjacentPos = ({
      row,
      col,
      dir,
    }: {
      row: number;
      col: number;
      dir: "up" | "down" | "upleft" | "upright" | "downleft" | "downright";
    }) => {
      switch (dir) {
        case "up":
          return { row: row - 1, col: col };
        case "down":
          return { row: row + 1, col: col };
        case "upleft":
          return { row: row + ((col + 1) % 2) - 1, col: col - 1 };
        case "upright":
          return { row: row + ((col + 1) % 2) - 1, col: col + 1 };
        case "downleft":
          return { row: row + ((col + 1) % 2), col: col - 1 };
        case "downright":
          return { row: row + ((col + 1) % 2), col: col + 1 };
        default:
          return { row: -1, col: -1 };
      }
    };

    const isMine = (row :number, col: number) => {
      return game.grid[row][col] === user?.username
    }

    const owner = ({ row, col }: { row: number; col: number }) => {
      if (row < 0 || row > 7 || col < 0 || col > 7) return 0;
      if (game.grid[row][col] === room.leader1) return 1;
      if (game.grid[row][col] === room.leader2) return 2;
      return 0;
    };

    const stateButton = () => {
      if (game.state === "spawning") {
        return "End Turn";
      } else if (game.state === "buying") {
        return "Skip";
      } else if (game.state === "first_spawning") {
        return "Spawn First Minion";
      } else {
        return "";
      }
    };

    const [onInfo, showInfo] = useState<number>(-1);
    const availableMinion = [Minion1, Minion2, Minion3];

    const generateBomb = () => {
      let grid = [];
      for (let i = 0; i < rows; i++) {
        let hex: boolean[] = [];
        for (let j = 0; j < cols; j++) {
          hex.push(Math.random() >= 0.85);
        }
        grid.push(hex);
      }
      return grid;
    };
    const [bomb, setBomb] = useState<boolean[][]>(generateBomb());

    /*   useEffect(() => {
      dispatch(setGridCellOwner({ row: 7, col: 7, owner: 1 }));
      dispatch(setGridCellOwner({ row: 7, col: 6, owner: 1 }));
      dispatch(setGridCellOwner({ row: 7, col: 5, owner: 1 }));
      dispatch(setGridCellOwner({ row: 6, col: 7, owner: 1 }));
      dispatch(setGridCellOwner({ row: 6, col: 6, owner: 1 }));
    }, []); */

    const patchUpdate = (data: any) => {
      //dispatch(setMinions(data["minions"]))
      dispatch(setOwnerGrid(data["owner"]));
      dispatch(setMinionGrid(data["minionHexes"]));
      dispatch(setTurn(data["turn"]));
      if (user?.username !== undefined) {
        const leaderData = data["leaders"][user?.username];
        //console.log(leaderData)
        dispatch(setLeaderData(leaderData));
      }
    };

    const onReciveMessage = (payload: Message) => {
      const payloadCommand = payload.headers["command"];
      if (payloadCommand === "update") {
        const data = JSON.parse(payload.body);
        console.log(data);
        //patchUpdate(data)
      }
      if (payloadCommand === "test") {
        const data = JSON.parse(payload.body);
        patchUpdate(data[0]);
        if (data.length > 1) {
          dispatch(setGameStream(data.slice(1)));
        }
        console.log(data);
      }
      if(payloadCommand === "add"){
        const data = JSON.parse(payload.body);
        //dispatch(addGameStream(data))
        //console.log(data)
        patchUpdate(data[0])
      }
    };

    useEffect(() => {
      console.log(stream.length)
      if (stream.length > 0) {
        const data = setTimeout(() => {
          patchUpdate(stream[0]);
          dispatch(popGameStream());
        }, 500 / speed);
        return () => clearTimeout(data);
      }
    }, [stream]);

    const onReciveUserMessage = (payload: Message) => {
      const payloadCommand = payload.headers["command"];
      if (payloadCommand === "init") {
        const data = JSON.parse(payload.body);
        console.log(data);
        patchUpdate(data);
      }
      if (payloadCommand === "minionls") {
        const data = JSON.parse(payload.body);
        //dispatch(setMinions(data["minions"]))
      }
    };

    useEffect(() => {
      const data = setTimeout(() => {
        const roomSub = subscribe(`/topic/game-${room.id}`, onReciveMessage);
        const userSub = subscribe(
          `/topic/user-${user?.username}`,
          onReciveUserMessage
        );
        setGameSub(roomSub);
        setUserSub(userSub);
        sendMessage(`/game/start`, { roomId: room.id });
      }, 0);
      return () => clearTimeout(data);
    }, []);

    const generateHex = () => {
      let grid = [];
      for (let i = 0; i < rows; i++) {
        let hex: String[] = [];
        for (let j = 0; j < cols; j++) {
          hex.push("?");
        }
        grid.push(hex);
      }
      return grid;
    };

    const [hex, setHex] = useState<String[][]>(generateHex);
    const [changed, setChange] = useState(false);

    const clickCell = (row: number, col: number) => () => {
      console.log(row, col);
      const newHex = hex;
      row--;
      col--;
      newHex[row][col] = bomb[row][col]
        ? "X"
        : (() => {
            let count = 0;
            if (row > 0 && bomb[row - 1][col]) count++;
            if (row < rows - 1 && bomb[row + 1][col]) count++;
            if (col % 2 == 1) {
              if (row > 0 && col > 0 && bomb[row - 1][col - 1]) count++;
              if (col > 0 && bomb[row][col - 1]) count++;
              if (row > 0 && col < cols - 1 && bomb[row - 1][col + 1]) count++;
              if (col < cols - 1 && bomb[row][col + 1]) count++;
            } else {
              if (col > 0 && bomb[row][col - 1]) count++;
              if (col < cols - 1 && bomb[row][col + 1]) count++;
              if (row < rows - 1) {
                if (col > 0 && bomb[row + 1][col - 1]) count++;
                if (col < cols - 1 && bomb[row + 1][col + 1]) count++;
              }
            }
            return count.toString();
          })();
      setHex(newHex);
      setChange(!changed);
    };

    const getHeight = () => Math.sqrt(3) * (cols + 0.5) * 50;
    const getWidth = () => 1.5 * 50 * rows + 0.5 * 50;

    const sendSpawnMessage = (row:number, col:number, index: number) => {
      sendMessage("/game/spawn", {
        row: row,
        col: col,
        roomId: room.id,
        owner: user?.username,
        minionType: minions.minions[index].name,})
    }

    function handleDragEnd({ active, over }: { active: any; over: any }) {
      {
        active ? console.log(active.data.current["minion"], active.id) : "";
      }
      {
        over
          ? console.log(
              over.data.current["row"],
              over.data.current["col"],
              over.id
            )
          : "";
      }

      if (active && over) {
        const row = over.data.current["row"] - 1;
        const col = over.data.current["col"] - 1;

        if (game.grid[row][col] === user?.username) {
          sendMessage("/game/spawn", {
            row: row,
            col: col,
            roomId: room.id,
            owner: user?.username,
            minionType: minions.minions[active.data.current["minion"]].name,
          });

          /*         const minion = active.data.current["minion"];
          const ls = minionList.map((r, i) =>
            r.map((e, j) => (i == row && j == col ? minion : e))
          );
          if (game.state === "first_spawning") {
            setState("spawning");
          } else {
            setState("buying");
          }
          setMinionList(ls); */
        }
      }
    }

    const nextState = () => {
      skipState();
    };

    const calculateBorderColor = (leader: number, buyable: boolean) => {
      if (buyable) return calculateLeaderColor(user?.username);
      if (leader === 1) return `#305CDE`;
      if (leader === 2) return `#e55451`;
      return "#000a";
    };

    const calculateLeaderColor = (leaderName: string | undefined) => {
      if (leaderName === room.leader1) return `#305CDE`;
      if (leaderName === room.leader2) return `#e55451`;
      return "#fff";
    };

    const isDrawBorder = (
      row: number,
      col: number,
      dir: "up" | "down" | "upleft" | "upright" | "downleft" | "downright"
    ) => {
      return (
        owner({ row: row - 1, col: col - 1 }) !==
        owner(
          getAdjacentPos({
            row: row - 1,
            col: col - 1,
            dir: dir,
          })
        )
      );
    };

    const skipState = () => {
      sendMessage("/game/skip", { roomId: room.id });
    };

    const isBuyableNow = (row: number, col: number) => {
      if (game.state !== "buying") return false;
      const found = game.buyableHexes.find(
        (pos) => pos.col === col - 1 && pos.row === row - 1
      );
      return found !== undefined;
    };

    const buyHexAt = (row: number, col: number) => () => {
      sendMessage("/game/buy", { row: row - 1, col: col - 1, roomId: room.id });
    };

    const changeSpeed = () => {
      if (speed < 4) {
        setSpeed(speed * 2);
      }else{
        setSpeed(1);
      }
    };

    const speedButtonHoverText = () => {
      if (speed < 4) {
        return `x${speed * 2}`
      }else{
        return `x1`
      }
    }
    
    const [keyCommand, setKeyCommand] = useState<string>("")
    const [commandErr, setCommandErr] = useState<string>("")
    const k = useKeyboard()


  useEffect(() => {
    console.log(k)
    if(k !== ""){
      setCommandErr("")
      if(keyCommand.length > 0 && k === "Enter"){
        if(keyCommand === "skip" || keyCommand === "ss"){
        skipState() 
        }else if(keyCommand === "x"){
          showInfo(-1)
        }

        let match = keyCommand.match(/s[1-5][1-8][1-8]/)
        if(match && match[0] === keyCommand){
          const index = Number(keyCommand[1])
          const row = Number(keyCommand[2])
          const col = Number(keyCommand[3])
          if(index > minions.minions.length){
            setCommandErr("Minion Index Out of Range")
            setKeyCommand("")
            return
          }
          sendSpawnMessage(row - 1, col - 1, index - 1)
          setKeyCommand("")
        }
        match = keyCommand.match(/b[1-8][1-8]/)
        if(match && match[0] === keyCommand){
          const row = Number(keyCommand[1])
          const col = Number(keyCommand[2])
          buyHexAt(row, col)()
          setKeyCommand("")
          return
        }
        match = keyCommand.match(/i[1-5]/)
        if(match && match[0] === keyCommand){
          const index = Number(keyCommand[1])
          showInfo(index - 1)
          setKeyCommand("")
          return
        }

        setKeyCommand("")
      }  
      else if(k?.length === 1 && (k?.match(/[a-z]/i) || k.match(/[0-9]/)) || k === "+" || k === " " || k === "_"){
        setKeyCommand(keyCommand + k)
      }else if(k === "Backspace"){
          setKeyCommand(keyCommand.slice(0, -1))
      }
  }}, [k])

    const [isFocus, setFocus] = useState<boolean>(false);
    
    const [isSpawningCommand, setSpawningCommand] = useState<boolean>(false)
    const [spawningCommandPos, setSpawningCommandPos] = useState<{row:number, col:number}>({row:-1, col:-1})
    const [isBuyingCommand, setBuyingCommand] = useState<boolean>(false)

    

    useEffect(() => {
      setSpawningCommand(false)
      setBuyingCommand(false)
      console.log("command: " + keyCommand)
      if(keyCommand.length > 1){
        console.log("Command Slice: ", keyCommand.slice(0, 2))
        if(keyCommand.slice(0, 2).match(/s[1-5]/)){
          setSpawningCommand(true)
          let row = -1
          let col = -1
          if(keyCommand.length > 2 && keyCommand[2].match(/[0-9]/)){
            row = Number(keyCommand[2])
          }
          if(keyCommand.length > 3 && keyCommand[3].match(/[0-9]/)){ 
            col = Number(keyCommand[3])
          }
          setSpawningCommandPos({row: row, col: col})
          console.log("isSpawingCommand")
        }
      }
      if(keyCommand[0] === "b"){
        setBuyingCommand(true)
      }
    }, [keyCommand])



    return (
      <DndContext onDragEnd={handleDragEnd}>
        <div className="absolute pointer-events-none text-center py-2 h-screen w-screen flex flex-col-reverse">
          <div className={`w-full bg-[#0005] h-6 ${commandErr === "" ? `text-white` : `text-red-500`}`}>{commandErr === "" ? keyCommand : commandErr}</div>
        </div>
        <div className="h-screen w-full flex flex-col">
          <div className="static h-screen w-full flex flex-row bg-[#2a2a2a99] items-center my-auto">
            <div className="w-[15%] text-[#bbb] pt-5 bg-[#00000088] h-[100%] flex flex-col">
              <div className="text-center text-2xl">Turn</div>
              <NumberFlow
                suffix={` / ${room.config["max_turns"]}`}
                value={game.turn}
                className="text-center text-[1.3rem]"
              ></NumberFlow>
            </div>
            <div
              style={{ minWidth: getWidth() }}
              className="flex justify-center w-full"
            >
              {/*           <Droppable id="droppable" className = "h-fit">
              {parent === "droppable" ? draggable : "Drop here"}
            </Droppable> */}
              <div
                style={{
                  height: getHeight(),
                  width: getWidth(),
                  translate: -0.75 * 50,
                }}
              >
                {range(1, cols).map((col) =>
                  range(1, rows).map((row) => (
                    <DroppableHex
                    isSpawningCommand = {isSpawningCommand && isMine(row - 1, col - 1)}
                    spawningPos = {spawningCommandPos}
                    isBuyingCommand = {isBuyingCommand && isBuyableNow(row, col)}
                      price={room.config["hex_purchase_cost"]}
                      border_color={calculateBorderColor(
                        owner({ row: row - 1, col: col - 1 }),
                        isBuyableNow(row, col)
                      )}
                      border_up={
                        isDrawBorder(row, col, "up") || isBuyableNow(row, col)
                      }
                      border_down={
                        isDrawBorder(row, col, "down") || isBuyableNow(row, col)
                      }
                      border_downleft={
                        isDrawBorder(row, col, "downleft") ||
                        isBuyableNow(row, col)
                      }
                      border_downright={
                        isDrawBorder(row, col, "downright") ||
                        isBuyableNow(row, col)
                      }
                      border_upleft={
                        isDrawBorder(row, col, "upleft") || isBuyableNow(row, col)
                      }
                      border_upright={
                        isDrawBorder(row, col, "upright") ||
                        isBuyableNow(row, col)
                      }
                      x={(col - 1) * 100}
                      y={(row - 1) * 100 - (col % 2 ? 0 : 50)}
                      row={row}
                      col={col}
                      key={`h${row}${col}`}
                      //clickFunc={clickCell(row, col)}
                      bomb={hex[row - 1][col - 1]}
                      id={`drop_hex${col},${row}`}
                      //className="w-[100px] h-[87px] absolute"
                      //style={{ transform: `translateX(100px)` }}
                      buyableOverlay={isBuyableNow(row, col)}
                      onBuying={buyHexAt(row, col)}
                      minion={
                        game.minionGrid[row - 1][col - 1].owner === "None" ||
                        game.minionGrid[row - 1][col - 1].minionType === "None"
                          ? ""
                          : getMinionImage(
                              minionTypeToImageId(
                                game.minionGrid[row - 1][col - 1].minionType
                              )
                            )(
                              calculateLeaderColor(
                                game.minionGrid[row - 1][col - 1].owner
                              ),
                              0.8
                            )
                      }
                    />
                  ))
                )}
              </div>
            </div>

            <div className="w-[100%] bg-[#00000033] h-[100%] flex flex-row">
              <motion.div
                key={`Minion Store`}
                className="w-[50%] pt-10 relative bg-[#00000022] px-5 h-[100%] items-center gap-5 flex flex-col"
              >
                <div
                  style={{
                    pointerEvents:
                      game.state === "spawning" || game.state === "first_spawning"
                        ? "all"
                        : "none",
                  }}
                  className="absolute w-full h-full px-5 items-center gap-5 flex flex-col"
                >
                  <TextFade
                    isShow={
                      game.state === "spawning" || game.state === "first_spawning"
                    }
                    className="w-full flex-col flex gap-2"
                    direction="down"
                  >
                    <div className="text-center text-[1.4rem] w-full h-[2rem]">
                      Minion Store
                    </div>
                    <Separator className="w-full" gradient={true} />
                    <div className="text-center w-full text-[1.1rem]">{`Price: ${room.config["spawn_cost"]}`}</div>
                  </TextFade>
                  <TextFade
                    isShow={
                      game.state === "spawning" || game.state === "first_spawning"
                    }
                    direction="up"
                    childClass="w-full h-[15%] flex flex-row gap-5"
                    className="w-full h-full flex flex-col items-center gap-5"
                  >
                    {minions.minions.map((x, i) => (
                      <div
                        key={`minnion${i}`}
                        className="w-full h-full flex flex-row gap-5"
                      >
                        <div className="w-[6rem] bg-[#0003] rounded-md flex justify-center items-center h-full">
                          <Draggable
                            disabled={
                              game.state !== "spawning" &&
                              game.state !== "first_spawning"
                            }
                            minion={i}
                            id={`minnion_${i}`}
                          >
                            {minionsImage[x.imageId](
                              calculateLeaderColor(user?.username),
                              1
                            )}
                          </Draggable>
                        </div>
                        <div className="w-[60%] h-full flex flex-col">
                          <div className=" rounded-md h-fit text-[1.2rem] w-full">
                            {x.name}
                          </div>
                          <div className=" rounded-md h-fit text-[1.0rem] w-full">
                            Defense
                          </div>
                          <div className="bg-[#2f2e2e] rounded-md h-fit text-[1.1rem] w-full px-2">
                            {x.defense}
                          </div>
                          <Button_v6
                            onClick={() => {
                              showInfo(i);
                            }}
                            className="mt-2 rounded-md text-[1.1rem]"
                          >
                            Info
                          </Button_v6>
                        </div>
                      </div>
                    ))}
                  </TextFade>
                </div>
                <div
                  style={{
                    pointerEvents: game.state === "buying" ? "all" : "none",
                  }}
                  className="absolute w-full h-full px-5 items-center gap-5 flex flex-col"
                >
                  <TextFade
                    isShow={game.state === "buying"}
                    direction="down"
                    className="w-full flex flex-col gap-2"
                  >
                    <div className="text-center text-[1.4rem] w-full h-[2rem]">
                      Conquering Hex
                    </div>
                    <Separator className="w-full" gradient={true} />
                    <div className="text-center w-full text-[1.1rem]">{`Price: ${room.config["hex_purchase_cost"]}`}</div>
                  </TextFade>
                </div>
                {/* </>:""} */}
              </motion.div>
              <div className="w-full p-5 flex flex-col gap-2">
                {onInfo < 0 ? (
                  ""
                ) : (
                  <>
                    <Button_v5
                      Icon="X"
                      hoverClass="bg-red-400"
                      onClick={() => showInfo(-1)}
                      className="absolute w-[5rem] top-4 right-5"
                    >
                      X
                    </Button_v5>
                    {/*                   <button
                      onClick={() => showInfo(-1)}
                      className="absolute bo top-4 right-5 bg-red-400 p-3 rounded-md px-5"
                    >
                      X
                    </button> */}
                    <div className="text-[1.5rem] text-center">Minion Detail</div>

                    {getMinionImage(minions.minions[onInfo].imageId)(
                      calculateLeaderColor(user?.username),
                      1
                    )}
                    <div className="text-[2rem] text-center">
                      {minions.minions[onInfo].name}
                    </div>
                    <div className="text-[1.2rem] text-center">{`Defense: ${minions.minions[onInfo].defense}`}</div>
                    <textarea
                      style={{ resize: "none" }}
                      disabled={true}
                      value={minions.minions[onInfo].script}
                      className="h-full text-[#fff] bg-[#52525299] rounded-md p-5 text-[1rem]"
                    ></textarea>
                  </>
                )}
              </div>
            </div>
          </div>
          <div className="flex flex-row gap-5 text-[1.3rem] pb-4 origin-bottom items-center px-5 pl-10 h-[10%] bg-[#0e0d0d]">
            <div className="w-[70%] flex flex-row gap-10 items-center">
              <div className="w-[20rem] items-center justify-start flex bg-[#1e1e1e] px-2 rounded-sm">
                <NumberFlow
                  prefix={"budget: "}
                  format={{ useGrouping: false }}
                  transformTiming={{ duration: 750, easing: "ease-out " }}
                  plugins={[continuous]}
                  suffix={` / ${room.config["max_budget"]}`}
                  value={game.budget}
                  className="text-[1.1rem] text-center"
                ></NumberFlow>
              </div>
              <div className="text-[1.1rem] text-center">{`Minion: ${game.minionAmount}/${room.config["max_spawns"]}`}</div>
              <div className="text-[1.1rem] text-center">{`Hex: ${game.hexAmount}`}</div>
            </div>
            <div className="flex h-full flex-row gap-5 justify-end items-center w-full px-10">
              <Button_v5 className="px-10 py-6" onClick={changeSpeed} Icon={speedButtonHoverText()}>
                {`x${speed}`}
              </Button_v5>
              <Button_v5
                onClick={() => {
                  nextState();
                }}
                disabled={game.state === "first_spawning"}
                Icon={stateButton()}
                className="w-[12rem] text-[1.1rem] py-6 px-10"
              >
                {stateButton()}
              </Button_v5>
            </div>
          </div>
        </div>
      </DndContext>
    );
  }
