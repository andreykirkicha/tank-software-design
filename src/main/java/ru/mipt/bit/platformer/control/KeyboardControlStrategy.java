package ru.mipt.bit.platformer.control;

import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.command.MoveCommand;
import ru.mipt.bit.platformer.command.ShootCommand;
import ru.mipt.bit.platformer.application.GameInput;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.GameField;
import ru.mipt.bit.platformer.model.Tank;

/**
 * Infrastructure: адаптер порта ControlStrategy для управления игроком.
 * Преобразует данные GameInput в команды, не обращаясь к LibGDX напрямую.
 */
public class KeyboardControlStrategy implements ControlStrategy {
    private final GameInput inputHandler;

    public KeyboardControlStrategy(GameInput inputHandler) {
        this.inputHandler = inputHandler;
    }

    @Override
    public Command nextCommand(GameField field, Tank tank) {
        if (inputHandler.isShootPressed()) {
            return new ShootCommand(field, tank);
        }
        Direction direction = inputHandler.getPressedDirection();
        return direction == null ? null : new MoveCommand(field, tank, direction);
    }
}
