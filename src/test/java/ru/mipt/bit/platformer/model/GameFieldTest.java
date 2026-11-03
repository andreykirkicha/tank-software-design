package ru.mipt.bit.platformer.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThrows;

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

    @Test
    public void blocksAllFourBoundariesForEveryTank() {
        for (Direction direction : Direction.values()) {
            Tank player = new Tank(1, 1, Direction.RIGHT);
            Tank bot = new Tank(1 + direction.getDeltaX(), 1 + direction.getDeltaY(), Direction.RIGHT);
            GameField field = new GameField(3, 3, player, new Tree[0]);
            field.addTank(bot);

            assertFalse(field.moveTank(bot, direction));
            assertEquals(1 + direction.getDeltaX(), bot.getX());
            assertEquals(1 + direction.getDeltaY(), bot.getY());
            assertEquals(direction, bot.getDirection());
            assertFalse(field.isTankMoving(bot));
        }
    }

    @Test
    public void movingTankReservesBothCellsUntilArrival() {
        Tank player = new Tank(1, 1, Direction.RIGHT);
        Tank follower = new Tank(0, 1, Direction.RIGHT);
        Tank opponent = new Tank(3, 1, Direction.LEFT);
        GameField field = new GameField(4, 3, player, new Tree[0]);
        field.addTank(follower);
        field.addTank(opponent);

        assertTrue(field.moveTank(player, Direction.RIGHT));
        assertTrue(field.isTankMoving(player));
        assertFalse(field.isCellFree(1, 1));
        assertFalse(field.isCellFree(2, 1));
        assertFalse(field.moveTank(follower, Direction.RIGHT));
        assertFalse(field.moveTank(opponent, Direction.LEFT));

        field.finishTankMove(player);

        assertFalse(field.isTankMoving(player));
        assertTrue(field.isCellFree(1, 1));
        assertFalse(field.isCellFree(2, 1));
        assertTrue(field.moveTank(follower, Direction.RIGHT));
        assertFalse(field.moveTank(opponent, Direction.LEFT));
    }

    @Test
    public void doesNotChangeDirectionOrStartAnotherStepWhileMoving() {
        Tank tank = new Tank(1, 1, Direction.RIGHT);
        GameField field = new GameField(4, 4, tank, new Tree[0]);
        field.moveTank(Direction.RIGHT);

        assertFalse(field.moveTank(tank, Direction.UP));
        assertEquals(2, tank.getX());
        assertEquals(1, tank.getY());
        assertEquals(Direction.RIGHT, tank.getDirection());

        field.finishTankMove(tank);
        assertTrue(field.moveTank(tank, Direction.UP));
        assertEquals(2, tank.getY());
    }

    @Test
    public void onlyOneTankCanReserveSharedDestination() {
        Tank player = new Tank(0, 0, Direction.RIGHT);
        Tank bot = new Tank(2, 0, Direction.LEFT);
        GameField field = new GameField(3, 1, player, new Tree[0]);
        field.addTank(bot);

        assertTrue(field.moveTank(player, Direction.RIGHT));
        assertFalse(field.moveTank(bot, Direction.LEFT));
        assertEquals(2, bot.getX());
    }

    @Test
    public void adjacentTanksCannotSwapCells() {
        Tank player = new Tank(0, 0, Direction.RIGHT);
        Tank bot = new Tank(1, 0, Direction.LEFT);
        GameField field = new GameField(2, 1, player, new Tree[0]);
        field.addTank(bot);

        assertFalse(field.moveTank(player, Direction.RIGHT));
        assertFalse(field.moveTank(bot, Direction.LEFT));
    }

    @Test
    public void independentTanksCanMoveAtTheSameTime() {
        Tank player = new Tank(0, 0, Direction.RIGHT);
        Tank bot = new Tank(0, 1, Direction.RIGHT);
        GameField field = new GameField(3, 2, player, new Tree[0]);
        field.addTank(bot);

        assertTrue(field.moveTank(player, Direction.RIGHT));
        assertTrue(field.moveTank(bot, Direction.RIGHT));
        field.finishTankMove(player);

        assertTrue(field.isCellFree(0, 0));
        assertFalse(field.isCellFree(0, 1));
        assertTrue(field.isTankMoving(bot));
    }

    @Test
    public void rejectsPlacementOnOccupiedReservedOrOutsideCells() {
        Tank tank = new Tank(0, 0, Direction.RIGHT);
        GameField field = new GameField(3, 2, tank, new Tree[]{new Tree(2, 1)});
        field.moveTank(Direction.RIGHT);

        assertThrows(IllegalArgumentException.class, () -> field.addTank(new Tank(0, 0, Direction.UP)));
        assertThrows(IllegalArgumentException.class, () -> field.addTank(new Tank(1, 0, Direction.UP)));
        assertThrows(IllegalArgumentException.class, () -> field.addTank(new Tank(2, 1, Direction.UP)));
        assertThrows(IllegalArgumentException.class, () -> field.addTank(new Tank(-1, 0, Direction.UP)));
        assertThrows(IllegalArgumentException.class, () -> field.addTank(tank));
        assertEquals(1, field.getTanks().size());
    }

    @Test
    public void keepsPlayerAndPreventsExternalChangesToTankList() {
        Tank player = new Tank(0, 0, Direction.RIGHT);
        GameField field = new GameField(3, 3, player, new Tree[0]);
        field.addTank(new Tank(2, 2, Direction.LEFT));

        assertSame(player, field.getTank());
        assertSame(player, field.getTanks().get(0));
        assertThrows(UnsupportedOperationException.class, () -> field.getTanks().clear());
    }

    @Test
    public void rejectsCommandsForForeignTank() {
        Tank player = new Tank(0, 0, Direction.RIGHT);
        GameField field = new GameField(3, 3, player, new Tree[0]);
        Tank foreign = new Tank(1, 1, Direction.UP);

        assertThrows(IllegalArgumentException.class, () -> field.moveTank(foreign, Direction.RIGHT));
        assertThrows(IllegalArgumentException.class, () -> field.finishTankMove(foreign));
    }
}
