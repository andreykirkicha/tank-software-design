package ru.mipt.bit.platformer.control;

import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.model.GameField;
import ru.mipt.bit.platformer.model.Movable;

public interface ControlStrategy {
    Command nextCommand(GameField field, Movable tank);
}
