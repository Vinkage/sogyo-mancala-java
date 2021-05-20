package mancala.domain;

public class Kalaha extends Bowl {

    public Kalaha() {
        this.myRocks = 0;
        this.playerThatOwnsMe = new Player();
        this.nextBowl = new SmallBowl(14, 1, this, this.playerThatOwnsMe.getOpponent());
    }

    Kalaha(int startPosition, int addedBowlsCount, Bowl startBowl, Player playerOwningThisSide) {
        this.myRocks = 0;
        this.playerThatOwnsMe = playerOwningThisSide;

        int boardPosition = calculateBoardPosition(startPosition, addedBowlsCount);

        try {
            if (!(boardPosition == 7 || boardPosition == 14))
                throw new Exception("Kalaha in wrong position");
            else if (addedBowlsCount == 13)
                this.nextBowl = startBowl;
            else {
                this.nextBowl = new SmallBowl(startPosition, ++addedBowlsCount, startBowl, playerOwningThisSide.getOpponent());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
