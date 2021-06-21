package mancala.domain;

abstract class Bowl {
    protected int myRocks;
    protected Player myOwner;
    protected Bowl nextBowl;

    public int getMyRocks() {
        return myRocks;
    }

    public Bowl getNextBowl() {
        return nextBowl;
    }

    public Player getMyOwner() {
        return myOwner;
    }

    abstract void distribute(int remainingRocks);

    abstract SmallBowl getOpposite(int i);

    abstract SmallBowl getNextSmallBowlTimes(int i);

    abstract Kalaha getKalaha();

    abstract SmallBowl getSmallBowl();

    // abstract SmallBowl getNextSmallBowl();

    void endTheGame() {
        getNextBowl().endTheGame(this, 0, 0);
    }

    abstract boolean isEmpty();

    private void endTheGame(Bowl startOfLoop, int scorePlayer, int scoreOpponent) {
        if (isEmpty() == false && myOwner.hasTheTurn()) return;

        if (getMyOwner().equals(startOfLoop.getMyOwner())) {
            scorePlayer = scorePlayer + getMyRocks();
        } else scoreOpponent = scoreOpponent + getMyRocks();

        if (this.equals(startOfLoop)) {

            int playerKalaha = getKalaha().getMyRocks();

            if (scorePlayer == playerKalaha) {

                if (scorePlayer == scoreOpponent) getMyOwner().gotADraw();
                else if (scorePlayer > scoreOpponent) getMyOwner().isTheWinner();
                else getMyOwner().getOpponent().isTheWinner();

            }


        } else getNextBowl().endTheGame(startOfLoop, scorePlayer, scoreOpponent);
    }

}
