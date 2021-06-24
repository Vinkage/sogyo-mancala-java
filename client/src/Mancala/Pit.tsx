import React, { useState } from "react";
import type { GameState } from "../gameState";
import "./Pit.css";

type Flatten<T> = T extends any[] ? T[number] : T;

type Player = Flatten<GameState["players"]>;

type Pit = Flatten<Player["pits"]>;

type PitProps = {
    player: Player;
    index: number;
    pit: Pit;
    onClick?: (index: number, player: Player, pit: Pit) => (event: React.MouseEvent<HTMLDivElement, MouseEvent>) => Promise<void>
    displayStones: (pit: Pit, spread: "small" | "big") => JSX.Element[]
}

type SmallBowl = Pit & {kind: "small"};
type Kalaha = Pit & {kind: "big"};



export function Pit({player, index, pit, onClick, displayStones}: PitProps) {
    const smallBowl = pit as SmallBowl;
    smallBowl.kind = "small";
    if (onClick) {
        return (
            <div className="Pit">
                {pit.nrOfStones > 10 ? "" + pit.nrOfStones : ""}
                <div className="Recession" onClick={onClick(index, player, pit)}>
                    <div className="stones" id={"pit" + pit.index}>
                        {displayStones(pit, smallBowl.kind)}
                    </div>
                </div>
            </div>
        );
    } else {
        return (
            <div className="Pit">
                <div className="Recession">
                    <div className="stones">
                        {pit.nrOfStones > 10 ? pit.nrOfStones : ""}
                        {displayStones(pit, smallBowl.kind)}
                    </div>
                </div>
            </div>
        );
    }
}

export function Kalaha({player, pit, displayStones}: PitProps) {
    const kalaha = pit as Kalaha;
    kalaha.kind = "big";
    return (
        <div className="Kalaha">
            {pit.nrOfStones > 10 ? pit.nrOfStones : ""}
            <div className="Recession">
                <div className="stones" id={"pit" + pit.index}>
                    {displayStones(pit, kalaha.kind)}
                </div>
            </div>
        </div>
    );
}
