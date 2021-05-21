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
        public void given_always_when_created_it_exists_in_a_mancala_board(int position) {
            traverseAndCheckBoard(new SmallBowl(position), position);
            traverseAndCheckBoard(new SmallBowl(), 1);
        }

        @Nested
        class given_its_the_start_of_the_game {
            @Test
            public void When_created_Then_has_four_rocks() {
                Bowl current = firstSmallBowlPlayer;
                for (int i = 0; i < 14; i++) {
                    current = current.getNextBowl();
                    if (current.getClass() == SmallBowl.class)
                        assertEquals(current.getMyRocks(), 4);
                }
                assertSame(current, firstSmallBowlPlayer);
            }

            @Test
            public void When_chosen_by_the_player_that_has_the_turn_Then_distribute_its_rocks_anti_clockwise() {
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
        class given_the_game_is_in_a_state_where {

            @Test
            public void its_not_the_players_turn_When_played_by_the_player_Then_nothing_happens() {
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
            public void play_can_reach_opponents_kalaha_When_player_plays_Then_opponents_kalaha_is_skipped() {
                SmallBowl playWillSkipFromThisBowl = goToSkippableState();
                int opponentKalahaRocksBefore = firstSmallBowlPlayer.getNextSmallBowlRepeat(6).getKalaha().getMyRocks();
                playWillSkipFromThisBowl.play();
                int opponentKalahaRocksAfter = firstSmallBowlPlayer.getNextSmallBowlRepeat(6).getKalaha().getMyRocks();
                assertEquals(opponentKalahaRocksBefore, opponentKalahaRocksAfter);
            }

            @Test
            public void the_bowl_is_empty_When_player_plays_the_bowl_Then_nothing_happens() {
                firstSmallBowlPlayer.play();
                firstSmallBowlPlayer.getPlayerThatOwnsMe().switchTurn();
                assertTrue(firstSmallBowlPlayer.getPlayerThatOwnsMe().hasTheTurn());
                firstSmallBowlPlayer.play();
                assertTrue(firstSmallBowlPlayer.getPlayerThatOwnsMe().hasTheTurn());
                assertEquals(5, firstSmallBowlPlayer.getNextBowl().getMyRocks());
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
        class given_the_play_ends{

            @Test
            public void in_own_kalaha_When_player_plays_this_bowl_Then_turn_is_not_switched() {
                firstSmallBowlPlayer.getNextSmallBowlRepeat(2).play();
                assertTrue(firstSmallBowlPlayer.getPlayerThatOwnsMe().hasTheTurn());
            }

            @Test
            public void in_own_small_bowl_When_player_plays_this_bowl_Then_turn_is_switched() {
                firstSmallBowlPlayer.play();
                assertFalse(firstSmallBowlPlayer.getPlayerThatOwnsMe().hasTheTurn());
            }

            @Test
            public void in_opponents_small_bowl_When_player_plays_this_bowl_Then_turn_is_switched() {
                firstSmallBowlPlayer.getNextSmallBowlRepeat(5).play();
                assertFalse(firstSmallBowlPlayer.getPlayerThatOwnsMe().hasTheTurn());
            }

            @Test
            public void in_own_empty_small_bowl_and_opposite_has_rocks_When_player_plays_this_bowl_Then_opposite_and_rock_are_added_to_kalaha() {
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