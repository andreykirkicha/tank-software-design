package ru.mipt.bit.platformer.input;

import ru.mipt.bit.platformer.model.Direction;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.IntPredicate;

import static com.badlogic.gdx.Input.Keys.SPACE;

public class InputHandler {
    private final IntPredicate isKeyPressed;
    private final Map<Integer, Direction> controls;

    public InputHandler(IntPredicate isKeyPressed, Map<Integer, Direction> controls) {
        this.isKeyPressed = isKeyPressed;
        this.controls = new LinkedHashMap<>(controls);
    }

    public Direction getPressedDirection() {
        for (Map.Entry<Integer, Direction> control : controls.entrySet()) {
            if (isKeyPressed.test(control.getKey())) {
                return control.getValue();
            }
        }
        return null;
    }

    public boolean isShootPressed() {
        return isKeyPressed.test(SPACE);
    }
}
