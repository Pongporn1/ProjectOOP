import exp from "constants";
import { useEffect, useState } from "react";

export function useKeyboard() {
  
  const [key, setKey] = useState<string | null>("")

    const handler = ({ altKey, shiftKey, key}: KeyboardEvent) => {
        setKey(key)
  };

  const keyupHandle = () => {
    setKey("")
  }

  useEffect(() => {
    window.addEventListener("keydown", handler);
    window.addEventListener("keyup", keyupHandle)
    return () => {
      window.removeEventListener("keydown", handler);
      window.removeEventListener("keyup", keyupHandle)
    };
  }, []);

  return key
}

