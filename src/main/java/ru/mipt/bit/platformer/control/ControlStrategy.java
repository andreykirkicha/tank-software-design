package ru.mipt.bit.platformer.control;

import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.model.GameField;
import ru.mipt.bit.platformer.model.Tank;

/**
 * Application: выходной порт выбора следующей команды танка.
 * Реализации подключают управление игрока или случайный источник действий.
 */
public interface ControlStrategy {
    Command nextCommand(GameField field, Tank tank);
}
