package ru.mipt.bit.platformer.input;

import org.junit.Test;
import ru.mipt.bit.platformer.model.Direction;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

import static com.badlogic.gdx.Input.Keys.A;
import static com.badlogic.gdx.Input.Keys.UP;
import static com.badlogic.gdx.Input.Keys.L;
import static com.badlogic.gdx.Input.Keys.SPACE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void togglesOnlyOnNewPressWhileMovementAndShootingStayAvailable() {
        Set<Integer> pressed = new HashSet<>();
        InputHandler handler = new InputHandler(pressed::contains, controls());
        assertFalse(handler.isHealthBarToggleJustPressed());

        pressed.add(L);
        pressed.add(UP);
        pressed.add(SPACE);
        assertTrue(handler.isHealthBarToggleJustPressed());
        assertEquals(Direction.UP, handler.getPressedDirection());
        assertTrue(handler.isShootPressed());
        for (int frame = 0; frame < 5; frame++) {
            assertFalse(handler.isHealthBarToggleJustPressed());
        }

        pressed.remove(L);
        assertFalse(handler.isHealthBarToggleJustPressed());
        pressed.add(L);
        assertTrue(handler.isHealthBarToggleJustPressed());
        assertFalse(handler.isHealthBarToggleJustPressed());
    }

    private Map<Integer, Direction> controls() {
        Map<Integer, Direction> controls = new LinkedHashMap<>();
        controls.put(UP, Direction.UP);
        controls.put(A, Direction.LEFT);
        return controls;
    }
}
