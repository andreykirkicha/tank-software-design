package ru.mipt.bit.platformer.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GameFieldTest {
    @Test
    public void movesTankToFreeCell() {
        Tank tank = new Tank(1, 1, Direction.RIGHT);
        GameField field = new GameField(3, 3, tank, new Tree[0]);

        field.moveTank(Direction.UP);

        assertEquals(1, tank.getX());
        assertEquals(2, tank.getY());
        assertEquals(Direction.UP, tank.getDirection());
    }

    @Test
    public void doesNotMoveTankOutsideField() {
        Tank tank = new Tank(0, 0, Direction.RIGHT);
        GameField field = new GameField(3, 3, tank, new Tree[0]);

        field.moveTank(Direction.LEFT);

        assertEquals(0, tank.getX());
        assertEquals(0, tank.getY());
        assertEquals(Direction.LEFT, tank.getDirection());
    }

    @Test
    public void doesNotMoveTankThroughTree() {
        Tank tank = new Tank(0, 0, Direction.RIGHT);
        GameField field = new GameField(3, 3, tank, new Tree[]{new Tree(1, 0)});

        field.moveTank(Direction.RIGHT);

        assertEquals(0, tank.getX());
        assertEquals(0, tank.getY());
    }

    @Test
    public void doesNotMoveTankThroughAnyObstacleImplementation() {
        Tank tank = new Tank(0, 0, Direction.RIGHT);
        Obstacle obstacle = new Obstacle() {
            @Override
            public int getX() {
                return 1;
            }

            @Override
            public int getY() {
                return 0;
            }
        };
        GameField field = new GameField(3, 3, tank, new Obstacle[]{obstacle});

        field.moveTank(Direction.RIGHT);

        assertEquals(0, tank.getX());
        assertEquals(0, tank.getY());
    }
}
