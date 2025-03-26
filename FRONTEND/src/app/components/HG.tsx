import React from "react";
import "./HG.css";

const HexGrid = ({ rows = 5, cols = 5 }) => {
  const hexagons = [];
  const hexWidth = 60; // Adjust width for flat-top hexagons
  const hexHeight = Math.sqrt(3) / 2 * hexWidth; // Height calculation for flat-top hexagons

  for (let row = 0; row < rows; row++) {
    for (let col = 0; col < cols; col++) {
      const xOffset = col * (hexWidth * 0.75);
      const yOffset = row * hexHeight 
      hexagons.push(
        <div
          key={`${row}-${col}`}
          className="hex"
          style={{ transform: `translate(${xOffset}px, ${yOffset}px)` }}
        ></div>
      );
    }
  }
  return <div className="hex-grid">{hexagons}</div>;
};

export default HexGrid;
