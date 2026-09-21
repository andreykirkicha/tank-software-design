package ru.mipt.bit.platformer.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DirectionTest {
    @Test
    public void storesMovementAndRotation() {
        assertEquals(0, Direction.UP.getDeltaX());
        assertEquals(1, Direction.UP.getDeltaY());
        assertEquals(90f, Direction.UP.getRotation(), 0f);

        assertEquals(-1, Direction.LEFT.getDeltaX());
        assertEquals(-180f, Direction.LEFT.getRotation(), 0f);
        assertEquals(-1, Direction.DOWN.getDeltaY());
        assertEquals(0f, Direction.RIGHT.getRotation(), 0f);
    }
}
