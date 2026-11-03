package ru.mipt.bit.platformer.control;

import org.junit.Test;
import ru.mipt.bit.platformer.command.MoveCommand;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.GameField;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.Tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TankControllerTest {
    @Test
    public void requestsNewCommandOnlyAfterPreviousMoveFinishes() {
        Tank tank = new Tank(0, 0, Direction.RIGHT);
        GameField field = new GameField(4, 1, tank, new Tree[0]);
        int[] requests = {0};
        TankController controller = new TankController(field, tank, (game, receiver) -> {
            requests[0]++;
            return new MoveCommand(game, receiver, Direction.RIGHT);
        });

        controller.update();
        controller.update();
        controller.update();

        assertEquals(1, tank.getX());
        assertEquals(1, requests[0]);
        field.finishTankMove(tank);
        controller.update();
        assertEquals(2, tank.getX());
        assertEquals(2, requests[0]);
    }

    @Test
    public void retriesAfterBlockedDirection() {
        Tank tank = new Tank(0, 0, Direction.RIGHT);
        GameField field = new GameField(2, 1, tank, new Tree[0]);
        int[] requests = {0};
        TankController controller = new TankController(field, tank, (game, receiver) ->
                new MoveCommand(game, receiver, requests[0]++ == 0 ? Direction.LEFT : Direction.RIGHT));

        controller.update();
        assertFalse(field.isTankMoving(tank));
        controller.update();
        assertEquals(1, tank.getX());
        assertTrue(field.isTankMoving(tank));
    }

    @Test
    public void missingCommandLeavesTankIdle() {
        Tank tank = new Tank(0, 0, Direction.RIGHT);
        GameField field = new GameField(2, 1, tank, new Tree[0]);

        new TankController(field, tank, (game, receiver) -> null).update();

        assertEquals(0, tank.getX());
        assertFalse(field.isTankMoving(tank));
    }
}
