package mancala.domain;

public class Player {
    private boolean hasTheTurn;
    private boolean isTheWinner;
    final private Player opponent;

    public Player() {
        // player has the turn when created
        this.hasTheTurn = true;

        this.opponent = new Player(this);
    }

    private Player(Player opponent) {
        this.hasTheTurn = false;

        this.opponent = opponent;
    }

    public Player getOpponent() {
        return opponent;
    }

    public boolean hasTheTurn() {
        return hasTheTurn;
    }

    public void switchTurn() {
        if (this.hasTheTurn == true) {
            this.hasTheTurn = false;
            this.opponent.hasTheTurn = true;
        } else {
            this.hasTheTurn = true;
            this.opponent.hasTheTurn = false;
        }
    }

    public boolean won() {
        return this.isTheWinner;
    }

    void isTheWinner() {
        this.isTheWinner = true;
        this.opponent.isTheWinner = false;
    }

    void gotADraw() {
        this.isTheWinner = true;
        this.opponent.isTheWinner = true;
    }

}
