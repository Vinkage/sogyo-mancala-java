package mancala.domain;

// Make your own mancala implementation using your design.
// You can take this stub as an example how to make a
// class inside a package and how to test it.
public class SmallBowl implements Bowl {
    private int myRocks;
    private final Player playerThatOwnsMe;
    private final Bowl nextBowl;

    public SmallBowl() {
        this(1);
    }

    public SmallBowl(int position) {
        // constructs board by recursively calling either kalaha or smallbowl constructors
        this.myRocks = 4;
        this.playerThatOwnsMe = new Player();
        if ((0 < position&&position < 6) || (7 < position && position < 13))
            this.nextBowl = new SmallBowl(position, 1, this, this.playerThatOwnsMe);
        else if (position == 6 || position == 13)
            this.nextBowl = new Kalaha(position, 1, this, this.playerThatOwnsMe);
        // Special cases where smallbowl is on "wrong position"
        else if (position == 7) {
            this.nextBowl = new SmallBowl(1, 1, this, this.playerThatOwnsMe);
        } else if (position == 14) {
            this.nextBowl = new SmallBowl(8, 1, this, this.playerThatOwnsMe);
        } else {
            this.nextBowl = null;
        }
    }

    public void play() {
        if ((!this.playerThatOwnsMe.hasTheTurn()) || (this.myRocks == 0));
        else {

            Bowl playEndedInThisBowl = new Recursive().flipFlopDistributeRock(this.myRocks, this.getNextBowl());
            this.myRocks = 0;

            if (!(playEndedInThisBowl.getClass() == Kalaha.class)) {
                this.playerThatOwnsMe.switchTurn();
            }

            stealTheBooty(playEndedInThisBowl);

            endTheGame();
        }
    }

    public SmallBowl getOpposite() {
        Bowl kalaha = this.getNextBowl();
        Recursive recursive = new Recursive();
        int i = 0;
        while (kalaha.getClass() != Kalaha.class) {
            i++;
            kalaha = kalaha.getNextBowl();
        }
        return (SmallBowl) recursive.getNextSmallBowl(i, (SmallBowl) kalaha.getNextBowl());
    }

    public Kalaha getKalaha() {
        Bowl kalaha = this.getNextBowl();
        while (kalaha.getClass() != Kalaha.class)
            kalaha = kalaha.getNextBowl();
        return (Kalaha) kalaha;
    }

    @Override
    public int getMyRocks() {
        return this.myRocks;
    }

    @Override
    public Bowl getNextBowl() {
        return nextBowl;
    }

    public SmallBowl getNextSmallBowlRepeat(int i) {
        Recursive recursive = new Recursive();
        return recursive.getNextSmallBowl(i, this);
    }

    @Override
    public Player getPlayerThatOwnsMe() {
        return playerThatOwnsMe;
    }

    // Recurses through board positions until connected again to startBowl
    SmallBowl(int startPosition, int addedBowlsCount, Bowl startBowl, Player playerOwningThisSide) {
        this.myRocks = 4;
        this.playerThatOwnsMe = playerOwningThisSide;

        int boardPosition = Bowl.calculateBoardPosition(startPosition, addedBowlsCount);

        if (addedBowlsCount == 13)
            this.nextBowl = startBowl;

        else if ((0 < boardPosition && boardPosition < 6) || (7 < boardPosition && boardPosition < 13))
            this.nextBowl = new SmallBowl(startPosition, ++addedBowlsCount, startBowl, playerOwningThisSide);

        else if (boardPosition == 6 || boardPosition == 13)
            this.nextBowl = new Kalaha(startPosition, ++addedBowlsCount, startBowl, playerOwningThisSide);

        else
            this.nextBowl = null;
    }

    class Recursive {
        Bowl flipFlopDistributeRock(int remainingRocks, Bowl currentBowl) {
            if (remainingRocks == 0)
                return currentBowl;
            else if (currentBowl.getClass() == Kalaha.class) {
                Kalaha tmpKalaha = (Kalaha) currentBowl;
                return tmpKalaha.new Recursive().flipFlopDistributeRock(remainingRocks, tmpKalaha);
            } else {
                SmallBowl tmpSmallBowl = (SmallBowl) currentBowl;
                return tmpSmallBowl.takeOneAndContinue(remainingRocks);
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

    private Bowl takeOneAndContinue(int remainingRocks) {
        this.myRocks++;
        if (remainingRocks == 1)
            return new Recursive().flipFlopDistributeRock(--remainingRocks, this);
        else
            return new Recursive().flipFlopDistributeRock(--remainingRocks, this.getNextBowl());
    }

    private void stealTheBooty(Bowl playEndedInThisBowl) {
        if (playEndedInThisBowl.getClass() == SmallBowl.class&&playEndedInThisBowl.getPlayerThatOwnsMe().equals(this.getPlayerThatOwnsMe())) {

            SmallBowl playEndSmallBowl = (SmallBowl) playEndedInThisBowl;
            SmallBowl opposite = playEndSmallBowl.getOpposite();
            int booty;
            if (playEndSmallBowl.getMyRocks() == 1&&opposite.getMyRocks() != 0) {
                booty = opposite.getMyRocks();
                booty++;
                playEndSmallBowl.myRocks = 0;
                opposite.myRocks = 0;
                this.getKalaha().acceptBooty(booty);
            }
        }
    }

    private void endTheGame() {
        int playerRocks = 0;
        int opponentRocks = 0;
        SmallBowl current = this;
        for (int i = 0; i < 14; i++) {
            if (current.getPlayerThatOwnsMe().equals(this.getPlayerThatOwnsMe()))
                playerRocks = playerRocks + current.getMyRocks();
            else
                opponentRocks = opponentRocks + current.getMyRocks();
        }

        if (playerRocks == this.getKalaha().getMyRocks()) {
            if (playerRocks > opponentRocks) {
                this.getPlayerThatOwnsMe().isTheWinner();
            } else
                this.getPlayerThatOwnsMe().getOpponent().isTheWinner();
        }
    }
}