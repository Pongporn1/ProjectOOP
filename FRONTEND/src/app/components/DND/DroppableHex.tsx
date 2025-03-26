// === DroppableHex.tsx ===
import React, { CSSProperties, ReactNode, useEffect, useState } from "react";
import { UniqueIdentifier, useDroppable } from "@dnd-kit/core";

export function DroppableHex(props: {
  id: UniqueIdentifier,
  row: number,
  col: number,
  minion: ReactNode,
  bomb: String,
  x: number,
  y: number,
  buyableOverlay: boolean,
  border_color: string,
  border_up: boolean,
  border_down: boolean,
  border_downleft: boolean,
  border_downright: boolean,
  border_upleft: boolean,
  border_upright: boolean,
  price: number,
  onBuying: () => void,
  isSpawningCommand: boolean,
  isBuyingCommand: boolean,
  spawningPos: { row: number, col: number }
}) {
  const { isOver, setNodeRef } = useDroppable({
    id: props.id,
    data: { row: props.row, col: props.col },
  });

  const [isHover, setHover] = useState(false);
  const [strokeColor, setStrokeColor] = useState("white");
  const [textColor, setTextColor] = useState("fill-white");

  const style: CSSProperties = {
    transform: `translate(${props.x * 0.75 + props.col * 0}px, ${(Math.sqrt(3) / 2) * props.y + 87 / 2 + props.row * 0}px)`,
    pointerEvents: "none",
  };

  const hexagonPoints = (radius: number) => {
    const halfWidth = (radius * Math.sqrt(3)) / 2;
    return `
      ${0},${halfWidth}
      ${radius / 2},${2 * halfWidth}
      ${(3 * radius) / 2},${2 * halfWidth}
      ${2 * radius}, ${halfWidth}
      ${(3 * radius) / 2},${0}
      ${radius / 2},${0}`;
  };

  const edgeList = (radius: number) => {
    const halfWidth = (radius * Math.sqrt(3)) / 2;
    return [
      [0, halfWidth],
      [radius / 2, 2 * halfWidth],
      [(3 * radius) / 2, 2 * halfWidth],
      [2 * radius, halfWidth],
      [(3 * radius) / 2, 0],
      [radius / 2, 0],
    ];
  };

  useEffect(() => {
    if (isOver) {
      setStrokeColor("z-30 fill-[#0E0E0E]");
      setTextColor("fill-[#ff0000]");
    } else if (props.bomb !== "?" && props.bomb !== "X") {
      setTextColor(props.bomb === "0" ? "fill-none" : "fill-[#00ff00]");
    } else if (isHover && props.bomb === "?") {
      setTextColor("fill-[#0000ff]");
    } else if (props.bomb === "X") {
      setTextColor("fill-[#ff0000]");
    } else {
      setTextColor("fill-[#505050]");
    }
  }, [isHover, isOver, props.bomb]);

  const getNeonColor = (color: string) => {
    if (color.toLowerCase() === "#305cde") return "#6ed3ff"; // neon blue
    if (color.toLowerCase() === "#e55451") return "#ff3b3b"; // neon red
    return "#aaaaaa";
  };

  return (
    <div className={`w-[100px] h-[87px] absolute`} style={style}>
      <div className="absolute z-40 w-full h-full p-3 flex justify-center items-center" ref={setNodeRef}>
        <div style={{ pointerEvents: "none", opacity: props.buyableOverlay || props.isSpawningCommand ? 0.3 : 1 }} className="w-full h-full flex justify-center items-center">
          {props.minion}
        </div>
        <div className="flex absolute justify-center items-center flex-col">
          {props.buyableOverlay && <div className="z-50 text-white">{`${props.price} $`}</div>}
          {(props.isSpawningCommand || props.isBuyingCommand) && <div className="z-50 text-white/80">{`${props.row} ${props.col}`}</div>}
        </div>
      </div>
      <svg className={`w-[100px] h-[87px] absolute ${strokeColor}`} key={`${props.x}${props.y}`} style={{ pointerEvents: "none" }}>
        <polygon
          style={{ fill: "#3e3937", pointerEvents: "auto", transition: "transform 0.2s ease", transform: isHover ? "scale(1.05)" : "scale(1)" }}
          onMouseEnter={() => setHover(true)}
          onMouseLeave={() => setHover(false)}
          points={hexagonPoints(50)}
          onClick={() => { if (props.buyableOverlay) props.onBuying(); }}
        />
        {[props.border_downleft, props.border_down, props.border_downright, props.border_upright, props.border_up, props.border_upleft].map((border, i) => (
          <line
            key={i}
            style={{
              pointerEvents: "auto",
              strokeDasharray: "100, 350",
              strokeWidth: 2,
              stroke: border ? getNeonColor(props.border_color) : `#000a`,
              filter: border ? `drop-shadow(0 0 6px ${getNeonColor(props.border_color)})` : "none"
            }}
            onMouseEnter={() => setHover(true)}
            onMouseLeave={() => setHover(false)}
            x1={edgeList(50)[i][0]}
            x2={edgeList(50)[(i + 1) % 6][0]}
            y1={edgeList(50)[i][1]}
            y2={edgeList(50)[(i + 1) % 6][1]}
          />
        ))}
      </svg>
    </div>
  );
}