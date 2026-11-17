package ru.mipt.bit.platformer.input;

import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.application.GameInput;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.IntPredicate;

import static com.badlogic.gdx.Input.Keys.SPACE;
import static com.badlogic.gdx.Input.Keys.L;

/**
 * Infrastructure: выходной адаптер порта GameInput для клавиатуры LibGDX.
 * Сопоставляет коды клавиш с действиями и выделяет одиночное нажатие L.
 */
public class InputHandler implements GameInput {
    private final IntPredicate isKeyPressed;
    private final Map<Integer, Direction> controls;
    private boolean healthBarToggleWasPressed;

    public InputHandler(IntPredicate isKeyPressed, Map<Integer, Direction> controls) {
        this.isKeyPressed = isKeyPressed;
        this.controls = new LinkedHashMap<>(controls);
    }

    @Override
    public Direction getPressedDirection() {
        for (Map.Entry<Integer, Direction> control : controls.entrySet()) {
            if (isKeyPressed.test(control.getKey())) {
                return control.getValue();
            }
        }
        return null;
    }

    @Override
    public boolean isShootPressed() {
        return isKeyPressed.test(SPACE);
    }

    @Override
    public boolean isHealthBarToggleJustPressed() {
        boolean pressed = isKeyPressed.test(L);
        boolean justPressed = pressed && !healthBarToggleWasPressed;
        healthBarToggleWasPressed = pressed;
        return justPressed;
    }
}
