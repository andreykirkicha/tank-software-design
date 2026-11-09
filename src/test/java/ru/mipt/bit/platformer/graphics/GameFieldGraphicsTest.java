package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import org.junit.Test;
import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.command.ToggleHealthBarsCommand;
import ru.mipt.bit.platformer.model.*;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class GameFieldGraphicsTest {
    @Test
    public void commandTogglesDecoratorsForExistingAndNewTanksAndDisposesBothLayers() {
        Tank player = new Tank(0, 0, Direction.RIGHT);
        Tank bot = new Tank(1, 0, Direction.LEFT);
        Tree tree = new Tree(3, 0);
        GameField field = new GameField(4, 1, player, new Tree[]{tree});
        field.addTank(bot);
        Map<GameObject, RecordingGraphics> sprites = new LinkedHashMap<>();
        Map<GameObject, RecordingGraphics> bars = new LinkedHashMap<>();
        GameFieldGraphics[] receiver = new GameFieldGraphics[1];
        GameFieldGraphics graphics = new GameFieldGraphics(field, object -> {
            RecordingGraphics sprite = new RecordingGraphics();
            sprites.put(object, sprite);
            if (object instanceof Tank) {
                RecordingGraphics bar = new RecordingGraphics();
                bars.put(object, bar);
                return new HealthBarGraphicsDecorator(sprite, bar, () -> receiver[0].isHealthBarsVisible());
            }
            return sprite;
        });
        receiver[0] = graphics;
        Command toggle = new ToggleHealthBarsCommand(graphics);

        assertFalse(graphics.isHealthBarsVisible());
        graphics.draw(null);
        assertEquals(0, bars.get(player).draws);
        assertEquals(0, bars.get(bot).draws);
        assertEquals(1, sprites.get(player).draws);

        toggle.execute();
        assertTrue(graphics.isHealthBarsVisible());
        Tank newcomer = new Tank(2, 0, Direction.LEFT);
        field.addTank(newcomer);
        graphics.draw(null);
        for (RecordingGraphics bar : bars.values()) {
            assertEquals(1, bar.draws);
        }
        assertFalse(bars.containsKey(tree));
        assertEquals(2, sprites.get(tree).draws);

        // Global visibility still changes after the player's tank has been removed.
        field.removeObject(player);
        toggle.execute();
        assertFalse(graphics.isHealthBarsVisible());
        graphics.draw(null);
        for (RecordingGraphics bar : bars.values()) {
            assertEquals(1, bar.draws);
        }
        assertEquals(3, sprites.get(bot).draws);
        assertEquals(1, sprites.get(player).disposals);
        assertEquals(1, bars.get(player).disposals);

        graphics.dispose();
        for (RecordingGraphics sprite : sprites.values()) {
            assertEquals(1, sprite.disposals);
        }
        for (RecordingGraphics bar : bars.values()) {
            assertEquals(1, bar.disposals);
        }
    }

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
