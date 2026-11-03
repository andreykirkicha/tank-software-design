package ru.mipt.bit.platformer.command;

import org.junit.Test;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.GameField;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.Tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MoveCommandTest {
    @Test
    public void movesOnlyItsReceiverAndReservesTheMove() {
        Tank player = new Tank(0, 0, Direction.RIGHT);
        Tank bot = new Tank(1, 1, Direction.RIGHT);
        GameField field = new GameField(3, 3, player, new Tree[0]);
        field.addTank(bot);

        new MoveCommand(field, bot, Direction.UP).execute();

        assertEquals(0, player.getY());
        assertEquals(2, bot.getY());
        assertEquals(Direction.UP, bot.getDirection());
        assertTrue(field.isTankMoving(bot));
    }

    @Test
    public void commandUsesFieldCollisionChecks() {
        Tank tank = new Tank(0, 0, Direction.RIGHT);
        GameField field = new GameField(2, 1, tank, new Tree[]{new Tree(1, 0)});

        new MoveCommand(field, tank, Direction.RIGHT).execute();

        assertEquals(0, tank.getX());
        assertFalse(field.isTankMoving(tank));
    }
}
