package mancala.domain;

// Make your own mancala implementation using your design.
// You can take this stub as an example how to make a 
// class inside a package and how to test it.
public class SmallBowl extends Bowl {

    public SmallBowl() {
        constructorHelper(1);
    }

    public SmallBowl(int position) {
        // constructs board by recursively calling either kalaha or smallbowl constructors
        constructorHelper(position);
    }

    SmallBowl getNextSmallBowlRepeat(int i) {
        Recursive recursive = new Recursive();
        SmallBowl next = recursive.getNextSmallBowl(i, this);
        return next;
    }

    SmallBowl getOpposite() {
        Bowl kalaha = this.getNextBowl();
        Recursive recursive = new Recursive();
        int i = 0;
        while (kalaha.getClass() != Kalaha.class) {
            i++;
            kalaha.getNextBowl();
        }
        return (SmallBowl) recursive.getNextSmallBowl(i-1, (SmallBowl) kalaha.getNextBowl());
    }

    Kalaha getKalaha() {
        Bowl kalaha = this.getNextBowl();
        while (kalaha.getClass() != Kalaha.class)
            kalaha = kalaha.getNextBowl();
        return (Kalaha) kalaha;
    }

    public void play() {
        if ((!this.playerThatOwnsMe.hasTheTurn()) || (this.myRocks == 0));
        else {
            Recursive recursive = new Recursive();
            Bowl playEndedInThisBowl = recursive.distributeAntiClockWise(this.myRocks, this.getNextBowl());
            this.myRocks = 0;
            if (!(playEndedInThisBowl.getClass() == Kalaha.class&&playEndedInThisBowl.getPlayerThatOwnsMe().equals(this.getPlayerThatOwnsMe()))) {
                this.playerThatOwnsMe.switchTurn();
            }
        }
    }

    private class Recursive {
        Bowl distributeAntiClockWise(int remainingRocks, Bowl currentBowl) {
            Boolean opponentKalahaCondition = (currentBowl.getClass() == Kalaha.class)&&!(currentBowl.getPlayerThatOwnsMe().equals(SmallBowl.this.getPlayerThatOwnsMe()));
            if (remainingRocks > 1&&!opponentKalahaCondition) {
                currentBowl.myRocks++;
                return distributeAntiClockWise(--remainingRocks, currentBowl.getNextBowl());
            } else if (opponentKalahaCondition) {
                return distributeAntiClockWise(remainingRocks, currentBowl.getNextBowl());
            } else {
                currentBowl.myRocks++;
                return currentBowl;
            }
        }

        SmallBowl getNextSmallBowl(int remainingNexts, Bowl currentBowl) {
            if (remainingNexts > 0)
                return getNextSmallBowl(--remainingNexts, currentBowl.getNextBowl());
            else if (currentBowl.getClass() == Kalaha.class)
                return getNextSmallBowl(remainingNexts, currentBowl.getNextBowl());
            else
                return (SmallBowl) currentBowl;

        }
    }

    // Recurses through board positions until connected again to startBowl
    SmallBowl(int startPosition, int addedBowlsCount, Bowl startBowl, Player playerOwningThisSide) {
        this.myRocks = 4;
        this.playerThatOwnsMe = playerOwningThisSide;

        int boardPosition = calculateBoardPosition(startPosition, addedBowlsCount);

        if (addedBowlsCount == 13)
            this.nextBowl = startBowl;

        else if (boardPosition < 6 || (7 < boardPosition && boardPosition < 13))
            this.nextBowl = new SmallBowl(startPosition, ++addedBowlsCount, startBowl, playerOwningThisSide);

        else if (boardPosition == 6 || boardPosition == 13)
            this.nextBowl = new Kalaha(startPosition, ++addedBowlsCount, startBowl, playerOwningThisSide);
    }

    private void constructorHelper(int position) {
        this.myRocks = 4;
        this.playerThatOwnsMe = new Player();

        if (position < 6 || (7 < position && position < 13))
            this.nextBowl = new SmallBowl(position, 1, this, this.playerThatOwnsMe);
        else if (position == 6 || position == 13)
            this.nextBowl = new Kalaha(position, 1, this, this.playerThatOwnsMe);
        else if (position == 7) {
            this.nextBowl = new SmallBowl(1, 1, this, this.playerThatOwnsMe);
        } else if (position == 14) {
            this.nextBowl = new SmallBowl(8, 1, this, this.playerThatOwnsMe);
        }
    }


}