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
        return myRocks;
    }

    @Override
    public Bowl getNextBowl() {
        return nextBowl;
    }

    @Override
    public Player getPlayerThatOwnsMe() {
        return playerThatOwnsMe;
    }

    Bowl distribute(int remainingRocks) {
        myRocks++;
        // Skip?
        SmallBowl next = (SmallBowl) getNextBowl();
        if (!getPlayerThatOwnsMe().hasTheTurn()) {
            myRocks--;
            return next.distribute(remainingRocks);
        } // last ?
        else if (remainingRocks == 1)
            return this;
        else
            return next.distribute(--remainingRocks);
    }

    void claimStolenBooty(int booty) {
        myRocks = myRocks + booty;
    }
}
