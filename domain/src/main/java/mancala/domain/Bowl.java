package mancala.domain;

interface Bowl {

    int getMyRocks();

    Bowl getNextBowl();

    Player getMyOwner();
}
