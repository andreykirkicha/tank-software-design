package ru.mipt.bit.platformer.level;

import org.junit.Test;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.GameField;
import ru.mipt.bit.platformer.model.Movable;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.Tree;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class RandomTankGeneratorTest {
    @Test
    public void fillsAllAvailableCellsWithoutOverlapsAndKeepsPlayer() {
        for (int seed = 0; seed < 20; seed++) {
            Tank player = new Tank(1, 1, Direction.UP);
            GameField field = new GameField(4, 3, player, new Tree[]{new Tree(0, 0), new Tree(2, 2)});

            new RandomTankGenerator(new Random(seed)).generate(field, 9);

            assertSame(player, field.getTank());
            assertEquals(10, field.getTanks().size());
            Set<String> occupied = new HashSet<>();
            occupied.add("0:0");
            occupied.add("2:2");
            for (Movable tank : field.getTanks()) {
                assertTrue(tank.getX() >= 0 && tank.getX() < field.getWidth());
                assertTrue(tank.getY() >= 0 && tank.getY() < field.getHeight());
                assertTrue(occupied.add(tank.getX() + ":" + tank.getY()));
            }
        }
    }

    @Test
    public void sameSeedProducesSameTanks() {
        GameField first = emptyField();
        GameField second = emptyField();

        new RandomTankGenerator(new Random(42)).generate(first, 4);
        new RandomTankGenerator(new Random(42)).generate(second, 4);

        for (int i = 0; i < first.getTanks().size(); i++) {
            Movable left = first.getTanks().get(i);
            Movable right = second.getTanks().get(i);
            assertEquals(left.getX(), right.getX());
            assertEquals(left.getY(), right.getY());
            assertEquals(left.getDirection(), right.getDirection());
        }
    }

    @Test
    public void skipsBothReservedCellsOfMovingTank() {
        GameField field = emptyField();
        field.moveTank(Direction.RIGHT);

        new RandomTankGenerator(new Random(42)).generate(field, 7);

        assertEquals(8, field.getTanks().size());
        for (int i = 1; i < field.getTanks().size(); i++) {
            Movable tank = field.getTanks().get(i);
            assertFalse(tank.getX() == 0 && tank.getY() == 0);
            assertFalse(tank.getX() == 1 && tank.getY() == 0);
        }
    }

    @Test
    public void rejectsTooManyTanksWithoutPartiallyChangingField() {
        GameField field = emptyField();

        assertThrows(IllegalArgumentException.class,
                () -> new RandomTankGenerator(new Random(42)).generate(field, 9));
        assertEquals(1, field.getTanks().size());
    }

    @Test
    public void rejectsNegativeCount() {
        assertThrows(IllegalArgumentException.class,
                () -> new RandomTankGenerator().generate(emptyField(), -1));
    }

    @Test
    public void zeroTanksWorksEvenOnFullField() {
        GameField field = new GameField(1, 1, new Tank(0, 0, Direction.RIGHT), new Tree[0]);

        new RandomTankGenerator().generate(field, 0);

        assertEquals(1, field.getTanks().size());
        assertThrows(IllegalArgumentException.class, () -> new RandomTankGenerator().generate(field, 1));
    }

    private GameField emptyField() {
        return new GameField(3, 3, new Tank(0, 0, Direction.RIGHT), new Tree[0]);
    }
}
