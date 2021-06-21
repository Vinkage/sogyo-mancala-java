package mancala.domain;

public class SmallBowl extends Bowl {

    public SmallBowl() {
        this.myRocks = 4;
        this.myOwner = new Player();

        int boardSize = 14;
        int bowlsToAdd = boardSize - 1;

        this.nextBowl = new SmallBowl(boardSize, bowlsToAdd, this, this.getMyOwner());
    }

    public SmallBowl(int boardSize) {
        try {
            if (boardSize < 4) {
                throw new Exception("Can't have a board smaller than four bowls.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.myRocks = 4;
        this.myOwner = new Player();
        int bowlsToAdd = boardSize - 1;
        this.nextBowl = new SmallBowl(boardSize, bowlsToAdd, this, this.getMyOwner());
    }

    SmallBowl(int boardSize, int bowlsToAdd, Bowl startBowl, Player playerOwningThisSide) {
        bowlsToAdd = bowlsToAdd - 1;
        this.myOwner = playerOwningThisSide;
        this.myRocks = 4;

        if (bowlsToAdd == 0) nextBowl = startBowl;

        else if (bowlsToAdd == (boardSize / 2) + 1) nextBowl = new Kalaha(boardSize, bowlsToAdd, startBowl, playerOwningThisSide);

        else if (bowlsToAdd == 1) nextBowl = new Kalaha(boardSize, bowlsToAdd, startBowl, playerOwningThisSide);

        else nextBowl = new SmallBowl(boardSize, bowlsToAdd, startBowl, playerOwningThisSide);
    }

    public SmallBowl getNextSmallBowlTimes(int remainingTimes) {
        if (remainingTimes == 0)
            return this;
        else {
            return getNextBowl().getNextSmallBowlTimes(--remainingTimes);
        }
    }

    public void play() {
        if (myOwner.hasTheTurn() == false) return;
        if (isEmpty()) return;

        int passThese = myRocks;
        myRocks = 0;
        getNextBowl().distribute(passThese);
    }

    @Override
    boolean isEmpty() {
        return this.myRocks == 0;
    }

    void distribute(int remainingRocks) {
        this.myRocks++;
        // last?
        if (remainingRocks == 1)
            lastSmallBowl();
        else {
            getNextBowl().distribute(--remainingRocks);
        }
    }

    private void lastSmallBowl() {
        // Did play end in smallbowl of my player? steal, otherwise do nothing
        if (getMyOwner().hasTheTurn()) stealTheBooty(false);

        getMyOwner().switchTurn();

        endTheGame();
    }

    SmallBowl getSmallBowl() {
        return this;
    }

    Kalaha getKalaha() {
        return getNextBowl().getKalaha();
    }

    private void stealTheBooty(boolean victim) {
        if (victim){
            getOpposite().getKalaha().claimStolenBooty(myRocks);
            myRocks = 0;

        } else if (getMyRocks() == 1 &&
                getOpposite().getMyRocks() != 0) {

            getKalaha().claimStolenBooty(myRocks);
            myRocks = 0;
            getOpposite().stealTheBooty(true);
        }
    }

    SmallBowl getOpposite() {
        return getOpposite(0);
    }

    SmallBowl getOpposite(int count) {
        count = count + 1;
        return getNextBowl().getOpposite(count);
    }
}
