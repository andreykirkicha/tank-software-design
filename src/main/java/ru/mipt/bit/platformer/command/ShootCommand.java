package ru.mipt.bit.platformer.command;

import ru.mipt.bit.platformer.model.GameField;
import ru.mipt.bit.platformer.model.Tank;

public class ShootCommand implements Command {
    private final GameField field;
    private final Tank tank;

    public ShootCommand(GameField field, Tank tank) {
        this.field = field;
        this.tank = tank;
    }

    @Override
    public void execute() {
        tank.shoot(field);
    }
}
