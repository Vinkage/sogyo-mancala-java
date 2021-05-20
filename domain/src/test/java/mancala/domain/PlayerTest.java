package mancala.domain;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("A player model ")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PlayerTest {
    Player player;
    Player opponent;

    @BeforeEach
    public void let_player_and_oppenent_exist() {
        player = new Player();
        opponent = player.getOpponent();
    }

    @Test
    public void has_turn_when_created() {
        assertTrue(player.hasTheTurn());
    }

    @Test
    public void opponent_does_not_have_turn_when_player_models_are_created() {
        assertFalse(player.getOpponent().hasTheTurn());
    }

    @Test
    public void can_change_turn_when_necessary() {
        player.switchTurn();
        assertFalse(player.hasTheTurn());
        assertTrue(player.getOpponent().hasTheTurn());
    }
}