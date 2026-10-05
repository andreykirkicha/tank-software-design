package ru.mipt.bit.platformer.input;

import org.junit.Test;
import ru.mipt.bit.platformer.model.Direction;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.badlogic.gdx.Input.Keys.A;
import static com.badlogic.gdx.Input.Keys.UP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class InputHandlerTest {
    @Test
    public void handlesArrowKey() {
        InputHandler handler = new InputHandler(key -> key == UP, controls());

        assertEquals(Direction.UP, handler.getPressedDirection());
    }

    @Test
    public void handlesWasdKey() {
        InputHandler handler = new InputHandler(key -> key == A, controls());

        assertEquals(Direction.LEFT, handler.getPressedDirection());
    }

    @Test
    public void returnsNullWhenMovementKeysAreNotPressed() {
        InputHandler handler = new InputHandler(key -> false, controls());

        assertNull(handler.getPressedDirection());
    }

    private Map<Integer, Direction> controls() {
        Map<Integer, Direction> controls = new LinkedHashMap<>();
        controls.put(UP, Direction.UP);
        controls.put(A, Direction.LEFT);
        return controls;
    }
}
