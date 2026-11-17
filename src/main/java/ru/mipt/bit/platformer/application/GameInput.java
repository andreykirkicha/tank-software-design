package ru.mipt.bit.platformer.application;

import ru.mipt.bit.platformer.model.Direction;

/** Application: выходной порт опроса ввода без зависимости от клавиш и LibGDX. */
public interface GameInput {
    Direction getPressedDirection();

    boolean isShootPressed();

    boolean isHealthBarToggleJustPressed();
}
