package mancala.domain;

abstract class Bowl {
    protected int myStones;
    protected Player myOwner;
    protected Bowl nextBowl;

    public int getMyStones() {
        return myStones;
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

    abstract SmallBowl getNextSmallBowl();

    abstract SmallBowl goToFirstBowlOfPlayerWithTurn();

    abstract boolean isEmpty();
    // abstract SmallBowl getNextSmallBowl();

    void endTheGame() {
        goToFirstBowlOfPlayerWithTurn().getNextBowl().endTheGame(goToFirstBowlOfPlayerWithTurn(), 0, 0);
    }

    protected void endTheGame(Bowl startOfLoop, int scorePlayer, int scoreOpponent) {
        if (isEmpty() == false && getMyOwner().equals(startOfLoop.getMyOwner())) return;

        if (this.equals(startOfLoop)) {

            int playerKalaha = getKalaha().getMyStones();

            if (scorePlayer == playerKalaha) {

                if (scorePlayer == scoreOpponent) getMyOwner().gotADraw();
                else if (scorePlayer > scoreOpponent) getMyOwner().isTheWinner();
                else getMyOwner().getOpponent().isTheWinner();
            }


        } else {
            if (getMyOwner().equals(startOfLoop.getMyOwner())) {
                scorePlayer = scorePlayer + getMyStones();
            } else scoreOpponent = scoreOpponent + getMyStones();

            getNextBowl().endTheGame(startOfLoop, scorePlayer, scoreOpponent);
        }
    }


    protected abstract String makeString(String playerBowls, String opponentBowls, String kalahas);
}
