package mancala.domain;

// Make your own mancala implementation using your design.
// You can take this stub as an example how to make a
// class inside a package and how to test it.
public class SmallBowl implements Bowl {
    private int myRocks;
    private final Player playerThatOwnsMe;
    private final Bowl nextBowl;

    public SmallBowl() {
        this.myRocks = 4;

        this.playerThatOwnsMe = new Player();
        int boardSize = 14;

        this.nextBowl = new SmallBowl(boardSize, --boardSize, this, this.getPlayerThatOwnsMe());
    }

    SmallBowl(int boardSize, int remainingBowls, Bowl startBowl, Player playerOwningThisSide) {
        this.myRocks = 4;
        this.playerThatOwnsMe = playerOwningThisSide;

        boolean startingFromKalaha = startBowl.getClass() == Kalaha.class;
        int startingFromKalahaAdjustment = 0;
        if (startingFromKalaha)
            startingFromKalahaAdjustment = -1;

        if (remainingBowls == 1) {
            this.nextBowl = startBowl;
        } else if (remainingBowls == boardSize / 2 + 2 + startingFromKalahaAdjustment || (remainingBowls == 2 && !startingFromKalaha)) {
            this.nextBowl = new Kalaha(boardSize, --remainingBowls, startBowl, playerOwningThisSide);
        } else {
            this.nextBowl = new SmallBowl(boardSize, --remainingBowls, startBowl, playerOwningThisSide);
        }
    }

    @Override
    public int getMyRocks() {
        return myRocks;
    }

    @Override
    public Bowl getNextBowl() {
        return nextBowl;
    }

    @Override
    public Player getPlayerThatOwnsMe() {
        return playerThatOwnsMe;
    }

    public SmallBowl getNextSmallBowlTimes(int remainingTimes) {
        if (remainingTimes == 0)
            return this;
        else {
            return getNextSmallBowl().getNextSmallBowlTimes(--remainingTimes);
        }
    }

    public void play() {
        if ((!playerThatOwnsMe.hasTheTurn()) || (myRocks == 0));
        else {
            // Distribute rocks one by one
            Bowl lastToReceiveRock;
            if (getNextBowl().getClass() == Kalaha.class)
                lastToReceiveRock = getKalaha().distribute(myRocks);
            else
                lastToReceiveRock = getNextSmallBowl().distribute(myRocks);
            myRocks = 0;

            // Did play end in smallbowl of my player?
            if (lastToReceiveRock.getClass() == SmallBowl.class && lastToReceiveRock.getPlayerThatOwnsMe().equals(getPlayerThatOwnsMe())) {
                stealTheBooty((SmallBowl) lastToReceiveRock);
            }

            // Did play end in Kalaha? otherwise switch turn
            if (!(lastToReceiveRock.getClass() == Kalaha.class)) {
                getPlayerThatOwnsMe().switchTurn();
            }

            // Should a player when? otherwise do nothing
            endTheGame();
        }
    }

    Bowl distribute(int remainingRocks) {
        this.myRocks++;
        // last?
        if (remainingRocks == 1)
            return this;
        else {
            if (getNextBowl().getClass() == SmallBowl.class)
                return getNextSmallBowl().distribute(--remainingRocks);
            else
                return getKalaha().distribute(--remainingRocks);
        }
    }

    private void stealTheBooty(SmallBowl thievingBowl) {
            SmallBowl victim = thievingBowl.getOpposite();
            int booty;
            if (thievingBowl.getMyRocks() == 1 && victim.getMyRocks() != 0) {
                booty = victim.getMyRocks();
                booty++;
                thievingBowl.myRocks = 0;
                victim.myRocks = 0;
                getKalaha().claimStolenBooty(booty);
            }
    }

    private void endTheGame() {
        SmallBowl firstBowlPlayer = (SmallBowl) getOpposite().getKalaha().getNextBowl();
        int playerRocks = firstBowlPlayer.countRocksTillDifferentPlayer();

        if (playerRocks == 0) {

            SmallBowl firstBowlOpponent = (SmallBowl) getKalaha().getNextBowl();
            int opponentRocks = firstBowlOpponent.countRocksTillDifferentPlayer();

            int playerKalaha = getKalaha().getMyRocks();
            int opponentKalaha = getOpposite().getKalaha().getMyRocks();

            if ((playerRocks + playerKalaha) == (opponentRocks + opponentKalaha))
                getPlayerThatOwnsMe().gotADraw();
            else if ((playerRocks + playerKalaha) > (opponentRocks + opponentKalaha))
                getPlayerThatOwnsMe().isTheWinner();
            else
                getPlayerThatOwnsMe().getOpponent().isTheWinner();
        }
    }

    private int countRocksTillDifferentPlayer() {
        if (!(getNextSmallBowl().getPlayerThatOwnsMe().equals(getPlayerThatOwnsMe())))
            return this.myRocks;
        else
            return this.myRocks + getNextSmallBowl().countRocksTillDifferentPlayer();
    }

    private SmallBowl getNextSmallBowl() {
        if (getNextBowl().getClass() == Kalaha.class)
            return (SmallBowl) getNextBowl().getNextBowl();
        else
            return (SmallBowl) getNextBowl();
    }

    private int countSmallBowlsUntilKalahaFromHere() {
        if (getNextBowl().getClass() == Kalaha.class)
            return 0;
        else
            return 1 + getNextSmallBowl().countSmallBowlsUntilKalahaFromHere();
    }

    // Deze package private methoden worden nog in de tests gebruikt
    private SmallBowl getOpposite() {
        SmallBowl opponentFirst = (SmallBowl) getKalaha().getNextBowl();
        return opponentFirst.getNextSmallBowlTimes(countSmallBowlsUntilKalahaFromHere());
    }

    private Kalaha getKalaha() {
        if (!(getNextBowl().getClass() == Kalaha.class)) {
            return getNextSmallBowl().getKalaha();
        } else
            return (Kalaha) getNextBowl();
    }
}