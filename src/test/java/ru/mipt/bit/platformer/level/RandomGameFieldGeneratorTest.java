package ru.mipt.bit.platformer.level;

import org.junit.Test;
import ru.mipt.bit.platformer.model.GameField;
import ru.mipt.bit.platformer.model.Obstacle;

import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RandomGameFieldGeneratorTest {
    @Test
    public void placesRequestedNumberOfTreesWithoutOverlaps() throws IOException {
        GameField field = new RandomGameFieldGenerator(5, 4, 8, new Random(42)).load();

        assertEquals(5, field.getWidth());
        assertEquals(4, field.getHeight());
        assertEquals(8, field.getObstacles().length);

        Set<String> occupied = new HashSet<>();
        occupied.add(key(field.getTank().getX(), field.getTank().getY()));
        for (Obstacle obstacle : field.getObstacles()) {
            assertTrue(obstacle.getX() >= 0 && obstacle.getX() < field.getWidth());
            assertTrue(obstacle.getY() >= 0 && obstacle.getY() < field.getHeight());
            assertFalse(occupied.contains(key(obstacle.getX(), obstacle.getY())));
            occupied.add(key(obstacle.getX(), obstacle.getY()));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void leavesAtLeastOneCellForTank() {
        new RandomGameFieldGenerator(2, 2, 4);
    }

    private String key(int x, int y) {
        return x + ":" + y;
    }
}
