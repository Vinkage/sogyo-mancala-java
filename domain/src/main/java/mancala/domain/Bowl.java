package mancala.domain;

interface Bowl {

    int getMyRocks();

    Bowl getNextBowl();

    Player getPlayerThatOwnsMe();

    Bowl takeOneAndContinue(int remainingRocks);

    static int calculateBoardPosition(int startPosition, int addedBowlsCount) {
        if ((startPosition + addedBowlsCount) > 14) {
            return ((addedBowlsCount + startPosition) - 14);
        }
        return startPosition + addedBowlsCount;
    }

}
