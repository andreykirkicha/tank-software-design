package ru.mipt.bit.platformer.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TankTest {
    @Test
    public void movesInCurrentDirection() {
        Tank tank = new Tank(1, 2, Direction.RIGHT);

        tank.move();

        assertEquals(2, tank.getX());
        assertEquals(2, tank.getY());
    }

    @Test
    public void changesDirection() {
        Tank tank = new Tank(1, 2, Direction.RIGHT);

        tank.setDirection(Direction.UP);

        assertEquals(Direction.UP, tank.getDirection());
        assertEquals(1, tank.getNextX());
        assertEquals(3, tank.getNextY());
    }
}
