
export interface GameState {
    players: [ Player, Player ]; // a player array contains exactly two Players
    gameStatus: {
        endOfGame: boolean;
    };
}

interface Player {
    name: string;
    pits: Pit[];
    type: "player1" | "player2"; // only "player1" and "player2" are valid options for this string
    hasTurn: boolean;
}

interface Pit {
    index: number;
    nrOfStones: number;
    state: PitState;
    setPitState: (newPitState: PitState) => void;
}


interface PitState {
    stoneElements: JSX.Element[] | undefined;
    }

