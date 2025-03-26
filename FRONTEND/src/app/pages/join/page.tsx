"use client";
import { Button_v5 } from '@/app/components/EButton';
import { Input, InputBlock } from '@/app/components/EInput';
import { useWebSocket } from '@/hooks/useWebsocket';
import { useRouter } from 'next/navigation';
import { useDispatch } from 'react-redux';
import { selectUser } from '@/stores/slices/userSlice';
import { useEffect, useState, useCallback } from 'react';
import { setRoom } from '@/stores/slices/roomSlice';
import { useAppSelector } from '@/stores/hook';
import { TextFade } from '@/app/components/FadeUp';
import { motion } from 'framer-motion';
import Particles from 'react-tsparticles';
import { loadSlim } from 'tsparticles-slim';

export default function Home() {
  const dispatch = useDispatch();
  const router = useRouter();
  const { sendMessage, subscribe } = useWebSocket();
  const [roomIdToJoin, setRoomIdToJoin] = useState("");
  const [menu, setMenu] = useState("select");
  const [joinAlert, setJoinAlert] = useState("");

  const user = useAppSelector(selectUser);

  useEffect(() => {
    if (!user) router.push("/");
  }, [user, router]);

  const createGameHandle = (gameMode: string) => () => {
    subscribe(`/topic/games-create-${user?.username}`, (payload: any) => {
      dispatch(setRoom(JSON.parse(payload.body)));
      router.push("/pages/setting");
    });
    sendMessage(`/game/create`, { gameMode });
  };

  const joinGameHandle = () => {
    subscribe(`/topic/games-join-${user?.username}`, (payload: any) => {
      const room = JSON.parse(payload.body);
      if (room.id === "ER-FULL") {
        setJoinAlert("Room is already full");
      } else if (room.id === "ER-NONE") {
        setJoinAlert("Room doesn't exist");
      } else {
        dispatch(setRoom(room));
        router.push("/pages/setting");
      }
    });
    sendMessage(`/game/join`, { roomId: roomIdToJoin });
  };

  const particlesInit = useCallback(async (engine: any) => {
    await loadSlim(engine);
  }, []);

  const buttonClass = "w-full py-4 text-lg font-semibold bg-gradient-to-r from-gray-700 via-gray-600 to-gray-700 text-gray-200 rounded-xl shadow-md hover:shadow-lg hover:scale-105 transition-all duration-500";

  const containerVariants = {
    hidden: { opacity: 0, scale: 0.95 },
    visible: { opacity: 1, scale: 1, transition: { duration: 0.8, ease: "easeOut" } },
  };

  const buttonVariants = {
    initial: { opacity: 0, y: 20 },
    animate: (i: number) => ({
      opacity: 1,
      y: 0,
      transition: { delay: 0.2 + i * 0.1, duration: 0.5, ease: "easeOut" },
    }),
  };

  const modeData = [
    {
      mode: "duel",
      label: "P V P",
      iconLeft: "👤",
      iconRight: "👤",
      bg: "from-red-800 to-gray-800",
      desc: "Battle real players in real-time!",
      ring: "hover:ring-red-400"
    },
    {
      mode: "solitaire",
      label: "P V E",
      iconLeft: "👤",
      iconRight: "🤖",
      bg: "from-blue-800 to-gray-800",
      desc: "Challenge the AI and improve your skills!",
      ring: "hover:ring-blue-400"
    },
    {
      mode: "auto",
      label: "E V E",
      iconLeft: "🤖",
      iconRight: "🤖",
      bg: "from-purple-800 to-gray-800",
      desc: "Watch bots clash in full automation!",
      ring: "hover:ring-purple-400"
    }
  ];

  return (
    <div className="relative flex items-center justify-center min-h-screen bg-gradient-to-b from-gray-950 via-gray-900 to-black overflow-hidden">
      <Particles
        id="tsparticles"
        init={particlesInit}
        options={{
          fullScreen: { enable: false },
          background: { color: "transparent" },
          fpsLimit: 60,
          particles: {
            number: { value: 90 },
            color: { value: "#ccc" },
            links: { enable: true, color: "#ccc", distance: 120 },
            move: { enable: true, speed: 1 },
            shape: { type: "circle" },
            size: { value: 2 },
            opacity: { value: 0.7 },
          },
        }}
        className="absolute inset-0 -z-10"
      />

      <svg className="absolute w-full h-full pointer-events-none opacity-30" xmlns="http://www.w3.org/2000/svg" preserveAspectRatio="none">
        <defs>
          <pattern id="grid" width="40" height="40" patternUnits="userSpaceOnUse">
            <path d="M 40 0 L 0 0 0 40" fill="none" stroke="gray" strokeWidth="0.5" />
          </pattern>
        </defs>
        <rect width="100%" height="100%" fill="url(#grid)" />
      </svg>

      <div className="aurora-background absolute inset-0 z-[-20] pointer-events-none" />
      <div className="sparkle-layer absolute inset-0 z-[-10] pointer-events-none" />

      <motion.div
        variants={containerVariants}
        initial="hidden"
        animate="visible"
        className="max-w-4xl w-full bg-gradient-to-b from-gray-900 via-gray-800 to-gray-900 shadow-2xl border border-gray-700 rounded-3xl p-14 backdrop-filter backdrop-blur-xl relative z-10"
      >
        <div className="mb-12 text-center space-y-2">
          <motion.h1
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.5, duration: 0.8, ease: "easeOut" }}
            className="text-4xl sm:text-5xl font-bold text-transparent bg-clip-text bg-aurora animate-aurora glow-text"
          >
            Welcome
          </motion.h1>

          {user?.username && (
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.9, duration: 1, ease: "easeOut" }}
              className="text-3xl sm:text-4xl font-extrabold tracking-wide text-white drop-shadow-[0_2px_10px_rgba(0,255,255,0.7)] animate-float"
            >
              {user.username}
            </motion.div>
          )}
        </div>

        {menu === "select" && (
          <div className="space-y-6">
            {["Join Game", "Create Game", "Exit"].map((text, i) => (
              <motion.div key={text} variants={buttonVariants} initial="initial" animate="animate" custom={i}>
                <Button_v5
                  onClick={() => {
                    if (text === "Exit") {
                      localStorage.removeItem("username");
                      router.push("/");
                    } else {
                      setMenu(text === "Join Game" ? "join" : "create");
                    }
                  }}
                  className={buttonClass}
                  Icon={text.split(" ")[0]}
                >
                  {text}
                </Button_v5>
              </motion.div>
            ))}
          </div>
        )}

        {menu === "join" && (
          <TextFade direction="up" isShow className="space-y-6">
            <InputBlock
              className={`border transition-shadow duration-300 ${joinAlert ? "border-red-500 shadow-lg shadow-red-800" : "border-gray-700"}`}
              upSection={joinAlert && <div className="text-red-400 font-semibold">{joinAlert}</div>}
            >
              <Input
                placeholder="Enter Room ID"
                value={roomIdToJoin}
                onChange={(e) => {
                  setRoomIdToJoin(e.target.value);
                  setJoinAlert("");
                }}
              />
            </InputBlock>
            <div className="flex gap-4">
              <Button_v5 onClick={joinGameHandle} className={buttonClass} Icon="Join">Join</Button_v5>
              <Button_v5 onClick={() => setMenu("select")} className={buttonClass} Icon="Back">Back</Button_v5>
            </div>
          </TextFade>
        )}

        {menu === "create" && (
          <TextFade direction="up" isShow className="space-y-6">
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-6 text-white text-center">
              {modeData.map(({ mode, label, iconLeft, iconRight, bg, desc, ring }) => (
                <motion.div
                  key={mode}
                  whileHover={{ scale: 1.08, boxShadow: "0 0 20px rgba(0,255,255,0.5)" }}
                  className={`relative group p-6 rounded-xl bg-gradient-to-b ${bg} shadow-xl ${ring} cursor-pointer transition`}
                  onClick={createGameHandle(mode)}
                >
                  <div className="flex items-center justify-between text-4xl mb-2">
                    <motion.span animate={{ y: [0, -5, 0] }} transition={{ repeat: Infinity, duration: 2.5, ease: 'easeInOut' }}>{iconLeft}</motion.span>
                    <motion.span animate={{ scale: [1, 1.2, 1], opacity: [0.6, 1, 0.6] }} transition={{ repeat: Infinity, duration: 2, ease: 'easeInOut' }} className="text-red-400 font-bold">VS</motion.span>
                    <motion.span animate={{ y: [0, 5, 0] }} transition={{ repeat: Infinity, duration: 2.5, ease: 'easeInOut' }}>{iconRight}</motion.span>
                  </div>
                  <div className="text-white text-lg font-bold tracking-widest animate-pulse-smooth">{label}</div>
                  <motion.div
                    className="absolute bottom-2 left-1/2 transform -translate-x-1/2 text-xs text-cyan-300 opacity-0 group-hover:opacity-100 transition"
                    initial={{ opacity: 0 }}
                    whileHover={{ opacity: 1 }}
                  >
                    {desc}
                  </motion.div>
                </motion.div>
              ))}
            </div>
            <Button_v5 onClick={() => setMenu("select")} className={buttonClass} Icon="Back">Back</Button_v5>
          </TextFade>
        )}
      </motion.div>

      <style jsx global>{`
        @keyframes aurora {
          0% { background-position: 0% 50%; }
          50% { background-position: 100% 50%; }
          100% { background-position: 0% 50%; }
        }
        @keyframes flicker {
          0%, 100% { opacity: 0.2; }
          50% { opacity: 1; }
        }
        @keyframes float {
          0% { transform: translateY(0); }
          50% { transform: translateY(-6px); }
          100% { transform: translateY(0); }
        }
        .bg-aurora {
          background-image: linear-gradient(120deg, #00f0ff, #8affff, #d08bff, #ff70a6, #00f0ff);
        }
        .animate-aurora {
          animation: aurora 6s ease-in-out infinite;
        }
        .glow-text {
          text-shadow: 0 0 10px rgba(0, 255, 255, 0.3), 0 0 20px rgba(0, 255, 255, 0.3), 0 0 40px rgba(0, 255, 255, 0.2);
        }
        .animate-float {
          animation: float 5s ease-in-out infinite;
        }
        @keyframes pulseSmooth {
          0%, 100% { opacity: 0.6; transform: scale(1); }
          50% { opacity: 1; transform: scale(1.05); }
        }
        .animate-pulse-smooth {
          animation: pulseSmooth 2.5s ease-in-out infinite;
        }
        .aurora-background {
          background-image: linear-gradient(120deg, rgba(0, 240, 255, 0.15), rgba(138, 255, 255, 0.1), rgba(208, 139, 255, 0.15), rgba(255, 112, 166, 0.1), rgba(0, 240, 255, 0.15));
          background-size: 300% 300%;
          animation: aurora 20s ease-in-out infinite;
          filter: blur(60px);
          mix-blend-mode: screen;
        }
        .sparkle-layer {
          background-image: radial-gradient(white 1px, transparent 1px);
          background-size: 60px 60px;
          animation: flicker 3s infinite ease-in-out alternate;
          opacity: 0.25;
          mix-blend-mode: screen;
          filter: blur(0.8px);
        }
      `}</style>
    </div>
  );
}
