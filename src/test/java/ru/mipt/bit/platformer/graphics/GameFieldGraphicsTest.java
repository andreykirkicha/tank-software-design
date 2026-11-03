package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import org.junit.Test;
import ru.mipt.bit.platformer.model.*;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class GameFieldGraphicsTest {
    @Test
    public void observesInitialObjectsSpawningAndDeathAndDisposesRemovedGraphics() {
        Tank player = new Tank(0, 0, Direction.RIGHT);
        Tree tree = new Tree(3, 0);
        GameField field = new GameField(4, 1, player, new Tree[]{tree});
        Map<GameObject, RecordingGraphics> created = new LinkedHashMap<>();
        GameFieldGraphics graphics = new GameFieldGraphics(field, object -> {
            RecordingGraphics result = new RecordingGraphics();
            assertNull(created.put(object, result));
            return result;
        });
        assertEquals(2, created.size());
        assertTrue(created.containsKey(player));
        assertTrue(created.containsKey(tree));

        Tank target = new Tank(2, 0, Direction.LEFT, 0, 50);
        field.addTank(target);
        player.shoot(field);
        Bullet bullet = (Bullet) field.getObjects().stream().filter(object -> object instanceof Bullet)
                .findFirst().get();
        graphics.draw(null);
        assertEquals(4, created.size());
        assertEquals(1, created.get(bullet).draws);
        assertEquals(1, created.get(target).draws);

        field.live(Bullet.STEP_DURATION);
        graphics.draw(null);

        assertEquals(1, created.get(bullet).disposals);
        assertEquals(1, created.get(target).disposals);
        assertEquals(1, created.get(bullet).draws);
        assertEquals(1, created.get(target).draws);
        assertEquals(2, created.get(player).draws);
        assertEquals(2, created.get(tree).draws);

        graphics.dispose();
        for (RecordingGraphics objectGraphics : created.values()) {
            assertEquals(1, objectGraphics.disposals);
        }
        field.addBullet(new Bullet(1, 0, Direction.RIGHT, 1));
        assertEquals(4, created.size());
    }

    private static class RecordingGraphics implements ObjectGraphics {
        private int draws;
        private int disposals;

        @Override
        public void draw(Batch batch) {
            draws++;
        }

        @Override
        public void dispose() {
            disposals++;
        }
    }
}
