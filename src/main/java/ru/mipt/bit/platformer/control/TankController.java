package ru.mipt.bit.platformer.control;

import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.model.GameField;
import ru.mipt.bit.platformer.model.Movable;

public class TankController {
    private final GameField field;
    private final Movable tank;
    private final ControlStrategy strategy;

    public TankController(GameField field, Movable tank, ControlStrategy strategy) {
        this.field = field;
        this.tank = tank;
        this.strategy = strategy;
    }

    public void update() {
        if (field.isTankMoving(tank)) {
            return;
        }
        Command command = strategy.nextCommand(field, tank);
        if (command != null) {
            command.execute();
        }
    }
}
