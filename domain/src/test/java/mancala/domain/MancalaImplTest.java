package mancala.domain;

import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@IndicativeSentencesGeneration(separator = " -> ", generator = DisplayNameGenerator.ReplaceUnderscores.class)
class MancalaImplTest {

    Mancala mancala;

    @BeforeEach
    void setUp() {
        mancala = new MancalaImpl();
    }

    /**
     * Method indicating if the first player has the next turn or not.
     * If player 1 is not in turn, then player 2 is in turn.
     *
     * @param The player which you want to know the turn for.
     * @return True if the first player has the next turn, false if it's the turn of the other player.
     */
    @Nested
    class isPlayersTurn {
        @Test
        void given_mancala_PLAYER_ONE_when_first_player_has_next_turn_then_return_true() {
            assertTrue(mancala.isPlayersTurn(Mancala.PLAYER_ONE));
        }

        @Test
        void given_mancala_PLAYER_ONE_when_second_player_has_next_turn_then_return_false() {
            playPitAndFailIfNotValid(0);
            assertFalse(mancala.isPlayersTurn(Mancala.PLAYER_ONE));
        }


        @Test
        void given_mancala_PLAYER_TWO_when_second_player_has_next_turn_then_return_true() {
            playPitAndFailIfNotValid(0);
            assertTrue(mancala.isPlayersTurn(Mancala.PLAYER_TWO));
        }

        @Test
        void given_mancala_PLAYER_TWO_when_second_player_has_turn_then_return_false() {
            assertFalse(mancala.isPlayersTurn(Mancala.PLAYER_TWO));
        }
    }

    /**
     * Method for playing the specified recess. Index is as specified below:
     *
     *    12 11 10  9  8  7
     * 13                    6
     *     0  1  2  3  4  5
     *
     * @param index Index of the recess to be played.
     * @return 15 item long Array with the current state of the game. The 15th item indicates which player has the next turn (possible values are 1 or 2).
     */
    @Nested
    class playPit {
        @Test
        void given_a_pit_index_from_PLAYER_one_choice_when_pit_doesnt_belong_to_player_one_then_throw_MancalaException() {
            for (int index: MancalaImpl.PLAYER_TWO_PITS) {
                playPitAndFailIfNoException(index, "Player one could play a pit that was not his without an exception!");
            }
        }

        @Test
        void given_a_pit_index_from_PLAYER_two_choice_when_pit_doesnt_belong_to_player_two_then_throw_MancalaException() {
            assumeTurn(Mancala.PLAYER_ONE);
            playPitAndFailIfNotValid(0);

            assumeTurn(Mancala.PLAYER_TWO);
            for (int index: MancalaImpl.PLAYER_ONE_PITS) {
                playPitAndFailIfNoException(index, "Player two could play a pit that was not his without an exception!");
            }
        }

        @Test
        void given_a_Kalaha_index_when_playPit_is_called_then_always_throw_MancalaException() {
            assumeTurn(Mancala.PLAYER_ONE);
            playPitAndFailIfNoException(MancalaImpl.PLAYER_ONE_KALAHA, "Kalaha of player one was played without throwing exception!");
            playPitAndFailIfNoException(MancalaImpl.PLAYER_TWO_KALAHA, "Kalaha of player two was played without throwing exception!");

            playPitAndFailIfNotValid(0);

            assumeTurn(Mancala.PLAYER_TWO);
            playPitAndFailIfNoException(MancalaImpl.PLAYER_ONE_KALAHA, "Kalaha of player one was played without throwing exception!");
            playPitAndFailIfNoException(MancalaImpl.PLAYER_TWO_KALAHA, "Kalaha of player two was played without throwing exception!");
        }

        @Test
        void given_that_valid_play_is_made_when_play_is_done_then_players_switch_turns() {
            assumeTurn(Mancala.PLAYER_ONE);
            playPitAndFailIfNotValid(0);

            assumeNotTurn(Mancala.PLAYER_ONE);
            assumeTurn(Mancala.PLAYER_TWO);
        }

        @Test
        void given_that_pit_has_no_stones_when_play_is_made_by_player_one_then_throw_MancalaException() {
            assumeTurn(Mancala.PLAYER_ONE);
            playPitAndFailIfNotValid(0);

            assumeTurn(Mancala.PLAYER_TWO);
            playPitAndFailIfNotValid(7);

            assumeTurn(Mancala.PLAYER_ONE);
            playPitAndFailIfNoException(0, "Didn't throw exception when Pit was empty when played!");
        }

        @Test
        void given_that_pit_has_no_stones_when_play_is_made_by_player_two_then_throw_MancalaException() {
            assumeTurn(Mancala.PLAYER_ONE);
            playPitAndFailIfNotValid(5);

            assumeTurn(Mancala.PLAYER_TWO);
            playPitAndFailIfNotValid(7);

            assumeTurn(Mancala.PLAYER_ONE);
            playPitAndFailIfNotValid(0);

            assumeTurn(Mancala.PLAYER_TWO);
            playPitAndFailIfNoException(7, "Didn't throw exception when Pit was empty when played!");
        }

        @Test
        void given_that_pit_has_stones_and_player_has_turn_when_playPit_is_called_then_return_array_representing_state() {
            mancala = new MancalaImpl(new int[] {1,0,0,0,0,0,0,1,0,0,0,0,0,0});
            assumeTurn(Mancala.PLAYER_ONE);
            int[] stateArray = playPitAndFailIfNotValid(0);
            assertEquals(15, stateArray.length,
                    "length of the state array should be 15.");
            assertTrue(Arrays.equals(new int[] {0,1,0,0,0,0,0,1,0,0,0,0,0,0,2}, stateArray));

            stateArray = playPitAndFailIfNotValid(7);
            assertEquals(15, stateArray.length,
                    "length of the state array should be 15.");
            assertTrue(Arrays.equals(new int[] {0,1,0,0,0,0,0,0,1,0,0,0,0,0,1}, stateArray));
        }

    }

    /**
     * Method for returning the amount of stones in de specified pit. Index is as specified below:
     *
     *    12 11 10  9  8  7
     * 13                    6
     *     0  1  2  3  4  5
     *
     * @param index Index of the pit.
     * @return Amount of stone.
     */
    @Nested
    class getStonesForPit {
        @Test
        void given_any_Pit_index_when_getStonesForPit_is_called_then_return_stones_of_pits() {
            HashSet<Integer> allPitIndexes = new HashSet<>(MancalaImpl.PLAYER_ONE_PITS);
            allPitIndexes.addAll(MancalaImpl.PLAYER_TWO_PITS);

            for (int pitIndex: allPitIndexes) {
                assertEquals(4, mancala.getStonesForPit(pitIndex),
                        "pit should have four stones when created in the domain model.");
            }
        }

        @Test
        void given_any_Kalaha_index_when_getStonesForPit_is_called_then_return_stones_of_Kalaha() {
            HashSet<Integer> allKalahaIndexes = new HashSet<>();
            allKalahaIndexes.add(MancalaImpl.PLAYER_ONE_KALAHA);
            allKalahaIndexes.add(MancalaImpl.PLAYER_TWO_KALAHA);
            for (int kalahaIndex: allKalahaIndexes) {
                assertEquals(0, mancala.getStonesForPit(kalahaIndex),
                        "Kalaha should have zero stones when created in the domain model.");
            }
        }

        @Test
        void given_that_a_play_has_been_made_and_not_looped_around_board_when_getting_stones_then_should_give_zero() {
            assumeTurn(Mancala.PLAYER_ONE);
            playPitAndFailIfNotValid(5);

            assertEquals(0, mancala.getStonesForPit(5),
                    "In this situation play should leave zero rocks!");

            assumeTurn(Mancala.PLAYER_TWO);
            playPitAndFailIfNotValid(7);

            assertEquals(0, mancala.getStonesForPit(7),
                    "In this situation play should leave zero rocks!");
        }

    }

    /**
     * Method for retrieving whether the game has ended or not.
     *
     * @return True is the game has ended otherwise False.
     */
    @Nested
    class isEndOfGame {
        @Test
        void given_the_game_has_not_ended_when_isEndOfGame_is_called_then_return_false() {
            assertFalse(mancala.isEndOfGame());
        }

        @Test
        void given_the_game_has_ended_when_isEndOfGame_is_called_then_return_true() {
            mancala = new MancalaImpl(new int[] {0,0,0,0,0,1,0,0,0,0,0,0,0,0});
            playPitAndFailIfNotValid(5);
            assertTrue(mancala.isEndOfGame());
        }


    }

    /**
     * Method for retrieving the player that has won the game.
     *
     * @return Integer value representing which player(s) (if any) won the game.
     */
    @Nested
    class getWinner {
        @Test
        void given_the_game_has_not_ended_when_getWinner_is_called_then_immediately_return_Mancala_NO_PLAYERS() {
            assertEquals(Mancala.NO_PLAYERS, mancala.getWinner());
        }

        @Test
        void given_PLAYER_ONE_has_won_in_the_domain_model_when_getWinner_is_called_then_return_Mancala_PLAYER_ONE() {
            mancala = new MancalaImpl(new int[] {0,0,0,0,0,1,0,0,0,0,0,0,0,0});
            playPitAndFailIfNotValid(5);
            assertEquals(Mancala.PLAYER_ONE, mancala.getWinner());
        }

        @Test
        void given_PLAYER_TWO_has_won_in_the_domain_model_when_getWinner_is_called_then_return_Mancala_PLAYER_TWO() {
            mancala = new MancalaImpl(new int[] {0,1,0,0,0,0,0,0,0,0,0,2,0,0});
            playPitAndFailIfNotValid(1);
            playPitAndFailIfNotValid(7 + 4);
            playPitAndFailIfNotValid(7 + 5);
            assertEquals(Mancala.PLAYER_TWO, mancala.getWinner(),
                    "PLAYER TWO should win here.");
        }

        @Test
        void given_BOTH_PLAYER_have_won_in_the_domain_model_when_getWinner_is_called_then_return_Mancala_BOTH_PLAYERS() {
            mancala = new MancalaImpl(new int[] {0,0,0,0,0,1,0,0,0,0,0,0,1,0});
            playPitAndFailIfNotValid(5);
            assertEquals(Mancala.BOTH_PLAYERS, mancala.getWinner(),
                    "PLAYER TWO should win here.");
        }

    }

    void assumeTurn(int player) {
        assertTrue(mancala.isPlayersTurn(player),
                "It's not PLAYER " + (player == 1 ? "ONE's" : "TWO's") + " turn!");
    }

    void assumeNotTurn(int player) {
        assertFalse(mancala.isPlayersTurn(player),
                "It's PLAYER " + (player == 1 ? "ONE's" : "TWO's") + " turn!");
    }

    int[] playPitAndFailIfNotValid(int index) {
        try {
            return mancala.playPit(index);
        } catch (MancalaException e) {
            fail("Invalid play.");
            return new int[0];
        }
    }

    void playPitAndFailIfNoException(int index, String message) {
        try {
            mancala.playPit(index);
            fail(message);
        } catch (MancalaException e) {
        }
    }

    void notImplementedYet() {
        fail("Not implemented yet.");
    }
}