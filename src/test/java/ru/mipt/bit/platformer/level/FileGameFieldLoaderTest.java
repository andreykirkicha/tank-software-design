package ru.mipt.bit.platformer.level;

import org.junit.Test;
import ru.mipt.bit.platformer.model.GameField;
import ru.mipt.bit.platformer.model.Obstacle;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;

public class FileGameFieldLoaderTest {
    @Test
    public void loadsTankAndTreesUsingBottomLeftCoordinates() throws IOException {
        GameField field = new FileGameFieldLoader(new StringReader("T__\n_X_\n__T")).load();

        assertEquals(3, field.getWidth());
        assertEquals(3, field.getHeight());
        assertEquals(1, field.getTank().getX());
        assertEquals(1, field.getTank().getY());
        assertHasObstacle(field, 0, 2);
        assertHasObstacle(field, 2, 0);
    }

    @Test(expected = IOException.class)
    public void rejectsNonRectangularLevel() throws IOException {
        new FileGameFieldLoader(new StringReader("X__\n__")).load();
    }

    @Test(expected = IOException.class)
    public void rejectsLevelWithoutTank() throws IOException {
        new FileGameFieldLoader(new StringReader("T__\n___")).load();
    }

    @Test(expected = IOException.class)
    public void rejectsUnknownCharacters() throws IOException {
        new FileGameFieldLoader(new StringReader("X?_\n___")).load();
    }

    private void assertHasObstacle(GameField field, int x, int y) {
        for (Obstacle obstacle : field.getObstacles()) {
            if (obstacle.getX() == x && obstacle.getY() == y) {
                return;
            }
        }
        throw new AssertionError("No obstacle at (" + x + ", " + y + ")");
    }
}
