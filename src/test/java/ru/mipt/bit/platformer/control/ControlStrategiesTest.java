package ru.mipt.bit.platformer.control;

import org.junit.Test;
import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.input.InputHandler;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.GameField;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.Tree;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Random;
import java.util.Set;

import static com.badlogic.gdx.Input.Keys.W;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ControlStrategiesTest {
    @Test
    public void keyboardCreatesCommandWithoutMovingUntilExecution() {
        Tank tank = new Tank(1, 1, Direction.RIGHT);
        GameField field = new GameField(3, 3, tank, new Tree[0]);
        InputHandler input = new InputHandler(key -> key == W, Collections.singletonMap(W, Direction.UP));
        ControlStrategy strategy = new KeyboardControlStrategy(input);

        Command command = strategy.nextCommand(field, tank);
        assertEquals(1, tank.getY());
        command.execute();

        assertEquals(2, tank.getY());
        assertEquals(Direction.UP, tank.getDirection());
    }

    @Test
    public void keyboardDoesNothingWithoutPressedKeys() {
        Tank tank = new Tank(1, 1, Direction.RIGHT);
        GameField field = new GameField(3, 3, tank, new Tree[0]);
        InputHandler input = new InputHandler(key -> false, Collections.singletonMap(W, Direction.UP));

        assertNull(new KeyboardControlStrategy(input).nextCommand(field, tank));
    }

    @Test
    public void randomStrategyChoosesAllFourDirectionsUsingProvidedRandom() {
        Random expected = new Random(42);
        ControlStrategy strategy = new RandomControlStrategy(new Random(42));
        Set<Direction> chosen = EnumSet.noneOf(Direction.class);
        for (int i = 0; i < 40; i++) {
            Tank tank = new Tank(1, 1, Direction.RIGHT);
            GameField field = new GameField(3, 3, tank, new Tree[0]);
            Direction direction = Direction.values()[expected.nextInt(4)];

            Command command = strategy.nextCommand(field, tank);
            assertEquals(1, tank.getX());
            assertEquals(1, tank.getY());
            command.execute();

            assertEquals(direction, tank.getDirection());
            assertEquals(1 + direction.getDeltaX(), tank.getX());
            assertEquals(1 + direction.getDeltaY(), tank.getY());
            chosen.add(direction);
        }
        assertEquals(EnumSet.allOf(Direction.class), chosen);
    }
}
