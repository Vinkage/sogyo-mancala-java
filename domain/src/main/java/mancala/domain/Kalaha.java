package mancala.domain;

class Kalaha implements Bowl {
    private int myRocks;
    private final Player playerThatOwnsMe;
    private final Bowl nextBowl;

    public Kalaha() {
        // constructs board by recursively calling either kalaha or smallbowl constructors
        this.myRocks = 0;
        this.playerThatOwnsMe = new Player();
        int boardSize = 14;

        this.nextBowl = new SmallBowl(boardSize, --boardSize, this, this.getPlayerThatOwnsMe().getOpponent());
    }

    Kalaha(int boardSize, int remainingBowls, Bowl startBowl, Player playerOwningThisSide) {
        this.myRocks = 0;
        this.playerThatOwnsMe = playerOwningThisSide;
        if (remainingBowls == 1) {
            this.nextBowl = startBowl;
        } else {
            this.nextBowl = new SmallBowl(boardSize, --remainingBowls, startBowl, playerOwningThisSide.getOpponent());
        }
    }

    @Override
    public int getMyRocks() {
        return this.myRocks;
    }

    @Override
    public Bowl getNextBowl() {
        return this.nextBowl;
    }

    @Override
    public Player getPlayerThatOwnsMe() {
        return this.playerThatOwnsMe;
    }

    @Override
    public Bowl distribute(int remainingRocks) {
        this.myRocks++;
        // Skip?
        if (!getPlayerThatOwnsMe().hasTheTurn()) {
            this.myRocks--;
            return getNextBowl().distribute(remainingRocks);
        } // last ?
        else if (remainingRocks == 1)
            return this;
        else
            return getNextBowl().distribute(--remainingRocks);
    }

    void acceptBooty(int booty) {
        myRocks = myRocks + booty;
    }
}
