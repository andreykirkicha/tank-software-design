package ru.mipt.bit.platformer.model;

import org.junit.Test;
import ru.mipt.bit.platformer.command.ShootCommand;
import ru.mipt.bit.platformer.control.TankController;
import ru.mipt.bit.platformer.command.MoveCommand;
import ru.mipt.bit.platformer.model.state.*;

import static org.junit.Assert.*;

public class TankStateTest {
    @Test
    public void selectsStateAtBothHealthBoundaries() {
        int[] health = {100, 71, 70, 69, 16, 15, 14, 1, 0};
        Class<?>[] states = {LightTankState.class, LightTankState.class,
                MediumTankState.class, MediumTankState.class, MediumTankState.class,
                MediumTankState.class, HeavyTankState.class, HeavyTankState.class, HeavyTankState.class};
        for (int i = 0; i < health.length; i++) {
            Tank tank = tankWithRemainingHealth(100, health[i]);
            assertEquals("Health: " + health[i], states[i], tank.getState().getClass());
        }
    }

    @Test
    public void thresholdsUseInitialHealthAndHandleLargeAndZeroValues() {
        Tank tank = tankWithRemainingHealth(200, 140);
        assertTrue(tank.getState() instanceof MediumTankState);
        tank = tankWithRemainingHealth(200, 30);
        assertTrue(tank.getState() instanceof MediumTankState);
        tank = tankWithRemainingHealth(200, 29);
        assertTrue(tank.getState() instanceof HeavyTankState);
        assertTrue(new Tank(0, 0, Direction.RIGHT, 0, 1).getState() instanceof HeavyTankState);
        tank = tankWithRemainingHealth(Integer.MAX_VALUE, Integer.MAX_VALUE - 1);
        assertTrue(tank.getState() instanceof LightTankState);
        tank = tankWithRemainingHealth(101, 71);
        assertTrue(tank.getState() instanceof LightTankState);
        tank = tankWithRemainingHealth(101, 70);
        assertTrue(tank.getState() instanceof MediumTankState);
    }

    @Test
    public void changesStateAfterSuccessiveHitsIncludingSkippedState() {
        Tank tank = new Tank(0, 0, Direction.RIGHT);
        GameField field = field(tank);
        tank.takeDamage(30, field);
        assertTrue(tank.getState() instanceof MediumTankState);
        tank.takeDamage(56, field);
        assertTrue(tank.getState() instanceof HeavyTankState);
        Tank other = tankWithRemainingHealth(100, 10);
        assertTrue(other.getState() instanceof HeavyTankState);
    }

    @Test
    public void eachStateMovesAtItsSpeedAndReservesBothCellsUntilArrival() {
        int[] health = {100, 70, 14};
        float[] durations = {0.4f, 0.8f, 1.2f};
        for (int i = 0; i < health.length; i++) {
            Tank tank = tankWithRemainingHealth(100, health[i]);
            GameField field = field(tank);
            assertTrue(field.moveTank(tank, Direction.RIGHT));
            field.live(durations[i] / 2f);
            assertEquals(0.5f, field.getTankMovementProgress(tank), 0.00001f);
            assertFalse(field.isCellFree(0, 0));
            assertFalse(field.isCellFree(1, 0));
            field.live(durations[i] / 2f);
            assertFalse(field.isTankMoving(tank));
            assertTrue(field.isCellFree(0, 0));
        }
    }

    @Test
    public void damageDuringMovementPreservesProgressAndSlowsRemainingDistance() {
        Tank tank = new Tank(0, 0, Direction.RIGHT);
        GameField field = field(tank);
        field.moveTank(tank, Direction.RIGHT);
        field.live(0.2f);
        tank.takeDamage(30, field);
        assertEquals(0.5f, field.getTankMovementProgress(tank), 0.00001f);
        field.live(0.2f);
        assertEquals(0.75f, field.getTankMovementProgress(tank), 0.00001f);
        tank.takeDamage(56, field);
        assertEquals(0.75f, field.getTankMovementProgress(tank), 0.00001f);
        field.live(0.15f);
        assertEquals(0.875f, field.getTankMovementProgress(tank), 0.00001f);
        field.live(0.15f);
        assertFalse(field.isTankMoving(tank));
    }

    @Test
    public void lightAndMediumStatesShootWithNormalCooldown() {
        for (int health : new int[]{100, 70, 15}) {
            Tank tank = tankWithRemainingHealth(100, health);
            GameField field = field(tank);
            tank.shoot(field);
            assertEquals(2, field.getObjects().size());
            assertFalse(tank.canShoot());
            field.live(0.2f);
            assertFalse(tank.canShoot());
            field.live(0.2f);
            assertTrue(tank.canShoot());
        }
    }

    @Test
    public void heavyStateRejectsDirectAndAlreadyCollectedShotButCanMove() {
        Tank tank = tankWithRemainingHealth(100, 15);
        GameField field = field(tank);
        ShootCommand collected = new ShootCommand(field, tank);
        tank.takeDamage(1, field);
        collected.execute();
        tank.shoot(field);
        assertEquals(1, field.getObjects().size());
        assertFalse(tank.canShoot());
        assertTrue(tank.isReady());
        new TankController(field, tank, (game, receiver) ->
                new MoveCommand(game, receiver, Direction.RIGHT)).update();
        assertTrue(field.isTankMoving(tank));
    }

    @Test
    public void hitByBulletChangesStateAndDisablesShootingImmediately() {
        Tank shooter = new Tank(0, 0, Direction.RIGHT, 100, 86);
        Tank target = new Tank(1, 0, Direction.RIGHT);
        GameField field = field(shooter);
        field.addTank(target);
        shooter.shoot(field);
        assertTrue(target.getState() instanceof HeavyTankState);
        target.shoot(field);
        assertEquals(2, field.getObjects().size());
    }

    @Test
    public void injectedBoosterStateCanDoubleMovementAndFireRateThenExpire() {
        TankState boosted = new TankState() {
            @Override
            public float getMovementDuration() {
                return Tank.MOVEMENT_DURATION / 2f;
            }

            @Override
            public float getShotCooldown() {
                return Tank.SHOT_COOLDOWN / 2f;
            }
        };
        boolean[] active = {true};
        TankStatePolicy normal = new HealthTankStatePolicy();
        Tank tank = new Tank(0, 0, Direction.RIGHT, 100, 50,
                (health, max) -> active[0] ? boosted : normal.select(health, max));
        GameField field = field(tank);
        field.moveTank(tank, Direction.RIGHT);
        field.live(0.1f);
        assertEquals(0.5f, field.getTankMovementProgress(tank), 0.00001f);
        field.live(0.1f);
        assertFalse(field.isTankMoving(tank));
        tank.shoot(field);
        assertFalse(tank.canShoot());
        field.live(0.1f);
        assertFalse(tank.canShoot());
        field.live(0.1f);
        assertTrue(tank.canShoot());
        tank.takeDamage(90, field);
        assertSame(boosted, tank.getState());
        active[0] = false;
        tank.refreshState();
        assertTrue(tank.getState() instanceof HeavyTankState);
        assertFalse(tank.canShoot());
    }

    private Tank tankWithRemainingHealth(int initial, int remaining) {
        Tank tank = new Tank(0, 0, Direction.RIGHT, initial, 50);
        if (initial > remaining) {
            tank.takeDamage(initial - remaining, field(tank));
        }
        return tank;
    }

    private GameField field(Tank tank) {
        return new GameField(8, 1, tank, new Tree[0]);
    }
}
