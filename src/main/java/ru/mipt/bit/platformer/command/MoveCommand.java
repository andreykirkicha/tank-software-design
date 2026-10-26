package ru.mipt.bit.platformer.command;

import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.GameField;
import ru.mipt.bit.platformer.model.Movable;

public class MoveCommand implements Command {
    private final GameField field;
    private final Movable tank;
    private final Direction direction;

    public MoveCommand(GameField field, Movable tank, Direction direction) {
        this.field = field;
        this.tank = tank;
        this.direction = direction;
    }

    @Override
    public void execute() {
        field.moveTank(tank, direction);
    }
}
