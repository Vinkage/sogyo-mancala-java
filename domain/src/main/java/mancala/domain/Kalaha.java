package mancala.domain;

import java.beans.Expression;

public class Kalaha implements Bowl {
    private int myRocks;
    private final Player playerThatOwnsMe;
    private final Bowl nextBowl;

    public Kalaha() {
        this.myRocks = 0;
        this.playerThatOwnsMe = new Player();
        this.nextBowl = new SmallBowl(14, 1, this, this.playerThatOwnsMe.getOpponent());
    }

    Kalaha(int startPosition, int addedBowlsCount, Bowl startBowl, Player playerOwningThisSide) {
        this.myRocks = 0;
        this.playerThatOwnsMe = playerOwningThisSide;

        int boardPosition = Bowl.calculateBoardPosition(startPosition, addedBowlsCount);

        if (!(boardPosition == 7 || boardPosition == 14)) {
            this.nextBowl = null;
        } else if (addedBowlsCount == 13)
            this.nextBowl = startBowl;
        else {
            this.nextBowl = new SmallBowl(startPosition, ++addedBowlsCount, startBowl, playerOwningThisSide.getOpponent());
        }


    }

    public void acceptBooty(int booty) {
        this.myRocks = this.myRocks + booty;
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
    public Bowl takeOneAndContinue(int remainingRocks) {
        this.myRocks++;
        if (remainingRocks == 1)
            return SmallBowl.Recursive.distributeAntiClockWise(--remainingRocks, this);
        else
            return SmallBowl.Recursive.distributeAntiClockWise(--remainingRocks, this.getNextBowl());
    }
}
