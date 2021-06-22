package mancala.domain;

class Kalaha extends Bowl {
    Kalaha(int boardSize, int bowlsToAdd, Bowl startBowl, Player playerOwningThisSide) {
        bowlsToAdd = bowlsToAdd - 1;

        this.myRocks = 0;
        this.myOwner = playerOwningThisSide;

        if (bowlsToAdd == 0) this.nextBowl = startBowl;

        else this.nextBowl = new SmallBowl(boardSize, bowlsToAdd, startBowl, playerOwningThisSide.getOpponent());
    }

    Kalaha getKalaha() {
        return this;
    }

    SmallBowl getSmallBowl() {
        return getNextBowl().getSmallBowl();
    }

    @Override
    SmallBowl goToFirstBowlOfPlayerWithTurn() {
        if (getMyOwner().hasTheTurn()) {
            return getNextBowl().getKalaha().getSmallBowl();
        } else {
            return getSmallBowl();
        }
    }

    SmallBowl getOpposite(int countTillThis) {
        return getNextBowl().getNextSmallBowlTimes(countTillThis - 1);
    }

    SmallBowl getNextSmallBowlTimes(int i) {
        return getNextBowl().getNextSmallBowlTimes(i);
    }

    void distribute(int remainingRocks) {
        myRocks++;
        // Skip?
        if (getMyOwner().hasTheTurn() == false) {
            myRocks--;
            getNextBowl().distribute(remainingRocks);
        } else if (remainingRocks == 1) {
            endTheGame();
        } else getNextBowl().distribute(--remainingRocks);
    }

    @Override
    boolean isEmpty() {
        return true;
    }


    void claimStolenBooty(int booty) {
        myRocks = myRocks + booty;
    }
}
