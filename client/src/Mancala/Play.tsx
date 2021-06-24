import React, { useState } from "react";
import type { GameState } from "../gameState";
import "./Play.css";
import { Pit, Kalaha } from "./Pit"

type Flatten<T> = T extends any[] ? T[number] : T;

type Player = Flatten<GameState["players"]>;

type Pit = Flatten<Player["pits"]>;

type PlayProps = {
    gameState: GameState;
    setGameState(newGameState: GameState): void;
}

function xyTranslate(pit: Pit, index: number) {
    var angle = index/pit.nrOfStones * 2 * Math.PI;
    var d = (index % 2 + 1) * 13;
    var ofX = -9;
    var ofY = -9;
    if (pit.index === 6 || pit.index === 13) {
        var xrandom = Math.random();
        var yrandom = Math.random();
        return 'translate('+ ((xrandom > 0.5 ? Math.random() : -Math.random()) * 18 - 10) + 'px, ' + (yrandom > 0.5 ? Math.random() : -Math.random()) * 60 + 'px)';
    } else {
        return 'translate('+ (Math.cos(angle) * (d) + ofX) + 'px, ' + (Math.sin(angle) * (d) + ofY) + 'px)';
    }
}

export function Play({ gameState, setGameState }: PlayProps) {

    function playPit(index: number, player: Player, pit: Pit) {
        const pitTotal = gameState.players[0].pits.length;
        const allPits = gameState.players[0].pits.concat(gameState.players[1].pits);
        const stonesToPass = pit.state.stoneElements as JSX.Element[];
        return async function event(event: React.MouseEvent<HTMLDivElement>) {
            if (!player.hasTurn) return;
            console.log("updating server");
            console.log(pit.nrOfStones);
            try {
                const response = await fetch('mancala/api/play', {
                    method: 'POST',
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({player: player.type === "player1" ? 0 : 1, index: index})
                });

                if (response.ok) {
                    const newGameState = await response.json();
                    localStorage.setItem("state", JSON.stringify(newGameState));
                    setGameState({gameState, ...newGameState});
                } else {
                    console.error(response.statusText);
                }
            } catch (error) {
                console.error(error.toString());
            }
            console.log(pit.nrOfStones);

        }
    }

    // function animate(newGameState: GameState) {
    //     var pits = newGameState.players[0].pits.concat(newGameState.players[1].pits);
    //     var type;
    //     for (var pit of pits) {
    //         type = pit.index === 6 || pit.index === 13 ? "big" : "small" as "big" | "small";
    //         displayStones(pit, type);
    //         setTimeout(() => {}, 1000);
    //     }
    // };

    function stoneStyler(pit: Pit, index: number, spread: "small" | "big", ) {
        const colors = ['gray', 'purple', 'blue', 'green'];
        var stoneStyle;
        if (spread === "small") {
            stoneStyle = {
                transform: xyTranslate(pit, index),
                background: colors[index % colors.length],
            };
        } else {
            stoneStyle = {
                transform: xyTranslate(pit, index),
                background: colors[index % colors.length],
            };
        }
        return stoneStyle;
    }

    function displayStones(pit: Pit, spread: "small" | "big") {
        var jsx;
        var kind = pit.index === 6 || pit.index === 13 ? "big" : "small" as "small"|"big";
        if (pit.state.stoneElements === undefined) {
            pit.state.stoneElements = [];
            for (let i = 0; i < pit.nrOfStones; i++) {
                jsx = ( <div style={stoneStyler(pit, i, kind)} key={i * (pit.index + 1)} className="Stone"/> );
                pit.state.stoneElements.reverse().push(jsx);
            }
        } else if (pit.nrOfStones < pit.state.stoneElements.length) {
            // while (pit.nrOfStones < pit.state.stoneElements.length) {
            //     pit.state.stoneElements.pop();
            // }
            pit.state.stoneElements = [];
            for (let i = 0; i < pit.nrOfStones; i++) {
                jsx = ( <div style={stoneStyler(pit, i, kind)} key={i * (pit.index + 1)} className="Stone"/> );
                pit.state.stoneElements.reverse().push(jsx);
            }
        } else if (pit.nrOfStones > pit.state.stoneElements.length) {
            pit.state.stoneElements = [];
            for (let i = 0; i < pit.nrOfStones; i++) {
                jsx = ( <div style={stoneStyler(pit, i, kind)} key={i * (pit.index + 1)} className="Stone"/> );
                pit.state.stoneElements.reverse().push(jsx);
            }
        }
        return pit.state.stoneElements;
    }

    const playerPits = gameState.players.map(
        player => {
            return player.pits.slice(0, -1).map(
                (pit, index) => {
                    const [ pitState, setPitState ] = useState<Pit["state"]>({stoneElements: undefined});
                    pit.state = pitState;
                    pit.setPitState = setPitState;
                    return (
                    <Pit
                    player={player}
                    index={player.type === "player1" ? index : player.pits.length + index}
                    key={player.type === "player1" ? index : player.pits.length + index}
                    pit={pit}
                    onClick={playPit}
                    displayStones={displayStones}
                    />
                    )
                });
            });

    const playerKalahas = gameState.players.map(
        (player, index) => {
            const [ kalahaState, setKalahaState ] = useState<Pit["state"]>({stoneElements: undefined});
            const pit = player.pits[player.pits.length - 1];
            pit.state = kalahaState;
            pit.setPitState = setKalahaState;
            return (
                <Kalaha
                player={player}
                index={player.type === "player1" ? player.pits.length - 1 : 2 * (player.pits.length) - 1}
                key={index}
                pit={pit}
                displayStones={displayStones}
                />
            )
        }
    );

    var player2Pits = [];
    var player1Pits = [];

    for (var i = 0; i < playerPits[0].length; i++) {
        player1Pits.push(playerPits[0][i]);
        player2Pits.push(playerPits[1][i]);
    }

    const revenge = (
        <button onClick={async () => {
            localStorage.clear();
            try {
                const response = await fetch('mancala/api/start', {
                    method: 'POST',
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ nameplayer1: gameState.players[0].name, nameplayer2: gameState.players[1].name })
                });

                if (response.ok) {
                    const gameState = await response.json();
                    setGameState(gameState);
                } else {
                    console.error(response.statusText);
                }
            } catch (error) {
                console.error(error.toString());
            }

            }}>
        game has ended... play again?
        </button>
    );

    return (
        <div className="playArea">
        <div className="playerStatus" id="statusPlayer1">
            {gameState.players[1].name} has {gameState.players[1].pits.reduce((sum, current) => {return (sum + current.nrOfStones)}, 0)} stones.
            {gameState.players[1].hasTurn ? " Also has the current turn" : ""}
        </div>
        <div id="board">
            {playerKalahas[1]}
            <div id="pits">
                {player2Pits.reverse()}
                {player1Pits}
            </div>
            {playerKalahas[0]}
        </div>
        <div className="playerStatus" id="statusPlayer1">
            {gameState.players[0].name} has {gameState.players[0].pits.reduce((sum, current) => {return (sum + current.nrOfStones)}, 0)} stones.
            {gameState.players[0].hasTurn ? "Also has the current turn" : ""}
        </div>
        {gameState.gameStatus.endOfGame ? revenge : ""}
        </div>
    )
}
