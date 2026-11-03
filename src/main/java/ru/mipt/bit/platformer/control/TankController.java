package ru.mipt.bit.platformer.control;

import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.model.GameField;
import ru.mipt.bit.platformer.model.Tank;

public class TankController {
    private final GameField field;
    private final Tank tank;
    private final ControlStrategy strategy;

    public TankController(GameField field, Tank tank, ControlStrategy strategy) {
        this.field = field;
        this.tank = tank;
        this.strategy = strategy;
    }

    public Command nextCommand() {
        if (!field.contains(tank) || field.isTankMoving(tank) || !tank.isReady()) {
            return null;
        }
        return strategy.nextCommand(field, tank);
    }

    public void update() {
        Command command = nextCommand();
        if (command != null) {
            command.execute();
        }
    }
}
