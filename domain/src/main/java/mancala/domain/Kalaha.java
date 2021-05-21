package mancala.domain;

class Kalaha implements Bowl {
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

    private Bowl takeOneAndContinue(int remainingRocks) {
        this.myRocks++;
        if (remainingRocks == 1)
            return new Recursive().flipFlopDistributeRock(--remainingRocks, this);
        else {
            SmallBowl smallBowl = (SmallBowl) this.getNextBowl();
            return smallBowl.new Recursive().flipFlopDistributeRock(--remainingRocks, smallBowl);
        }
    }

    class Recursive {
        Bowl flipFlopDistributeRock(int remainingRocks, Kalaha currentBowl) {
            if (remainingRocks == 0)
                return currentBowl;
            else if (!(Kalaha.this.getPlayerThatOwnsMe().hasTheTurn())) {
                SmallBowl smallBowl = (SmallBowl) currentBowl.getNextBowl();
                return smallBowl.new Recursive().flipFlopDistributeRock(remainingRocks, smallBowl);
            } else {
                return takeOneAndContinue(remainingRocks);
            }

        }
    }

    void acceptBooty(int booty) {
        this.myRocks = this.myRocks + booty;
    }
}
