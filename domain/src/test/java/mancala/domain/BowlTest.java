package mancala.domain;

// Your test class should be in the same 
// package as the class you're testing.
// Usually the test directory mirrors the
// main directory 1:1. So for each class in src/main,
// there is a class in src/test.

// Import our test dependencies. We import the Test-attribute
// and a set of assertions.

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;


class BowlTest {
    // Define a test starting with @Test. The test is like
    // a small main method - you need to setup everything
    // and you can write any arbitrary Java code in it.

    protected void traverseAndCheckBoard(Bowl currentBowl, int position) {
        Bowl initialBowl = currentBowl;
        int traversedCount = 0;
        int currentPosition = 0;
        for (int i = 0; i < 14; i++) {

            if ((position + traversedCount) > 14) {
                // if looping around the board, position = ((start+traversed) - total)
                // in other words the amount of bowls that the absolute position is greater than the board's total bowls
                //
                // Only relevant during construction
                currentPosition = ((traversedCount + position) - 14);
            } else
                // Or just use normal position
                currentPosition = position + traversedCount;

            // check for kalaha's, and check for smallbowl otherwise
            if (currentPosition == 7 || currentPosition == 14)
                assertEquals(currentBowl.getClass(), Kalaha.class);
            else
                assertEquals(currentBowl.getClass(), SmallBowl.class);

            currentBowl = currentBowl.getNextBowl();
            assertNotNull(currentBowl);
            traversedCount++;
        }
        assertSame(initialBowl, currentBowl);
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class For_a_normal_mancala_bowl{
        SmallBowl firstSmallBowlPlayer;

        @BeforeEach
        public void makeASmallBowlInMancala() {
            firstSmallBowlPlayer = new SmallBowl();
        }

        @ParameterizedTest
        @ValueSource(ints = {1,2,3,4,5,6,8,9,10,11,12,13})
        public void GIVEN_always_WHEN_created_THEN_it_exists_in_a_mancala_board(int position) {
            traverseAndCheckBoard(new SmallBowl(position), position);
            traverseAndCheckBoard(new SmallBowl(), 1);
        }

        @Nested
        class GIVEN_its_the_start_of_the_game {
            @Test
            public void WHEN_before_any_small_bowls_are_played_THEN_has_four_rocks() {
                Bowl current = firstSmallBowlPlayer;
                for (int i = 0; i < 14; i++) {
                    current = current.getNextBowl();
                    if (current.getClass() == SmallBowl.class)
                        assertEquals(current.getMyRocks(), 4);
                }
                assertSame(current, firstSmallBowlPlayer);
            }

            @Test
            public void WHEN_chosen_by_the_player_that_has_the_turn_THEN_distribute_its_rocks_anti_clockwise() {
                int initialRocks = firstSmallBowlPlayer.getMyRocks();
                firstSmallBowlPlayer.play();
                Bowl neighbour = firstSmallBowlPlayer.getNextBowl();
                for (int i = 0; i < initialRocks; i++) {
                    assertEquals(5, neighbour.getMyRocks());
                    neighbour = neighbour.getNextBowl();
                }
            }
        }

        @Nested
        class GIVEN_the_game_is_in_a_state_where {

            @Test
            public void its_not_the_players_turn_WHEN_played_by_the_player_THEN_nothing_happens() {
                firstSmallBowlPlayer.getPlayerThatOwnsMe().switchTurn();
                int initialRocks = firstSmallBowlPlayer.getMyRocks();
                firstSmallBowlPlayer.play();
                Bowl neighbour = firstSmallBowlPlayer.getNextBowl();
                for (int i = 0; i < initialRocks; i++) {
                    assertEquals(4, neighbour.getMyRocks());
                    neighbour = neighbour.getNextBowl();
                }
            }

            @Test
            public void play_can_reach_opponents_kalaha_WHEN_played_by_the_player_THEN_opponents_kalaha_is_skipped() {
                SmallBowl playWillSkipFromThisBowl = goToSkippableState();
                int opponentKalahaRocksBefore = firstSmallBowlPlayer.getNextSmallBowlRepeat(6).getKalaha().getMyRocks();
                playWillSkipFromThisBowl.play();
                int opponentKalahaRocksAfter = firstSmallBowlPlayer.getNextSmallBowlRepeat(6).getKalaha().getMyRocks();
                assertEquals(opponentKalahaRocksBefore, opponentKalahaRocksAfter);
            }

            @Test
            public void the_bowl_is_empty_WHEN_the_player_plays_the_empty_bowl_THEN_nothing_happens() {
                firstSmallBowlPlayer.play();
                firstSmallBowlPlayer.getPlayerThatOwnsMe().switchTurn();
                assertTrue(firstSmallBowlPlayer.getPlayerThatOwnsMe().hasTheTurn());
                firstSmallBowlPlayer.play();
                assertTrue(firstSmallBowlPlayer.getPlayerThatOwnsMe().hasTheTurn());
                assertEquals(5, firstSmallBowlPlayer.getNextBowl().getMyRocks());
            }

            @Test
            public void all_small_bowls_of_the_player_are_empty_WHEN_a_play_ends_THEN_tell_players_who_won() {
                Player player = firstSmallBowlPlayer.getPlayerThatOwnsMe();
                Player opponent = firstSmallBowlPlayer.getNextSmallBowlRepeat(6).getPlayerThatOwnsMe();
                assertFalse(player.won());
                assertFalse(opponent.won());
                goToEndOfSillyGame();
                assertTrue(player.won());
                assertFalse(opponent.won());

            }

            @Test
            public void all_small_bowls_of_the_player_are_empty_WHEN_a_play_ends_THEN_tell_players_who_wonOPPONENTVARIATION() {
                Player player = firstSmallBowlPlayer.getPlayerThatOwnsMe();
                Player opponent = firstSmallBowlPlayer.getNextSmallBowlRepeat(6).getPlayerThatOwnsMe();
                goToEndOfGameWhereOpponentWins();
                assertFalse(player.won());
                assertTrue(opponent.won());
            }

            private void goToEndOfGameWhereOpponentWins() {
                SmallBowl firstSmallBowlOpponent = firstSmallBowlPlayer.getNextSmallBowlRepeat(6);
                firstSmallBowlPlayer.getNextSmallBowlRepeat(1).play();
                firstSmallBowlOpponent.getNextSmallBowlRepeat(2).play();
                firstSmallBowlOpponent.getNextSmallBowlRepeat(5).play();
                firstSmallBowlPlayer.getNextSmallBowlRepeat(1).play();
                firstSmallBowlOpponent.getNextSmallBowlRepeat(1).play();
                firstSmallBowlPlayer.getNextSmallBowlRepeat(2).play();
                firstSmallBowlOpponent.play();
                firstSmallBowlPlayer.getNextSmallBowlRepeat(3).play();
                firstSmallBowlOpponent.getNextSmallBowlRepeat(1).play();
                firstSmallBowlPlayer.getNextSmallBowlRepeat(4).play();
                firstSmallBowlOpponent.play();
                // Cheating here, let player go again >:), i'm too dumb too make a loop/skip and steal play happen in fair game
                firstSmallBowlOpponent.getPlayerThatOwnsMe().switchTurn();
                // Should skip and steal
                // this bowls rocks
                assertEquals(10, firstSmallBowlOpponent.getNextSmallBowlRepeat(3).getMyRocks());
                // End up here by looping around the board, thus skipping
                assertEquals(0, firstSmallBowlOpponent.getMyRocks());
                // Thus steal from last bowl on players side
                assertEquals(8, firstSmallBowlPlayer.getNextSmallBowlRepeat(5).getMyRocks());
                // Result is big kalaha booty
                assertEquals(8, firstSmallBowlOpponent.getKalaha().getMyRocks());
                firstSmallBowlOpponent.getNextSmallBowlRepeat(3).play();
                assertEquals(19, firstSmallBowlOpponent.getKalaha().getMyRocks());
                firstSmallBowlPlayer.getNextSmallBowlRepeat(1).play();
                firstSmallBowlOpponent.getNextSmallBowlRepeat(1).play();
                firstSmallBowlPlayer.play();
                firstSmallBowlPlayer.getPlayerThatOwnsMe().switchTurn();
                firstSmallBowlPlayer.getNextSmallBowlRepeat(3).play();
                firstSmallBowlPlayer.getPlayerThatOwnsMe().switchTurn();
                firstSmallBowlPlayer.getNextSmallBowlRepeat(4).play();
                firstSmallBowlPlayer.getNextSmallBowlRepeat(5).play();
            }

            private void goToEndOfSillyGame() {
                SmallBowl firstSmallBowlOpponent = firstSmallBowlPlayer.getNextSmallBowlRepeat(6);

                // player
                // Best opening
                firstSmallBowlPlayer.getNextSmallBowlRepeat(2).play();
                // Set up for steal move
                firstSmallBowlPlayer.getNextSmallBowlRepeat(4).play();
                assertEquals(2, firstSmallBowlPlayer.getKalaha().getMyRocks());

                // opponent
                // ... worst opening?
                firstSmallBowlOpponent.play();

                // player
                firstSmallBowlPlayer.play();
                // Check if i did it properly on paper
                assertEquals(9, firstSmallBowlPlayer.getKalaha().getMyRocks());
                assertEquals(0, firstSmallBowlPlayer.getNextSmallBowlRepeat(4).getMyRocks());
                assertEquals(0, firstSmallBowlPlayer.getNextSmallBowlRepeat(4).getOpposite().getMyRocks());

                // opponent
                firstSmallBowlOpponent.getNextSmallBowlRepeat(3).play();

                //Player
                firstSmallBowlPlayer.getNextSmallBowlRepeat(3).play();
                assertEquals(10, firstSmallBowlPlayer.getKalaha().getMyRocks());

                // opponent makes stupid move again
                firstSmallBowlOpponent.getNextSmallBowlRepeat(1).play();

                // player makes big steal
                firstSmallBowlPlayer.getNextSmallBowlRepeat(2).play();
                assertEquals(19, firstSmallBowlPlayer.getKalaha().getMyRocks());

                // opponent steals tiny booty
                firstSmallBowlOpponent.play();
                assertEquals(3, firstSmallBowlOpponent.getKalaha().getMyRocks());

                // player is stalling until the end
                firstSmallBowlPlayer.play();

                // opponent is heading for disaster
                firstSmallBowlOpponent.getNextSmallBowlRepeat(5).play();
                firstSmallBowlPlayer.play();
                firstSmallBowlOpponent.getNextSmallBowlRepeat(4).play();
                firstSmallBowlPlayer.play();
                firstSmallBowlOpponent.getNextSmallBowlRepeat(5).play();
                // everything empty!
                for (int i = 0; i < 6; i++) {
                    assertEquals(0, firstSmallBowlOpponent.getNextSmallBowlRepeat(i).getMyRocks());
                }

            }

            private SmallBowl goToSkippableState() {
                SmallBowl firstSmallBowlOpponent = firstSmallBowlPlayer.getNextSmallBowlRepeat(6);

                firstSmallBowlPlayer.getNextSmallBowlRepeat(2).play();
                firstSmallBowlPlayer.getNextSmallBowlRepeat(3).play();

                firstSmallBowlOpponent.getNextSmallBowlRepeat(2).play();
                firstSmallBowlOpponent.getNextSmallBowlRepeat(3).play();

                firstSmallBowlPlayer.play();
                firstSmallBowlOpponent.play();

                firstSmallBowlPlayer.getNextSmallBowlRepeat(4).play();
                firstSmallBowlOpponent.getNextSmallBowlRepeat(4).play();

                // Playing this bowl should give a skip!
                assertTrue(firstSmallBowlPlayer.getNextSmallBowlRepeat(5).getMyRocks() >= 8);
                return firstSmallBowlPlayer.getNextSmallBowlRepeat(5);
            }
        }

        @Nested
        class GIVEN_the_play_ends{

            @Test
            public void in_own_kalaha_WHEN_play_ends_THEN_turn_is_not_switched() {
                firstSmallBowlPlayer.getNextSmallBowlRepeat(2).play();
                assertTrue(firstSmallBowlPlayer.getPlayerThatOwnsMe().hasTheTurn());
            }

            @Test
            public void in_own_small_bowl_WHEN_play_ends_THEN_turn_is_switched() {
                firstSmallBowlPlayer.play();
                assertFalse(firstSmallBowlPlayer.getPlayerThatOwnsMe().hasTheTurn());
            }

            @Test
            public void in_opponents_small_bowl_WHEN_player_plays_this_bowl_THEN_turn_is_switched() {
                firstSmallBowlPlayer.getNextSmallBowlRepeat(5).play();
                assertFalse(firstSmallBowlPlayer.getPlayerThatOwnsMe().hasTheTurn());
            }

            @Test
            public void in_own_empty_small_bowl_and_opposite_has_rocks_WHEN_play_ends_THEN_rocks_of_opposite_plus_last_rock_of_play_are_added_to_kalaha() {
                firstSmallBowlPlayer.getNextSmallBowlRepeat(5).play();
                SmallBowl firstSmallBowlOpponent = firstSmallBowlPlayer.getNextSmallBowlRepeat(6);
                firstSmallBowlOpponent.getNextSmallBowlRepeat(5).play();
                firstSmallBowlPlayer.play();
                assertEquals(7, firstSmallBowlPlayer.getKalaha().getMyRocks());
            }

        }


    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class a_kalaha {

        Kalaha kalaha;

        @BeforeEach
        public void makeKalahaInBoard() {
            kalaha = new Kalaha();
        }

        @Test
        public void exists_in_a_mancala_board() {
            traverseAndCheckBoard(kalaha, 14);
        }

        @Test
        public void has_zero_rocks_when_created() {
            Bowl current = kalaha;
            for (int i = 0; i < 14; i++) {
                current = current.getNextBowl();
                if (current.getClass() == Kalaha.class)
                    assertEquals(current.getMyRocks(), 0);
            }
        }
    }


    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class in_a_game_of_mancala {

    }
}