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

        SmallBowl referenceSmallBowl;

        {
            try {
                referenceSmallBowl = new SmallBowl();
            } catch (DomainSmallBowlException e) {
                e.printStackTrace();
            }
        }

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
        }

        @Nested
        class given_a_kalaha {

            Kalaha playerKalaha = referenceSmallBowl.getKalaha();
            Kalaha opponentKalaha = referenceSmallBowl.getKalaha().getNextSmallBowl().getKalaha();

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
            try {
                referenceSmallBowl = new SmallBowl(stonesList);
            } catch (DomainSmallBowlException e) {
                fail("Invalid instantiation.");
            }
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
        void given_a_stones_list_with_odd_number_of_elements_when_instantiating_small_bowl_then_throw_DomainSmallBowlException() {
            int[] stonesArray = new int[] {1,2,3,4,5,6,7,8,9,10,11,12,13};
            List<Integer> stonesList = Arrays.stream(stonesArray).boxed().collect(Collectors.toList());
            try {
                referenceSmallBowl = new SmallBowl(stonesList);
                fail();
            } catch (DomainSmallBowlException e) {
            }
        }

        void setupGameSituationAndFailIfInvalid(int[] stonesArray) {
            try {
                referenceSmallBowl = new SmallBowl(
                        Arrays.stream(
                                stonesArray
                        ).boxed().collect(Collectors.toList())
                );
            } catch (DomainSmallBowlException e) {
                fail("Invalid instantiation.");
            }
        }

        @Nested
        class playBehaviour {

            {
                // setup default game in this sub class
                // by default
                try {
                    referenceSmallBowl = new SmallBowl();
                } catch (DomainSmallBowlException e) {
                    e.printStackTrace();
                }
            }

            @Test
            void given_stones_can_reach_oppenent_kalaha_when_played_validly_then_opponents_kalaha_is_skipped() {
                setupGameSituationAndFailIfInvalid(new int[] {0,0,0,0,0,100,0,0,0,0,0,0,0,0});
                referenceSmallBowl.getNextSmallBowlTimes(5).play();
                assertEquals(0, referenceSmallBowl.getKalaha().getNextBowl().getKalaha().getMyStones());
            }

            @Test
            void given_its_not_the_players_turn_when_play_is_called_then_nothing_happens() {
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
            void given_the_bowl_is_empty_when_play_called_on_the_bowl_then_nothing_happens() {
                referenceSmallBowl.play();
                referenceSmallBowl.getMyOwner().switchTurn();
                assertTrue(referenceSmallBowl.getMyOwner().hasTheTurn());
                referenceSmallBowl.play();
                assertTrue(referenceSmallBowl.getMyOwner().hasTheTurn());
                assertEquals(5, referenceSmallBowl.getNextBowl().getMyStones());
            }

            @Test
            void given_stones_would_skip_opponent_kalaha_at_the_last_rock_and_steal_when_played_then_should_skip_and_steal_correctly() {
                setupGameSituationAndFailIfInvalid(new int[] {13,0,0,0,0,0,
                                                            0,
                                                                0,0,0,0,0,8,
                                                            0});
                System.out.println(referenceSmallBowl.getMyStones());
                System.out.println(referenceSmallBowl.stateString());
                referenceSmallBowl.play();
                System.out.println(referenceSmallBowl.stateString());

                assertEquals(11, referenceSmallBowl.getKalaha().getMyStones(),
                        "resulting kalaha stones after stealing should be 11.");
                assertEquals(0, referenceSmallBowl.getMyStones(),
                        "played bowl should be zero, since steal happened");
                assertEquals(0, referenceSmallBowl.getNextSmallBowlTimes(12).getMyStones());
            }

            @Test
            void given_that_play_ends_in_own_kalaha_when_play_is_called_validly_then_turn_is_not_switched() {
                referenceSmallBowl.getNextSmallBowlTimes(2).play();
                assertTrue(referenceSmallBowl.getMyOwner().hasTheTurn());
            }

            @Test
            void given_that_play_ends_in_own_small_bowl_when_play_is_called_validly_then_turn_is_switched() {
                referenceSmallBowl.play();
                assertFalse(referenceSmallBowl.getMyOwner().hasTheTurn());
            }

            @Test
            void given_that_play_ends_in_opponents_small_bowl_when_play_is_called_validly_then_turn_is_switched() {
                referenceSmallBowl.getNextSmallBowlTimes(5).play();
                assertFalse(referenceSmallBowl.getMyOwner().hasTheTurn());
            }

            @Test
            void given_that_play_ends_in_own_empty_small_bowl_and_opposite_has_rocks_when_play_is_called_validly_then_rocks_of_opposite_plus_last_rock_of_play_are_added_to_next_kalaha() {
                System.out.println(referenceSmallBowl.stateString());
                referenceSmallBowl.getNextSmallBowlTimes(5).play();
                System.out.println(referenceSmallBowl.stateString());
                SmallBowl firstSmallBowlOpponent = referenceSmallBowl.getNextSmallBowlTimes(6);
                firstSmallBowlOpponent.getNextSmallBowlTimes(5).play();
                System.out.println(referenceSmallBowl.stateString());
                referenceSmallBowl.play();
                System.out.println(referenceSmallBowl.stateString());
                assertEquals(7, referenceSmallBowl.getKalaha().getMyStones());
                assertEquals(0, referenceSmallBowl.getNextSmallBowlTimes(5).getMyStones());
                assertEquals(0, referenceSmallBowl.getKalaha().getNextBowl().getMyStones());
            }
        }

        @Nested
        class endGameBehaviour {

            @Test
            void given_all_small_bowls_of_the_player_are_empty_when_a_play_ends_then_tell_players_who_won() {
                setupGameSituationAndFailIfInvalid(new int[] {0,0,0,0,0,1,0,4,4,4,4,4,4,0});
                Player player = referenceSmallBowl.getMyOwner();
                Player opponent = referenceSmallBowl.getNextSmallBowlTimes(6).getMyOwner();
                assertFalse(player.won(), "players haven't won at start of game");
                assertFalse(opponent.won(), "players haven't won at start of game");
                referenceSmallBowl.getNextSmallBowlTimes(5).play();
                assertFalse(player.won(), "player should lose here.");
                assertTrue(opponent.won(), "opponent should win here.");

                setupGameSituationAndFailIfInvalid(new int[] {4,4,4,4,4,4,0,0,0,0,0,0,1,0});
                player = referenceSmallBowl.getMyOwner();
                opponent = referenceSmallBowl.getNextSmallBowlTimes(6).getMyOwner();
                assertFalse(player.won(), "players haven't won at start of game");
                assertFalse(opponent.won(), "players haven't won at start of game");
                player.switchTurn();
                referenceSmallBowl.getNextSmallBowlTimes(6 + 5).play();
                assertTrue(player.won(), "player should win here.");
                assertFalse(opponent.won(), "opponent should lose here.");
            }

            @Test
            void given_all_small_bowls_of_the_player_are_empty_and_score_is_tied_when_a_play_ends_then_tell_both_player_they_won() {
                setupGameSituationAndFailIfInvalid(new int[] {0,0,0,0,0,1,0,0,0,0,0,0,1,0});
                System.out.println(referenceSmallBowl.stateString());
                referenceSmallBowl.getNextSmallBowlTimes(5).play();
                System.out.println(referenceSmallBowl.stateString());
                assertTrue(referenceSmallBowl.getMyOwner().won() && referenceSmallBowl.getMyOwner().getOpponent().won());
            }

        }
    }
}