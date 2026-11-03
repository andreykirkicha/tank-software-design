package ru.mipt.bit.platformer.model;

import org.junit.Test;
import ru.mipt.bit.platformer.command.MoveCommand;
import ru.mipt.bit.platformer.command.ShootCommand;
import ru.mipt.bit.platformer.control.TankController;

import static org.junit.Assert.*;

public class ShootingTest {
    @Test
    public void spawnsAndMovesBulletInEveryOrientation() {
        for (Direction direction : Direction.values()) {
            Tank tank = new Tank(2, 2, direction);
            GameField field = new GameField(5, 5, tank, new Tree[0]);
            ShootCommand command = new ShootCommand(field, tank);
            assertEquals(1, field.getObjects().size());

            command.execute();

            Bullet bullet = bulletIn(field);
            assertEquals(2 + direction.getDeltaX(), bullet.getX());
            assertEquals(2 + direction.getDeltaY(), bullet.getY());
            assertEquals(direction, bullet.getDirection());
            assertEquals(Tank.DEFAULT_BULLET_DAMAGE, bullet.getDamage());

            tank.setDirection(Direction.LEFT);
            field.live(Bullet.STEP_DURATION);
            assertEquals(2 + 2 * direction.getDeltaX(), bullet.getX());
            assertEquals(2 + 2 * direction.getDeltaY(), bullet.getY());
            assertTrue(field.contains(bullet));
        }
    }

    @Test
    public void hitsAdjacentTankAtSpawnUsingConfiguredDamage() {
        Tank shooter = new Tank(0, 0, Direction.RIGHT, 100, 17);
        Tank target = new Tank(1, 0, Direction.LEFT);
        GameField field = new GameField(3, 1, shooter, new Tree[0]);
        field.addTank(target);

        shooter.shoot(field);

        assertEquals(83, target.getHealth());
        assertEquals(100, shooter.getHealth());
        assertEquals(2, field.getObjects().size());
    }

    @Test
    public void removesTankOnlyWhenHealthBecomesNegative() {
        Tank shooter = new Tank(0, 0, Direction.RIGHT);
        Tank target = new Tank(1, 0, Direction.LEFT, 50, 50);
        GameField field = new GameField(3, 1, shooter, new Tree[0]);
        field.addTank(target);

        shooter.shoot(field);
        assertEquals(0, target.getHealth());
        assertTrue(field.contains(target));
        field.live(Tank.SHOT_COOLDOWN);
        shooter.shoot(field);

        assertEquals(-50, target.getHealth());
        assertFalse(field.contains(target));
        assertFalse(field.getTanks().contains(target));
        assertTrue(field.isCellFree(1, 0));
    }

    @Test
    public void treeStopsBulletAtSpawnAndDuringFlight() {
        for (int treeX = 1; treeX <= 3; treeX++) {
            Tank shooter = new Tank(0, 0, Direction.RIGHT);
            Tree tree = new Tree(treeX, 0);
            Tank target = new Tank(4, 0, Direction.LEFT);
            GameField field = new GameField(5, 1, shooter, new Tree[]{tree});
            field.addTank(target);

            shooter.shoot(field);
            field.live(1f);

            assertEquals(3, field.getObjects().size());
            assertTrue(field.contains(tree));
            assertEquals(100, target.getHealth());
        }
    }

    @Test
    public void leavingAnyBoundaryRemovesBulletIncludingAtSpawn() {
        for (Direction direction : Direction.values()) {
            Tank shooter = new Tank(1, 1, direction);
            GameField field = new GameField(3, 3, shooter, new Tree[0]);
            shooter.shoot(field);
            field.live(Bullet.STEP_DURATION);
            assertEquals(1, field.getObjects().size());

            GameField tiny = new GameField(1, 1, new Tank(0, 0, direction), new Tree[0]);
            tiny.getTank().shoot(tiny);
            assertEquals(1, tiny.getObjects().size());
        }
    }

    @Test
    public void collidingBulletsBothDisappearAtSpawn() {
        Tank shooter = new Tank(0, 0, Direction.RIGHT);
        Tank opponent = new Tank(2, 0, Direction.LEFT);
        GameField field = new GameField(3, 1, shooter, new Tree[0]);
        field.addTank(opponent);
        shooter.shoot(field);
        opponent.shoot(field);

        assertEquals(2, field.getObjects().size());
        assertEquals(100, shooter.getHealth());
        assertEquals(100, opponent.getHealth());
    }

    @Test
    public void approachingBulletsCannotPassEachOtherDuringLongTick() {
        for (int separation : new int[]{5, 6}) {
            Tank shooter = new Tank(0, 0, Direction.RIGHT);
            Tank opponent = new Tank(separation, 0, Direction.LEFT);
            GameField field = new GameField(separation + 1, 1, shooter, new Tree[0]);
            field.addTank(opponent);
            shooter.shoot(field);
            opponent.shoot(field);

            field.live(1f);

            assertEquals(2, field.getObjects().size());
            assertEquals(100, shooter.getHealth());
            assertEquals(100, opponent.getHealth());
        }
    }

    @Test
    public void longTickDoesNotSkipTankOrDamageTheNextOne() {
        Tank shooter = new Tank(0, 0, Direction.RIGHT);
        Tank target = new Tank(3, 0, Direction.LEFT);
        Tank behind = new Tank(4, 0, Direction.LEFT);
        GameField field = new GameField(6, 1, shooter, new Tree[0]);
        field.addTank(target);
        field.addTank(behind);
        shooter.shoot(field);

        field.live(1f);

        assertEquals(50, target.getHealth());
        assertEquals(100, behind.getHealth());
        assertEquals(3, field.getObjects().size());
    }

    @Test
    public void deathDuringMovementReleasesOriginAndDestination() {
        Tank shooter = new Tank(0, 0, Direction.RIGHT);
        Tank target = new Tank(2, 0, Direction.RIGHT, 0, 50);
        GameField field = new GameField(5, 1, shooter, new Tree[0]);
        field.addTank(target);
        field.moveTank(target, Direction.RIGHT);
        shooter.shoot(field);

        field.live(Bullet.STEP_DURATION);

        assertFalse(field.contains(target));
        assertFalse(field.isTankMoving(target));
        assertTrue(field.isCellFree(2, 0));
        assertTrue(field.isCellFree(3, 0));
    }

    @Test
    public void movingIntoBulletHitsTankImmediately() {
        Tank shooter = new Tank(0, 0, Direction.RIGHT);
        Tank target = new Tank(1, 1, Direction.DOWN);
        GameField field = new GameField(3, 2, shooter, new Tree[0]);
        field.addTank(target);
        shooter.shoot(field);

        field.moveTank(target, Direction.DOWN);

        assertEquals(50, target.getHealth());
        assertEquals(2, field.getObjects().size());
    }

    @Test
    public void movementAndReloadFinishWithoutGraphics() {
        Tank tank = new Tank(1, 1, Direction.RIGHT);
        GameField field = new GameField(6, 3, tank, new Tree[0]);
        field.moveTank(Direction.RIGHT);
        tank.shoot(field);
        assertEquals(1, field.getObjects().size());
        field.live(0.2f);
        assertEquals(0.5f, field.getTankMovementProgress(tank), 0.00001f);
        assertFalse(field.isCellFree(1, 1));
        field.live(0.2f);
        assertFalse(field.isTankMoving(tank));
        assertTrue(field.isCellFree(1, 1));

        tank.shoot(field);
        Bullet first = bulletIn(field);
        tank.shoot(field);
        assertEquals(2, field.getObjects().size());
        assertSame(first, bulletIn(field));
        field.live(Tank.SHOT_COOLDOWN);
        assertTrue(tank.isReady());
        tank.shoot(field);
        assertEquals(2, field.getObjects().size());
    }

    @Test
    public void commandsCollectedBeforeDeathAndDeadControllerDoNothing() {
        Tank player = new Tank(0, 0, Direction.RIGHT);
        Tank target = new Tank(1, 0, Direction.LEFT, 0, 50);
        GameField field = new GameField(3, 1, player, new Tree[0]);
        field.addTank(target);
        MoveCommand move = new MoveCommand(field, target, Direction.RIGHT);
        ShootCommand shoot = new ShootCommand(field, target);
        TankController controller = new TankController(field, target, (game, tank) -> {
            fail("Dead tank must not request commands");
            return null;
        });
        player.shoot(field);

        move.execute();
        shoot.execute();
        controller.update();

        assertEquals(1, field.getObjects().size());
        assertEquals(1, target.getX());
    }

    @Test
    public void rejectsInvalidTimeSteps() {
        GameField field = new GameField(1, 1, new Tank(0, 0, Direction.RIGHT), new Tree[0]);
        for (float tick : new float[]{-1f, Float.NaN, Float.POSITIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class, () -> field.live(tick));
        }
        field.live(0f);
        assertEquals(1, field.getObjects().size());
    }

    private Bullet bulletIn(GameField field) {
        return (Bullet) field.getObjects().stream().filter(object -> object instanceof Bullet)
                .findFirst().get();
    }
}
