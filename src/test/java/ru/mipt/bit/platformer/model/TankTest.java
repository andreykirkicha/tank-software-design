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

    @Test
    public void startsWithFullHealthAndKeepsMaximumAfterDamage() {
        Tank tank = new Tank(0, 0, Direction.RIGHT);
        GameField field = new GameField(2, 1, tank, new Tree[0]);
        assertEquals(100, tank.getHealth());
        assertEquals(100, tank.getMaxHealth());

        tank.takeDamage(30, field);

        assertEquals(70, tank.getHealth());
        assertEquals(100, tank.getMaxHealth());
    }

    @Test
    public void usesConfiguredInitialHealthAsMaximumIncludingZero() {
        Tank tank = new Tank(0, 0, Direction.RIGHT, 80, 50);
        assertEquals(80, tank.getHealth());
        assertEquals(80, tank.getMaxHealth());

        Tank empty = new Tank(0, 0, Direction.RIGHT, 0, 50);
        assertEquals(0, empty.getHealth());
        assertEquals(0, empty.getMaxHealth());
    }
}
