package ru.mipt.bit.platformer.application;

import org.junit.Test;
import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.command.MoveCommand;
import ru.mipt.bit.platformer.command.ShootCommand;
import ru.mipt.bit.platformer.control.ControlStrategy;
import ru.mipt.bit.platformer.control.KeyboardControlStrategy;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.GameField;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.Tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class GameLoopTest {
    @Test
    public void collectsAllCommandsBeforeExecutingThemAndThenAdvancesDomain() {
        Tank player = new Tank(0, 0, Direction.RIGHT);
        Tank bot = new Tank(0, 1, Direction.RIGHT);
        GameField field = new GameField(4, 2, player, new Tree[0]);
        field.addTank(bot);
        List<String> events = new ArrayList<>();
        ControlStrategy playerStrategy = (game, tank) -> {
            events.add("collect player");
            assertEquals(0, bot.getX());
            return () -> {
                events.add("move player");
                new MoveCommand(game, tank, Direction.RIGHT).execute();
            };
        };
        ControlStrategy botStrategy = (game, tank) -> {
            events.add("collect bot");
            assertEquals(0, player.getX());
            return () -> {
                events.add("move bot");
                new MoveCommand(game, tank, Direction.RIGHT).execute();
            };
        };
        GameSession session = new GameLoop(field, new StubInput(true), playerStrategy,
                botStrategy, () -> events.add("toggle"));

        session.update(0.2f);

        assertEquals(Arrays.asList("collect player", "collect bot", "toggle", "move player", "move bot"), events);
        assertEquals(0.5f, field.getTankMovementProgress(player), 0.00001f);
        assertEquals(0.5f, field.getTankMovementProgress(bot), 0.00001f);
    }

    @Test
    public void collectedShotIsRejectedIfEarlierCommandCausesHeavyDamage() {
        Tank player = new Tank(0, 0, Direction.RIGHT, 100, 86);
        Tank bot = new Tank(1, 0, Direction.LEFT);
        GameField field = new GameField(3, 1, player, new Tree[0]);
        field.addTank(bot);
        ControlStrategy shooting = (game, tank) -> new ShootCommand(game, tank);

        new GameLoop(field, new StubInput(false), shooting, shooting, () -> fail("No toggle expected"))
                .update(0f);

        assertEquals(14, bot.getHealth());
        assertEquals(100, player.getHealth());
        assertEquals(2, field.getObjects().size());
    }

    @Test
    public void keyboardStrategyUsesInputPortWithoutLibGdx() {
        Tank tank = new Tank(0, 0, Direction.RIGHT);
        GameField field = new GameField(3, 1, tank, new Tree[0]);
        Command command = new KeyboardControlStrategy(new StubInput(false)).nextCommand(field, tank);
        command.execute();
        assertEquals(1, tank.getX());
    }

    @Test
    public void invalidFrameDoesNotReadInputOrExecuteCommands() {
        Tank tank = new Tank(0, 0, Direction.RIGHT);
        GameField field = new GameField(3, 1, tank, new Tree[0]);
        int[] toggles = {0};
        GameSession session = new GameLoop(field, new StubInput(true),
                (game, receiver) -> new MoveCommand(game, receiver, Direction.RIGHT),
                (game, receiver) -> null, () -> toggles[0]++);
        for (float tick : new float[]{-1f, Float.NaN, Float.POSITIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class, () -> session.update(tick));
        }
        assertEquals(0, tank.getX());
        assertEquals(0, toggles[0]);
    }

    @Test
    public void globalToggleStillWorksAfterPlayerDeath() {
        Tank tank = new Tank(0, 0, Direction.RIGHT);
        GameField field = new GameField(3, 1, tank, new Tree[0]);
        tank.takeDamage(101, field);
        int[] toggles = {0};
        ControlStrategy noCommand = (game, receiver) -> {
            fail("Dead tank must not request a command");
            return null;
        };
        new GameLoop(field, new StubInput(true), noCommand, noCommand, () -> toggles[0]++)
                .update(0.1f);
        assertEquals(1, toggles[0]);
    }

    private static class StubInput implements GameInput {
        private final boolean toggle;

        StubInput(boolean toggle) {
            this.toggle = toggle;
        }

        @Override
        public Direction getPressedDirection() {
            return Direction.RIGHT;
        }

        @Override
        public boolean isShootPressed() {
            return false;
        }

        @Override
        public boolean isHealthBarToggleJustPressed() {
            return toggle;
        }
    }
}
