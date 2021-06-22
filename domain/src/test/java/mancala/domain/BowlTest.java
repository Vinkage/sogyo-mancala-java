package mancala.domain;

import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


@IndicativeSentencesGeneration(separator = " -> ", generator = DisplayNameGenerator.ReplaceUnderscores.class)
class BowlTest {


    @Nested
    class instantiatingDefaultGame {

        private void checkIfDefaultState(Bowl currentBowl, int position) {
            Bowl initialBowl = currentBowl;
            int traversedCount = 0;
            int currentPosition = 0;
            for (int i = 0; i < 14; i++) {
                if ((position + traversedCount) > 14) {
                    // if looping around the board, position = ((start+traversed) - total)
                    // in other words the amount of bowls that the absolute position is greater than the board's total bowls
                    //
                    // Only relevant to check construction btw, also only checking in the case where there are 14 bowls
                    currentPosition = ((traversedCount + position) - 14);
                } else
                    // Or just use normal position
                    currentPosition = position + traversedCount;

                // check for kalaha's, and check for smallbowl otherwise
                if (currentPosition == 7 || currentPosition == 14) {
                    assertEquals(currentBowl.getClass(), Kalaha.class);
                    assertEquals(0, currentBowl.getMyStones(),
                            "In a 'normal' game the kalaha should have 0 rocks when created.");
                } else {
                    assertEquals(currentBowl.getClass(), SmallBowl.class);
                    assertEquals(4, currentBowl.getMyStones(),
                            "In a 'normal' game the smallbowl should have 4 rocks when created.");
                }

                currentBowl = currentBowl.getNextBowl();
                assertNotNull(currentBowl);
                traversedCount++;
            }
            assertSame(initialBowl, currentBowl);
        }

        SmallBowl referenceSmallBowl = new SmallBowl();

        @Test
        public void default_stones_amount_check() {
            checkIfDefaultState(referenceSmallBowl, 1);
        }

        @Nested
        class given_a_small_bowl {

            @Nested
            class and_its_the_start_of_the_game {
                @Test
                public void when_getMyStones_is_called_then_it_returns_four() {
                    SmallBowl next = referenceSmallBowl.getNextSmallBowl();
                    while (!next.equals(referenceSmallBowl)) {
                        assertEquals(4, next.getMyStones());
                        next = next.getNextSmallBowl();
                    }
                }

                @Test
                public void when_play_is_called_and_owner_has_turn_then_distribute_its_rocks_anti_clockwise() {
                    int initialRocks = referenceSmallBowl.getMyStones();
                    referenceSmallBowl.play();
                    Bowl neighbour = referenceSmallBowl.getNextBowl();
                    for (int i = 0; i < initialRocks; i++) {
                        assertEquals(5, neighbour.getMyStones());
                        neighbour = neighbour.getNextBowl();
                    }
                    assertEquals(4, neighbour.getMyStones());
                }
            }

            @Nested
            class and_the_game_is_in_a_state_where {

                @Test
                public void its_not_the_players_turn_when_play_is_called_then_nothing_happens() {
                    referenceSmallBowl.getMyOwner().switchTurn();
                    int initialRocks = referenceSmallBowl.getMyStones();
                    referenceSmallBowl.play();
                    Bowl neighbour = referenceSmallBowl.getNextBowl();
                    for (int i = 0; i < initialRocks; i++) {
                        assertEquals(4, neighbour.getMyStones());
                        neighbour = neighbour.getNextBowl();
                    }
                    assertEquals(4, neighbour.getMyStones());
                }

                @Test
                public void the_bowl_is_empty_WHEN_the_player_plays_the_empty_bowl_THEN_nothing_happens() {
                    referenceSmallBowl.play();
                    referenceSmallBowl.getMyOwner().switchTurn();
                    assertTrue(referenceSmallBowl.getMyOwner().hasTheTurn());
                    referenceSmallBowl.play();
                    assertTrue(referenceSmallBowl.getMyOwner().hasTheTurn());
                    assertEquals(5, referenceSmallBowl.getNextBowl().getMyStones());
                }

                @Test
                public void all_small_bowls_of_the_player_are_empty_WHEN_a_play_ends_THEN_tell_players_who_won() {
                    Player player = referenceSmallBowl.getMyOwner();
                    Player opponent = referenceSmallBowl.getNextSmallBowlTimes(6).getMyOwner();
                    assertFalse(player.won());
                    assertFalse(opponent.won());
                    goToEndOfSillyGame();
                    assertTrue(player.won());
                    assertFalse(opponent.won());

                }

                @Test
                public void all_small_bowls_of_the_player_are_empty_WHEN_a_play_ends_THEN_tell_players_who_wonOPPONENTVARIATION() {
                    Player player = referenceSmallBowl.getMyOwner();
                    Player opponent = referenceSmallBowl.getNextSmallBowlTimes(6).getMyOwner();
                    goToEndOfGameWhereOpponentWins();
                    assertFalse(player.won());
                    assertTrue(opponent.won());
                }

                @Test
                public void the_play_would_skip_past_opponent_kalaha_at_the_last_rock_and_steal_WHEN_played_THEN_should_skip_and_steal_correctly() {
                    goToSkipAndStealOnLast();
                    SmallBowl firstSmallBowlOpponent = referenceSmallBowl.getNextSmallBowlTimes(6);
                    assertEquals(3, referenceSmallBowl.getNextSmallBowlTimes(5).getNextBowl().getMyStones());
                    firstSmallBowlOpponent.getNextSmallBowlTimes(3).play();
                    assertEquals(19, firstSmallBowlOpponent.getNextSmallBowlTimes(5).getNextBowl().getMyStones());
                }

                private void goToSkipAndStealOnLast() {
                    SmallBowl firstSmallBowlOpponent = referenceSmallBowl.getNextSmallBowlTimes(6);
                    referenceSmallBowl.getNextSmallBowlTimes(1).play();
                    firstSmallBowlOpponent.getNextSmallBowlTimes(2).play();
                    firstSmallBowlOpponent.getNextSmallBowlTimes(5).play();
                    referenceSmallBowl.getNextSmallBowlTimes(1).play();
                    firstSmallBowlOpponent.getNextSmallBowlTimes(1).play();
                    referenceSmallBowl.getNextSmallBowlTimes(2).play();
                    firstSmallBowlOpponent.play();
                    referenceSmallBowl.getNextSmallBowlTimes(3).play();
                    firstSmallBowlOpponent.getNextSmallBowlTimes(1).play();
                    referenceSmallBowl.getNextSmallBowlTimes(4).play();
                    firstSmallBowlOpponent.play();
                    // Cheating here, let player go again >:), i'm too dumb too make a loop/skip and steal play happen in fair game
                    firstSmallBowlOpponent.getMyOwner().switchTurn();
                    // Should skip and steal
                    // this bowls rocks
                    assertEquals(10, firstSmallBowlOpponent.getNextSmallBowlTimes(3).getMyStones());
                    // End up here by looping around the board, thus skipping
                    assertEquals(0, firstSmallBowlOpponent.getMyStones());
                    // Thus steal from last bowl on players side
                    assertEquals(8, referenceSmallBowl.getNextSmallBowlTimes(5).getMyStones());
                    // Result is big kalaha booty
                    assertEquals(8, firstSmallBowlOpponent.getNextSmallBowlTimes(5).getNextBowl().getMyStones());
                }

                private void goToEndOfGameWhereOpponentWins() {
                    goToSkipAndStealOnLast();
                    SmallBowl firstSmallBowlOpponent = referenceSmallBowl.getNextSmallBowlTimes(6);
                    firstSmallBowlOpponent.getNextSmallBowlTimes(3).play();
                    referenceSmallBowl.getNextSmallBowlTimes(1).play();
                    firstSmallBowlOpponent.getNextSmallBowlTimes(1).play();
                    referenceSmallBowl.play();
                    referenceSmallBowl.getMyOwner().switchTurn();
                    referenceSmallBowl.getNextSmallBowlTimes(3).play();
                    referenceSmallBowl.getMyOwner().switchTurn();
                    referenceSmallBowl.getNextSmallBowlTimes(4).play();
                    referenceSmallBowl.getNextSmallBowlTimes(5).play();
                }

                private void goToEndOfSillyGame() {
                    SmallBowl firstSmallBowlOpponent = referenceSmallBowl.getNextSmallBowlTimes(6);

                    // player
                    // Best opening
                    referenceSmallBowl.getNextSmallBowlTimes(2).play();
                    // Set up for steal move
                    referenceSmallBowl.getNextSmallBowlTimes(4).play();
                    assertEquals(2, referenceSmallBowl.getKalaha().getMyStones());

                    // opponent
                    // ... worst opening?
                    firstSmallBowlOpponent.play();

                    // player
                    assertSame(referenceSmallBowl.getNextSmallBowlTimes(4).getOpposite(), referenceSmallBowl.getKalaha().getNextBowl().getNextBowl());
                    referenceSmallBowl.play();
                    // Check if i did it properly on paper
                    assertEquals(9, referenceSmallBowl.getKalaha().getMyStones());
                    assertEquals(0, referenceSmallBowl.getNextSmallBowlTimes(4).getMyStones());
                    // assertEquals(0, firstSmallBowlPlayer.getNextSmallBowlTimes(4).getOpposite().getMyRocks());

                    // opponent
                    firstSmallBowlOpponent.getNextSmallBowlTimes(3).play();

                    //Player
                    referenceSmallBowl.getNextSmallBowlTimes(3).play();
                    assertEquals(10, referenceSmallBowl.getNextSmallBowlTimes(5).getNextBowl().getMyStones());

                    // opponent makes stupid move again
                    firstSmallBowlOpponent.getNextSmallBowlTimes(1).play();

                    // player makes big steal
                    //assertEquals(0, firstSmallBowlPlayer.getNextSmallBowlTimes(5).getNextBowl().getMyRocks());
                    assertEquals(10, referenceSmallBowl.getNextSmallBowlTimes(5).getNextBowl().getMyStones());
                    referenceSmallBowl.getNextSmallBowlTimes(2).play();
                    assertEquals(19, referenceSmallBowl.getNextSmallBowlTimes(5).getNextBowl().getMyStones());

                    // opponent steals tiny booty
                    firstSmallBowlOpponent.play();
                    assertEquals(3, firstSmallBowlOpponent.getNextSmallBowlTimes(5).getNextBowl().getMyStones());

                    // player is stalling until the end
                    referenceSmallBowl.play();

                    // opponent is heading for disaster
                    firstSmallBowlOpponent.getNextSmallBowlTimes(5).play();
                    referenceSmallBowl.play();
                    firstSmallBowlOpponent.getNextSmallBowlTimes(4).play();
                    referenceSmallBowl.play();
                    firstSmallBowlOpponent.getNextSmallBowlTimes(5).play();
                    // everything empty!
                    for (int i = 0; i < 6; i++) {
                        assertEquals(0, firstSmallBowlOpponent.getNextSmallBowlTimes(i).getMyStones());
                    }

                }

                private SmallBowl goToSkippableState() {
                    SmallBowl firstSmallBowlOpponent = referenceSmallBowl.getNextSmallBowlTimes(6);

                    referenceSmallBowl.getNextSmallBowlTimes(2).play();
                    referenceSmallBowl.getNextSmallBowlTimes(3).play();

                    firstSmallBowlOpponent.getNextSmallBowlTimes(2).play();
                    firstSmallBowlOpponent.getNextSmallBowlTimes(3).play();

                    referenceSmallBowl.play();
                    firstSmallBowlOpponent.play();

                    referenceSmallBowl.getNextSmallBowlTimes(4).play();
                    firstSmallBowlOpponent.getNextSmallBowlTimes(4).play();

                    // Playing this bowl should give a skip!
                    assertTrue(referenceSmallBowl.getNextSmallBowlTimes(5).getMyStones() >= 8);
                    return referenceSmallBowl.getNextSmallBowlTimes(5);
                }
            }

            @Nested
            class GIVEN_the_play_ends{

                @Test
                public void in_own_kalaha_WHEN_play_ends_THEN_turn_is_not_switched() {
                    referenceSmallBowl.getNextSmallBowlTimes(2).play();
                    assertTrue(referenceSmallBowl.getMyOwner().hasTheTurn());
                }

                @Test
                public void in_own_small_bowl_WHEN_play_ends_THEN_turn_is_switched() {
                    referenceSmallBowl.play();
                    assertFalse(referenceSmallBowl.getMyOwner().hasTheTurn());
                }

                @Test
                public void in_opponents_small_bowl_WHEN_player_plays_this_bowl_THEN_turn_is_switched() {
                    referenceSmallBowl.getNextSmallBowlTimes(5).play();
                    assertFalse(referenceSmallBowl.getMyOwner().hasTheTurn());
                }

                @Test
                public void in_own_empty_small_bowl_and_opposite_has_rocks_WHEN_play_ends_THEN_rocks_of_opposite_plus_last_rock_of_play_are_added_to_kalaha() {
                    referenceSmallBowl.getNextSmallBowlTimes(5).play();
                    SmallBowl firstSmallBowlOpponent = referenceSmallBowl.getNextSmallBowlTimes(6);
                    firstSmallBowlOpponent.getNextSmallBowlTimes(5).play();
                    assertSame(referenceSmallBowl.getNextSmallBowlTimes(1).getOpposite(), referenceSmallBowl.getKalaha().getNextSmallBowl().getNextSmallBowlTimes(4));
                    // assertSame(firstSmallBowlPlayer.getOpposite(), firstSmallBowlPlayer.getKalaha().getNextSmallBowlTimes(5));
                    referenceSmallBowl.play();
                    assertEquals(7, referenceSmallBowl.getNextSmallBowlTimes(5).getNextBowl().getMyStones());
                }

            }


        }

        @Nested
        class given_a_kalaha {

            Kalaha playerKalaha;
            Kalaha opponentKalaha;

            @BeforeEach
            public void makeKalahaInBoard() {
                playerKalaha = referenceSmallBowl.getKalaha();
                opponentKalaha = referenceSmallBowl.getKalaha().getNextSmallBowl().getKalaha();
            }

            @Test
            public void when_getMyStones_is_called_after_instantiating_then_has_zero_stones() {
                assertEquals(0, playerKalaha.getMyStones());
                assertEquals(0, opponentKalaha.getMyStones());
            }
        }

    }


    @Nested
    class instantiatingGameWithStonesList {
        SmallBowl referenceSmallBowl;

        @Test
        void given_a_stones_list_when_instantiating_a_small_bowl_then_configure_stones_as_in_list() {
            int[] stonesArray = new int[] {1,2,3,4,5,6,7,8,9,10,11,12,13,14};
            List<Integer> stonesList = Arrays.stream(stonesArray).boxed().collect(Collectors.toList());
            referenceSmallBowl = new SmallBowl(stonesList);
            for (int i = 0; i < stonesArray.length; i++) {
                if (i < 6) assertEquals(stonesArray[i], referenceSmallBowl.getNextSmallBowlTimes(i).getMyStones());
                else if (i == 6) assertEquals(stonesArray[i], referenceSmallBowl.getKalaha().getMyStones());
                else if (i == 13) assertEquals(stonesArray[i], referenceSmallBowl.getKalaha().getNextBowl().getKalaha().getMyStones());
                else {
                    int index = i - 1;
                    assertEquals(stonesArray[i], referenceSmallBowl.getNextSmallBowlTimes(index).getMyStones());
                }
            }
        }

        @Test
        public void given_stones_can_reach_oppenent_kalaha_when_played_validly_then_opponents_kalaha_is_skipped() {
            SmallBowl playWillSkipFromThisBowl = goToSkippableState();
            int opponentKalahaRocksBefore = referenceSmallBowl.getNextSmallBowlTimes(11).getNextBowl().getMyStones();
            playWillSkipFromThisBowl.play();
            int opponentKalahaRocksAfter = referenceSmallBowl.getNextSmallBowlTimes(11).getNextBowl().getMyStones();
            assertEquals(opponentKalahaRocksBefore, opponentKalahaRocksAfter);
        }
    }
}