package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Disposable;
import ru.mipt.bit.platformer.model.Bullet;
import ru.mipt.bit.platformer.model.GameField;
import ru.mipt.bit.platformer.model.GameFieldListener;
import ru.mipt.bit.platformer.model.GameObject;
import ru.mipt.bit.platformer.model.Obstacle;
import ru.mipt.bit.platformer.model.Tank;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class GameFieldGraphics implements GameFieldListener, Disposable {
    private final GameField field;
    private final Function<GameObject, ObjectGraphics> factory;
    private final Map<GameObject, ObjectGraphics> graphics = new LinkedHashMap<>();
    private boolean healthBarsVisible;

    public GameFieldGraphics(GameField field, TiledMapTileLayer tileLayer) {
        this.field = field;
        this.factory = object -> createGraphics(tileLayer, object);
        field.addListener(this);
    }

    public GameFieldGraphics(GameField field, Function<GameObject, ObjectGraphics> factory) {
        this.field = field;
        this.factory = factory;
        field.addListener(this);
    }

    private ObjectGraphics createGraphics(TiledMapTileLayer tileLayer, GameObject object) {
        if (object instanceof Tank) {
            String texturePath = object == field.getTank() ? "images/tank_blue.png" : "images/tank_orange.png";
            Tank tank = (Tank) object;
            return new HealthBarGraphicsDecorator(
                    new TankGraphics(tileLayer, field, tank, texturePath),
                    new HealthBarGraphics(tileLayer, field, tank),
                    this::isHealthBarsVisible
            );
        }
        if (object instanceof Obstacle) {
            return new TreeGraphics(tileLayer, (Obstacle) object, "images/greenTree.png");
        }
        if (object instanceof Bullet) {
            return new BulletGraphics(tileLayer, (Bullet) object);
        }
        throw new IllegalArgumentException("Unknown game object: " + object.getClass().getName());
    }

    public boolean isHealthBarsVisible() {
        return healthBarsVisible;
    }

    public void toggleHealthBars() {
        healthBarsVisible = !healthBarsVisible;
    }

    @Override
    public void objectAdded(GameObject object) {
        graphics.put(object, factory.apply(object));
    }

    @Override
    public void objectRemoved(GameObject object) {
        ObjectGraphics removed = graphics.remove(object);
        if (removed != null) {
            removed.dispose();
        }
    }

    public void draw(Batch batch) {
        for (ObjectGraphics objectGraphics : graphics.values()) {
            objectGraphics.draw(batch);
        }
    }

    @Override
    public void dispose() {
        field.removeListener(this);
        for (ObjectGraphics objectGraphics : graphics.values()) {
            objectGraphics.dispose();
        }
        graphics.clear();
    }
}
