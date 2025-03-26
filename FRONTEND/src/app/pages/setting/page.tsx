"use client";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { useDispatch } from "react-redux";
import { setSettings } from "@/stores/slices/gameSlice";
import { TextFade } from "@/app/components/FadeUp";
import { Input, InputBlock } from "@/app/components/EInput";
import { useAppSelector } from "@/stores/hook";
import {
  changeConfig,
  selectRoom,
  setConfig,
  setRoom,
} from "@/stores/slices/roomSlice";
import { useWebSocket } from "@/hooks/useWebsocket";
import { Message, StompSubscription } from "@stomp/stompjs";
import Stomp from "stompjs";
import { Button_v5 } from "@/app/components/EButton";
import { selectUser } from "@/stores/slices/userSlice";
import { useKeyboard } from "@/hooks/useKeyboard";
import useFocus from "@/hooks/useFocus";
import { UserRoundCheck, Clock4, Swords, ChevronDown, ShieldCheck, Sparkle } from "lucide-react";

export default function Setting() {
  const router = useRouter();
  const dispatch = useDispatch();
  const room = useAppSelector(selectRoom);
  const user = useAppSelector(selectUser);
  const { sendMessage, subscribe, unsubscribe } = useWebSocket();
  const [userSubscription, setUserSubscription] = useState<StompSubscription>();
  const [roomSubscription, setRoomSubscription] = useState<StompSubscription>();

  interface SettingItem {
    setting: string;
    value: number;
  }

  const [confirm, setConfirm] = useState<boolean>(false);
  const [loading, setLoading] = useState<boolean>(true);
  const [settings, setSetting] = useState<SettingItem[]>([]);
  const [keyCommand, setKeyCommand] = useState<string>("");
  const [commandErr, setCommandErr] = useState<string>("");
  const k = useKeyboard();
  const roomInput = useFocus<HTMLInputElement>();
  const [isFocus, setFocus] = useState<boolean>(false);

  useEffect(() => {
    setUserSubscription(userSubscription);
    return () => userSubscription && unsubscribe(userSubscription);
  }, [userSubscription]);

  useEffect(() => {
    setRoomSubscription(roomSubscription);
    return () => roomSubscription && unsubscribe(roomSubscription);
  }, [roomSubscription]);

  useEffect(() => {
    const timeout = setTimeout(() => {
      setUserSubscription(subscribe(`/topic/gamesConfig`, onSettingChange));
      setRoomSubscription(subscribe(`/topic/room-${room?.id}`, onRoomUpdate));
      const st: SettingItem[] = Object.entries(room.config).map(([key, val]) => ({
        setting: key,
        value: val,
      }));
      setSetting(st);
    }, 0);
    return () => clearTimeout(timeout);
  }, []);

  useEffect(() => {
    const st: SettingItem[] = Object.entries(room.config).map(([key, val]) => ({
      setting: key,
      value: val,
    }));
    setSetting(st);
  }, [room.config]);

  const onSettingChange = (payload: Message) => {
    const config = JSON.parse(payload.body);
    dispatch(setConfig(config));
  };

  useEffect(() => {
    setLoading(settings.length === 0);
  }, [settings]);

  const saveSetting = () => {
    const config: Record<string, number> = {};
    settings.forEach((x) => (config[x.setting] = x.value));
    dispatch(setSettings(config));
  };

  const changeSetting = (key: string, value: number) => {
    if (value > 9223372036854775000) value = 9223372036854775000;
    const updated = settings.map((x) =>
      x.setting === key ? { setting: key, value } : x
    );
    sendMessage(`/room/editConfig`, { roomId: room.id, setting: key, value });
    setSetting(updated);
  };

  const onRoomUpdate = (payload: Message) => {
    const cm = eval(`payload.headers.command`);
    const data = JSON.parse(payload.body);
    if (cm === "update") dispatch(setRoom(data));
    else if (cm === "next") {
      saveSetting();
      dispatch(setRoom(data));
      router.push("/pages/create");
    }
  };

  useEffect(() => {
    if (user?.username === room.leader1) setConfirm(room.leader1Confirm);
    if (user?.username === room.leader2) setConfirm(room.leader2Confirm);
  }, [room]);

  const sendComfirmMessage = () => {
    sendMessage(`/room/confirmConfig`, {
      roomId: room.id,
      username: user?.username,
      confirmed: !confirm,
    });
  };

  useEffect(() => {
    if (k && !isFocus) {
      setCommandErr("");
      if (k === "Enter") {
        if (keyCommand === "next") {
          if (!confirm) sendComfirmMessage();
          setKeyCommand("");
        } else if (keyCommand === "cancle") {
          if (confirm) sendComfirmMessage();
          setKeyCommand("");
        } else {
          const matched = settings.find(({ setting }) =>
            keyCommand.startsWith(setting + " ")
          );
          if (matched) {
            const val = Number(keyCommand.split(" ")[1]);
            changeSetting(matched.setting, val);
            setKeyCommand("");
          } else {
            setCommandErr("Don't have this command");
          }
        }
      } else if (k === "Backspace") {
        setKeyCommand((prev) => prev.slice(0, -1));
      } else if (/^[a-zA-Z0-9 _]$/.test(k)) {
        setKeyCommand((prev) => prev + k);
      }
    }
  }, [k]);

  const PlayerCard = ({ name, isConfirmed, role }: { name: string; isConfirmed: boolean; role: string }) => {
    return (
      <div
        className={`relative w-full text-center px-6 py-8 rounded-3xl transition-all duration-300 shadow-2xl border border-white/10 bg-gradient-to-tr ${isConfirmed ? 'from-green-500/80 to-emerald-500/90' : 'from-zinc-700 to-zinc-800'}`}
      >
        <div className="absolute -top-4 left-1/2 -translate-x-1/2 bg-black/70 px-4 py-1 rounded-full text-xs tracking-wider font-semibold text-white shadow-md uppercase flex items-center gap-1">
          <ShieldCheck className="w-3 h-3" /> {role}
        </div>
        <div className="flex flex-col items-center space-y-3">
          <div className="w-16 h-16 rounded-full bg-gradient-to-br from-emerald-200 to-white/80 flex items-center justify-center text-zinc-900 font-bold text-xl shadow-inner">
            {name.charAt(0).toUpperCase()}
          </div>
          <div className="text-xl font-semibold tracking-wide">{name}</div>
          <div className={`text-sm ${isConfirmed ? 'text-white' : 'text-zinc-300'} flex items-center gap-1`}>
            {isConfirmed ? <Sparkle className="w-4 h-4 animate-pulse" /> : <Clock4 className="w-4 h-4 animate-pulse" />}
            {isConfirmed ? 'Confirmed' : 'Waiting...'}
          </div>
        </div>
      </div>
    );
  };

  return (
    <div className="min-h-screen w-full bg-gradient-to-br from-[#0a0f1c] to-[#1f2937] text-white flex flex-col overflow-hidden font-sans relative">
      <div className="fixed top-0 left-0 w-full z-50 pointer-events-none">
        <div className={`text-center py-2 bg-black/60 text-sm tracking-wide font-mono transition-all duration-300 ${commandErr ? 'text-red-400 animate-pulse' : 'text-white'}`}>{commandErr || keyCommand}</div>
      </div>

      {confirm && (
        <div className="fixed inset-0 bg-black/80 z-50 flex flex-col items-center justify-center space-y-6 animate-fadeIn">
          <div className="text-white text-3xl font-bold tracking-tight flex items-center gap-2">
            <Clock4 className="w-7 h-7 animate-pulse" /> Waiting for Other Player to Confirm
          </div>
          <Button_v5 Icon="Cancel" className="bg-red-600 hover:bg-red-700 text-lg px-8 py-3 rounded-full shadow-lg transition-all duration-300" onClick={sendComfirmMessage}>
            Cancel
          </Button_v5>
        </div>
      )}

      <div className="flex-1 flex flex-col md:flex-row overflow-hidden">
        <aside className="w-full md:w-[20%] min-w-[220px] bg-zinc-900/60 p-6 flex items-center justify-center border-r border-white/10 animate-slideInLeft">
          <PlayerCard name={room.leader1} isConfirmed={room.leader1Confirm} role="Player 1" />
        </aside>

        <main className="flex-1 px-6 md:px-12 py-10 space-y-8 overflow-y-auto max-h-screen animate-fadeInUp">
          <div className="max-w-6xl mx-auto bg-[#111827]/80 backdrop-blur-xl p-10 rounded-3xl shadow-2xl border border-zinc-700 relative">
            <div className="absolute -top-5 left-1/2 -translate-x-1/2 bg-[#111827] rounded-full px-6 py-2 text-emerald-400 shadow-md text-sm flex items-center gap-2 uppercase tracking-widest font-bold"><Swords className="w-4 h-4" /> Game Setup</div>
            <h2 className="text-4xl font-extrabold mb-6 tracking-tight text-center text-white drop-shadow">Game Settings</h2>
            <div className="text-sm text-gray-400 mb-8 text-center">Room ID: <span className="font-mono text-white text-base">{room.id}</span></div>
            <TextFade isShow={!loading} direction="up" className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-8">
              {settings.map((element) => (
                <div key={element.setting} className="flex flex-col gap-2">
                  <label className="capitalize text-sm font-semibold text-gray-300 tracking-wide flex items-center gap-1">
                    {element.setting.replace(/_/g, ' ')}
                    <ChevronDown className="w-4 h-4 text-zinc-400" />
                  </label>
                  <InputBlock variant="neubrutalism">
                    <Input
                      min={0}
                      max={9223372036854775807}
                      type="number"
                      className="text-right px-4 py-2 rounded-lg bg-zinc-900 text-white border border-zinc-600 focus:ring-2 focus:ring-emerald-400 shadow-sm"
                      value={element.value}
                      onFocus={() => setFocus(true)}
                      onBlur={() => setFocus(false)}
                      onChange={(e) => changeSetting(element.setting, e.target.valueAsNumber)}
                    />
                  </InputBlock>
                </div>
              ))}
            </TextFade>
          </div>

          <div className="flex justify-center gap-8 pt-6">
            <button className="bg-gradient-to-r from-zinc-700 to-zinc-800 hover:from-zinc-600 hover:to-zinc-700 px-8 py-3 rounded-xl font-semibold tracking-wide shadow-lg transition-all duration-200" onClick={() => { saveSetting(); router.push("/pages/join"); }}>
              Back
            </button>
            <button className="bg-gradient-to-r from-green-400 to-emerald-500 hover:from-green-300 hover:to-emerald-400 px-10 py-3 rounded-xl font-semibold tracking-wide shadow-xl transition-all duration-200" onClick={sendComfirmMessage}>
              Confirm
            </button>
          </div>
        </main>

        <aside className="w-full md:w-[20%] min-w-[220px] bg-zinc-900/60 p-6 flex items-center justify-center border-l border-white/10 animate-slideInRight">
          <PlayerCard name={room.leader2} isConfirmed={room.leader2Confirm} role="Player 2" />
        </aside>
      </div>
    </div>
  );
}