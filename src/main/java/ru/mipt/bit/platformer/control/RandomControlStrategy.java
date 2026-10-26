package ru.mipt.bit.platformer.control;

import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.command.MoveCommand;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.GameField;
import ru.mipt.bit.platformer.model.Movable;

import java.util.Random;

public class RandomControlStrategy implements ControlStrategy {
    private final Random random;

    public RandomControlStrategy() {
        this(new Random());
    }

    public RandomControlStrategy(Random random) {
        this.random = random;
    }

    @Override
    public Command nextCommand(GameField field, Movable tank) {
        Direction[] directions = Direction.values();
        return new MoveCommand(field, tank, directions[random.nextInt(directions.length)]);
    }
}
