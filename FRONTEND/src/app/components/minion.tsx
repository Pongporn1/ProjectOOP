import { motion } from "framer-motion";
export function Minion1({ col, scale }: { col: string; scale: number }) {
  return (
    <>
      <svg
        scale={scale}
        xmlns="http://www.w3.org/2000/svg"
        id="Layer_3"
        viewBox="0 0 512 512"
      >
        <polygon
          style={{ fill: "#545554" }}
          className="cls-1"
          points="132.36 326.53 241.37 217.53 241.37 104.59 19.59 326.37 132.36 326.53"
        />
        <polygon
          style={{ fill: "#545554" }}
          className="cls-1"
          points="379.64 326.53 270.63 217.53 270.63 104.59 492.41 326.37 379.64 326.53"
        />
        <motion.rect
          animate={{ rotate: [45, 45, 135], scale: [1, 0.4, 1] }}
          transition={{
            repeat: Infinity,
            delay: 0,
            duration: 1.5,
            repeatType: "loop",
            repeatDelay: 1,
            times: [0, 0.3, 1],
          }}
          initial={{ fill: col }}
          className="cls-2"
          x="192.24"
          y="254.88"
          width="126.35"
          height="126.35"
          transform="translate(299.71 -87.45) rotate(45)"
        />
      </svg>
    </>
  );
}

export function Minion2({ col, scale }: { col: string; scale: number }) {
  return (
    <>
      <svg
        scale={scale}
        xmlns="http://www.w3.org/2000/svg"
        id="Layer_2"
        viewBox="0 0 512 512"
      >
        <path
          style={{ fill: "#545554" }}
          d="M358.91,245.33c-4.15,36.35-22.88,57.11-43.25,68.97l-59.66,157.75-59.65-157.75c-20.38-11.86-39.11-32.63-43.25-68.97l25.99-9.94,76.92-76.92,76.93,76.92,25.99,9.94Z"
        />
        <motion.polygon
          animate={{ translateY: [-10, -20, -10], scale: [1, 1.1, 1] }}
          transition={{
            duration: 2,
            times: [0, 0.4, 1],
            repeatDelay: 0.5,
            repeat: Infinity,
            repeatType: "loop",
          }}
          initial={{ fill: col }}
          points="414.75 266.69 358.91 245.33 332.92 235.39 255.99 158.48 179.08 235.39 153.09 245.33 97.25 266.69 255.99 39.95 414.75 266.69"
        />
      </svg>
    </>
  );
}

export const Minion3 = ({ col, scale }: { col: string; scale: number }) => (
  <svg xmlns="http://www.w3.org/2000/svg" id="Layer_3" viewBox="0 0 500 500">
    <defs>
      <style>
        {`.cls-1 {
        fill: #545554;
      }

      .cls-2 {
        fill: ${col};
      }`}
      </style>
    </defs>
    <circle className="cls-2" cx="250" cy="325.07" r="86.81" />
    <path
      className="cls-1"
      d="M485.29,325.07h-120.67c0-63.3-51.32-114.62-114.62-114.62s-114.62,51.32-114.62,114.62H14.71s42.73-18.04,76.52-51.64c16.18-49.81,55.15-89.33,104.6-106.29,35.22-34.16,54.16-79.02,54.16-79.02,0,0,18.94,44.87,54.16,79.02,49.45,16.96,88.42,56.48,104.6,106.29,33.79,33.61,76.52,51.64,76.52,51.64Z"
    />
  </svg>
);