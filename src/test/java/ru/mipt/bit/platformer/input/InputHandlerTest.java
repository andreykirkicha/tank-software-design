package ru.mipt.bit.platformer.input;

import org.junit.Test;
import ru.mipt.bit.platformer.model.Direction;

import static com.badlogic.gdx.Input.Keys.A;
import static com.badlogic.gdx.Input.Keys.UP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class InputHandlerTest {
    @Test
    public void handlesArrowKey() {
        InputHandler handler = new InputHandler(key -> key == UP);

        assertEquals(Direction.UP, handler.getPressedDirection());
    }

    @Test
    public void handlesWasdKey() {
        InputHandler handler = new InputHandler(key -> key == A);

        assertEquals(Direction.LEFT, handler.getPressedDirection());
    }

    @Test
    public void returnsNullWhenMovementKeysAreNotPressed() {
        InputHandler handler = new InputHandler(key -> false);

        assertNull(handler.getPressedDirection());
    }
}
