package mancala.domain;

abstract class Bowl {
    protected int myRocks;
    protected Player playerThatOwnsMe;
    protected Bowl nextBowl;

    protected int calculateBoardPosition(int startPosition, int addedBowlsCount) {
        if ((startPosition + addedBowlsCount) > 14) {
            return ((addedBowlsCount + startPosition) - 14);
        }
        return startPosition + addedBowlsCount;
    }

    protected void printStateDuringConstruction(int startPosition, int addedBowlsCount) {
        System.out.println("Board position: " + calculateBoardPosition(startPosition, addedBowlsCount)+ " type of bowl: " + this.getClass().toString() + ", Player pointer:" + this.playerThatOwnsMe);
    }

    Bowl getNextBowl() {
        return nextBowl;
    }
    Player getPlayerThatOwnsMe() {
        return playerThatOwnsMe;
    }

    int getMyRocks() {
        return myRocks;
    }
}
