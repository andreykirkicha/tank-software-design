package ru.mipt.bit.platformer.model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class GameFieldListenerTest {
    @Test
    public void replaysInitialStateOnceAndNotifiesAboutShortLivedObjects() {
        Tank player = new Tank(0, 0, Direction.RIGHT);
        Tree tree = new Tree(1, 0);
        GameField field = new GameField(3, 1, player, new Tree[]{tree});
        List<GameObject> added = new ArrayList<>();
        List<GameObject> removed = new ArrayList<>();
        GameFieldListener listener = new GameFieldListener() {
            @Override
            public void objectAdded(GameObject object) {
                added.add(object);
            }

            @Override
            public void objectRemoved(GameObject object) {
                removed.add(object);
            }
        };

        field.addListener(listener);
        field.addListener(listener);
        assertEquals(2, added.size());
        assertTrue(added.contains(tree));
        assertTrue(added.contains(player));

        player.shoot(field);
        assertEquals(3, added.size());
        assertEquals(1, removed.size());
        assertSame(added.get(2), removed.get(0));
        field.removeObject(removed.get(0));
        assertEquals(1, removed.size());

        field.removeListener(listener);
        field.removeObject(tree);
        assertEquals(1, removed.size());
        assertTrue(field.isCellFree(1, 0));
        assertEquals(0, field.getObstacles().length);
        assertThrows(UnsupportedOperationException.class, () -> field.getObjects().clear());
    }
}
