package ru.mipt.bit.platformer.control;

import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.command.MoveCommand;
import ru.mipt.bit.platformer.command.ShootCommand;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.GameField;
import ru.mipt.bit.platformer.model.Tank;

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
    public Command nextCommand(GameField field, Tank tank) {
        Direction[] directions = Direction.values();
        int action = random.nextInt(directions.length + 1);
        return action == directions.length
                ? new ShootCommand(field, tank) : new MoveCommand(field, tank, directions[action]);
    }
}
