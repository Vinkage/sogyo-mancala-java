package mancala.domain;

import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MancalaImpl implements Mancala {

    public static final HashSet<Integer> PLAYER_ONE_PITS = new HashSet<>(
            IntStream.rangeClosed(0, 5).boxed().collect(Collectors.toList())
    );
    public static final HashSet<Integer> PLAYER_TWO_PITS = new HashSet<>(
            IntStream.rangeClosed(7, 12).boxed().collect(Collectors.toList())
    );
    public static final int PLAYER_ONE_KALAHA = 6;
    public static final int PLAYER_TWO_KALAHA = 13;

    private SmallBowl domainReference;

    public MancalaImpl() {
        try {
            domainReference = new SmallBowl();
        } catch (DomainSmallBowlException e) {
            e.printStackTrace();
        }
        domainPlayer = domainReference.getMyOwner();
        domainOpponent = domainPlayer.getOpponent();
    }

    @Override
    public boolean isPlayersTurn(int player) {
        switch (player) {
            case Mancala.PLAYER_ONE:
                return domainReference.getMyOwner().hasTheTurn();
            case Mancala.PLAYER_TWO:
                return domainReference.getMyOwner().getOpponent().hasTheTurn();
            default:
                return false;
        }
    }

    @Override
	public void playPit(int index) throws MancalaException {
        if (isPlayersTurn(Mancala.PLAYER_ONE) && MancalaImpl.PLAYER_TWO_PITS.contains(index)) {
            throw new MancalaException("Player one cannot play player two's pits.");
        }
        if (isPlayersTurn(Mancala.PLAYER_TWO) && MancalaImpl.PLAYER_ONE_PITS.contains(index)) {
            throw new MancalaException("Player two cannot play player one's pits.");
        }
        if (index == MancalaImpl.PLAYER_ONE_KALAHA || index == MancalaImpl.PLAYER_TWO_KALAHA) {
            throw new MancalaException("A kalaha can never be played!");
        }
        if (getStonesForPit(index) == 0) {
            throw new MancalaException("The pit was empty when played!");
        }

        if (isPlayersTurn(Mancala.PLAYER_ONE)) {
            domainReference.getNextSmallBowlTimes(index).play();
        } else {
            int skipKalahaIndex = index - 1;
            domainReference.getNextSmallBowlTimes(skipKalahaIndex).play();
        }

    }
	
	@Override
	public int getStonesForPit(int index) {
        if (MancalaImpl.PLAYER_ONE_PITS.contains(index)) {
            return domainReference.getNextSmallBowlTimes(index).getMyStones();
        }
        else if (MancalaImpl.PLAYER_TWO_PITS.contains(index)) {
            return domainReference.getNextSmallBowlTimes(--index).getMyStones();
        }
        else if (index == MancalaImpl.PLAYER_ONE_KALAHA) {
            return domainReference.getKalaha().getMyStones();
        }
        else if (index == MancalaImpl.PLAYER_TWO_KALAHA) {
            return domainReference.getKalaha().getNextBowl().getKalaha().getMyStones();
        }
        else
            return -1;
    }

    private final mancala.domain.Player domainPlayer;
    private final mancala.domain.Player domainOpponent;

    @Override
	public boolean isEndOfGame() {
        return domainPlayer.won() || domainOpponent.won();
    }

	@Override
	public int getWinner() {
        if (!isEndOfGame()) return Mancala.NO_PLAYERS;

        if (domainPlayer.won()) return Mancala.PLAYER_ONE;
        else if (domainOpponent.won()) return Mancala.PLAYER_TWO;
        else if (domainPlayer.won() && domainOpponent.won()) return Mancala.BOTH_PLAYERS;
        else return Mancala.NO_PLAYERS;
    }

}