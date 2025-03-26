"use client";
import { useEffect, useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { useRouter } from "next/navigation";
import Logo from "@/app/components/Logo";
import { IoLogIn } from "react-icons/io5";
import {
  Route,
  BrowserRouter as Router,
  Routes,
  useLocation,
} from "react-router-dom";
import { Minion1 } from "./components/minion";
import { useAppSelector } from "@/stores/hook";
import { selectWebsocket } from "@/stores/slices/webSocketSlice";
import { TextFade } from "./components/FadeUp";
import { Button_v5 } from "./components/EButton";
import { Input, InputBlock } from "./components/EInput";
import { useDispatch } from "react-redux";
import { selectUser, setUser } from "@/stores/slices/userSlice";
import { Message, StompSubscription } from "@stomp/stompjs";
import { useWebSocket } from "@/hooks/useWebsocket";
import Stomp from "stompjs";

export default function Menu() {
  const [userSubscription, setUserSubscription] = useState<StompSubscription>();
  const { unsubscribe, subscribe, sendMessage } = useWebSocket();

  const webSocket = useAppSelector(selectWebsocket);

  useEffect(() => {
    setUserSubscription(userSubscription);
    return () => {
      if (userSubscription) {
        unsubscribe(userSubscription);
      }
    };
  }, [userSubscription]);

  const [buttonStatus, setButtonStatus] = useState<
    "idle" | "transitioning" | "connecting"
  >("connecting");
  const [logoMoved, setLogoMoved] = useState(false);
  const [showMenu, setShowMenu] = useState(false);
  const router = useRouter();

  const dispatch = useDispatch();
  const user = useAppSelector(selectUser);

  const { client, isConnected } = useAppSelector(selectWebsocket);

  const redColor = "#F46451";
  const blueColor = "#528BEF";

  const buttonVariant = {
    connecting: { opacity: 1 },
    transitioning: { opacity: 0, transition: { duration: 0.5 } },
    idle: { opacity: [0, 1] },
  };

  const startTransition = () => {
    setButtonStatus("transitioning");
    setTimeout(() => {
      setLogoMoved(true);
      setTimeout(() => setShowMenu(true), 500); // Show menu after logo moves
    }, 500);
  };

  useEffect(() => {
    console.log(isConnected);
    if (isConnected) {
      console.log("idle");
      setButtonStatus("idle");
    } else {
      console.log("connecting");
      setButtonStatus("connecting");
    }
  }, [isConnected]);

  const [usernameInput, setUsernameInput] = useState<string>("");

  const onLoginSuccessed = (payload: Stomp.Message) => {
    const user = JSON.parse(payload.body);
    console.log(user);
    if (user["username"] === usernameInput) {
      dispatch(setUser(user));
      payload.ack();
      router.push(`pages/join`);
      localStorage.setItem("username", user["username"]);
      sessionStorage.setItem("username", user["username"]);
    } else {
      setAlertInput(user["username"]);
    }
  };

  const loginHandle = async () => {
    const sub = subscribe(
      `/topic/connected-${usernameInput}`,
      onLoginSuccessed
    );
    setUserSubscription(sub);
    sendMessage(`/login`, { username: usernameInput });
  };

  useEffect(() => {
    console.log("load uername");
    const un = localStorage.getItem("username");
    if (un !== null) {
      console.log("un", un);
      const sub = subscribe(`/topic/connected-${un}`, onLoginSuccessed);
      setUsernameInput(un);
      setUserSubscription(sub);
      sendMessage(`/login`, { username: un });
    }
  }, [webSocket.isConnected, webSocket.client]);

  const [alertInput, setAlertInput] = useState<string>("");

  return (
    <>
      <div className="flex flex-col h-screen justify-center items-center relative">
        <motion.div
          initial={{ y: 0 }}
          animate={{ y: logoMoved ? -200 : 0 }} // Moves logo to the top
          transition={{ duration: 1, ease: "easeOut" }}
          className="absolute flex flex-col z-10"
        >
          <TextFade isShow={true} direction="up">
            <Logo />
          </TextFade>
          <motion.button
            onClick={startTransition}
            animate={buttonStatus}
            variants={buttonVariant}
            disabled={!isConnected}
            style={{
              pointerEvents:
                isConnected && buttonStatus !== "transitioning"
                  ? "all"
                  : "none",
            }}
            initial={{ opacity: 1 }}
            transition={{
              repeat: Infinity,
              duration: 2,
              repeatType: "reverse",
            }}
            className="mt-10 text-[1rem]"
          >
            {isConnected ? "Click to Continue" : "Connecting..."}
          </motion.button>
        </motion.div>

        {/* Menu */}
        {showMenu && (
          <>
            <div
              style={{ opacity: alertInput !== "" ? 1 : 0 }}
              className="w-[30rem] mt-5 text-start text-red-400"
            >
              {alertInput}
            </div>
            <motion.div
              initial={{ opacity: 0, y: 50 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.7 }}
              className="w-[30rem] items-center justify-center flex flex-row gap-5 z-10"
            >
              <InputBlock
                className={`w-full ${
                  alertInput !== "" ? "border-red-400 dark:shadow-red-500" : ""
                }`}
                variant={"neubrutalism"}
              >
                <Input
                  onChange={(e) => {
                    setUsernameInput(e.target.value), setAlertInput("");
                  }}
                  placeholder="Username"
                  className="text-start"
                />
              </InputBlock>
              <Button_v5
                onClick={loginHandle}
                className=""
                Icon={<IoLogIn size="1.5rem" />}
              >
                Login
              </Button_v5>
            </motion.div>
          </>
        )}

        {/* Animated maskDiv with loading effect */}
        <div className="h-screen w-screen absolute flex flex-col justify-center items-center">
  <motion.div
    id="maskDiv"
    
    className="halftone w-[271px] h-[272px] mt-auto mb-[4.5rem] absolute"
    style={{ clipPath: "url(#maskRect1)" }}
    animate={{ y: [0, -20, 0] }}
    transition={{ duration: 1.5, repeat: Infinity, repeatType: "reverse" }}
  ></motion.div>
</div>
<svg>
  <defs>
    <clipPath id="maskRect1">
      <path
        transform="scale(0.5)"
        d="M1.42209 204.894C0.0795275 199.706 1.60676 194.196 5.4285 190.439L194.002 5.07265C197.824 1.31592 203.359 -0.116567 208.524 1.31478L463.343 71.9411C468.507 73.3724 472.515 77.4502 473.858 82.6382L540.103 338.631C541.446 343.819 539.919 349.329 536.097 353.086L347.523 538.453C343.702 542.209 338.166 543.642 333.002 542.211L78.1827 471.584C73.0184 470.153 69.01 466.075 67.6674 460.887L1.42209 204.894Z"
        fill="#D9D9D9"
      />
    </clipPath>
  </defs>
</svg>
      </div>
    </>
  );
}
