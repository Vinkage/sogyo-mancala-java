package mancala.domain;

import java.util.List;

class Kalaha extends Bowl {
    Kalaha(int boardSize, int bowlsToAdd, List<Integer> stonesList, Bowl startBowl, Player playerOwningThisSide) {
        bowlsToAdd = bowlsToAdd - 1;

        this.myStones = stonesList.remove(0);
        this.myOwner = playerOwningThisSide;

        if (bowlsToAdd == 0) this.nextBowl = startBowl;

        else this.nextBowl = new SmallBowl(boardSize, bowlsToAdd, stonesList, startBowl, playerOwningThisSide.getOpponent());
    }

    Kalaha getKalaha() {
        return this;
    }

    SmallBowl getNextSmallBowl() {
        return getNextBowl().getNextSmallBowl();
    }

    @Override
    SmallBowl goToFirstBowlOfPlayerWithTurn() {
        if (getMyOwner().hasTheTurn()) {
            return getNextBowl().getKalaha().getNextSmallBowl();
        } else {
            return getNextSmallBowl();
        }
    }

    SmallBowl getOpposite(int countTillThis) {
        return getNextBowl().getNextSmallBowlTimes(countTillThis - 1);
    }

    SmallBowl getNextSmallBowlTimes(int i) {
        return getNextBowl().getNextSmallBowlTimes(i);
    }

    void distribute(int remainingRocks) {
        myStones++;
        // Skip?
        if (getMyOwner().hasTheTurn() == false) {
            myStones--;
            getNextBowl().distribute(remainingRocks);
        } else if (remainingRocks == 1) {
            endTheGame();
        } else getNextBowl().distribute(--remainingRocks);
    }

    @Override
    boolean isEmpty() {
        return true;
    }

    @Override
    protected String makeString(String playerBowls, String opponentBowls, String kalahas) {
        if (getMyOwner().equals(SmallBowl.referencePoint.getMyOwner().getOpponent())) {
            return "  " + opponentBowls + "\n" +
                    getMyStones() + "\t\t\t\t   " + kalahas + "\n" +
                    "  " + playerBowls;
        }
        else {
            return getNextBowl().makeString(
                    playerBowls,
                    opponentBowls,
                    kalahas + getMyStones());
        }
    }

    void claimStolenBooty(int booty) {
        myStones = myStones + booty;
    }

    @Override
    protected int[] toStateArray(int[] stateArray, int index) {
        stateArray[index] = getMyStones();
        if (index == stateArray.length - 2) {
            stateArray[stateArray.length - 1] = (getMyOwner().hasTheTurn() ? Mancala.PLAYER_TWO : Mancala.PLAYER_ONE);
            return stateArray;
        } else {
            return getNextBowl().toStateArray(stateArray, ++index);
        }
    }

}
