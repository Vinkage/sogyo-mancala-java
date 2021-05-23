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
        return this.myRocks;
    }

    @Override
    public Bowl getNextBowl() {
        return nextBowl;
    }

    @Override
    public Player getPlayerThatOwnsMe() {
        return playerThatOwnsMe;
    }

    @Override
    public Bowl distribute(int remainingRocks) {
        this.myRocks++;
        // last?
        if (remainingRocks == 1)
            return this;
        else
            return this.getNextBowl().distribute(--remainingRocks);
    }

    public void play() {
        if ((!this.playerThatOwnsMe.hasTheTurn()) || (this.myRocks == 0));
        else {

            Bowl lastToReceiveRock = getNextBowl().distribute(myRocks);
            myRocks = 0;

            if (lastToReceiveRock.getClass() == SmallBowl.class && lastToReceiveRock.getPlayerThatOwnsMe().equals(getPlayerThatOwnsMe())) {
                stealTheBooty(lastToReceiveRock);
            }

            if (!(lastToReceiveRock.getClass() == Kalaha.class)) {
                this.playerThatOwnsMe.switchTurn();
            }

            endTheGame();

        }
    }

    SmallBowl getOpposite() {
        SmallBowl opponentFirst = (SmallBowl) getKalaha().getNextBowl();
        return opponentFirst.getNextSmallBowlTimes(countSmallBowlsUntilKalahaFromHere());
    }


    Kalaha getKalaha() {
        if (!(getNextBowl().getClass() == Kalaha.class)) {
            SmallBowl notKalaha = (SmallBowl) getNextBowl();
            return notKalaha.getKalaha();
        } else
            return (Kalaha) getNextBowl();
    }

    public SmallBowl getNextSmallBowlTimes(int remainingNexts) {
        if (remainingNexts == 0)
            return this;
        else {
            SmallBowl next = getNextSmallBowl();
            return next.getNextSmallBowlTimes(--remainingNexts);
        }
    }

    private void stealTheBooty(Bowl playEndedInThisBowl) {
            SmallBowl playEndSmallBowl = (SmallBowl) playEndedInThisBowl;
            SmallBowl opposite = playEndSmallBowl.getOpposite();
            int booty;
            if (playEndSmallBowl.getMyRocks() == 1&&opposite.getMyRocks() != 0) {
                booty = opposite.getMyRocks();
                booty++;
                playEndSmallBowl.myRocks = 0;
                opposite.myRocks = 0;
                getKalaha().acceptBooty(booty);
            }
    }

    private void endTheGame() {
        SmallBowl firstBowlPlayer = (SmallBowl) getOpposite().getKalaha().getNextBowl();
        int playerRocks = firstBowlPlayer.countRocksTillDifferentPlayer();

        SmallBowl firstBowlOpponent = (SmallBowl) getKalaha().getNextBowl();
        int opponentRocks = firstBowlOpponent.countRocksTillDifferentPlayer();

        int playerKalaha = getKalaha().getMyRocks();
        int opponentKalaha = getOpposite().getKalaha().getMyRocks();

        if (playerRocks == 0) {
            if ((playerRocks + playerKalaha) == (opponentRocks + opponentKalaha))
                getPlayerThatOwnsMe().gotADraw();
            else if ((playerRocks + playerKalaha) > (opponentRocks + opponentKalaha))
                getPlayerThatOwnsMe().isTheWinner();
            else
                getPlayerThatOwnsMe().getOpponent().isTheWinner();
        }
    }

    private int countRocksTillDifferentPlayer() {
        if (getNextSmallBowl().getPlayerThatOwnsMe().equals(getPlayerThatOwnsMe()))
            return this.myRocks + getNextSmallBowl().countRocksTillDifferentPlayer();
        else
            return this.myRocks;
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
}