package ru.mipt.bit.platformer.input;

import ru.mipt.bit.platformer.model.Direction;

import java.util.function.IntPredicate;

import static com.badlogic.gdx.Input.Keys.*;

public class InputHandler {
    private final IntPredicate isKeyPressed;

    public InputHandler(IntPredicate isKeyPressed) {
        this.isKeyPressed = isKeyPressed;
    }

    public Direction getPressedDirection() {
        if (isKeyPressed.test(UP) || isKeyPressed.test(W)) {
            return Direction.UP;
        }
        if (isKeyPressed.test(LEFT) || isKeyPressed.test(A)) {
            return Direction.LEFT;
        }
        if (isKeyPressed.test(DOWN) || isKeyPressed.test(S)) {
            return Direction.DOWN;
        }
        if (isKeyPressed.test(RIGHT) || isKeyPressed.test(D)) {
            return Direction.RIGHT;
        }
        return null;
    }
}
