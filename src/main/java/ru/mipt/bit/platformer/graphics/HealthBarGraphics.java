package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.model.GameField;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.util.TileMovement;

import static com.badlogic.gdx.math.MathUtils.clamp;

public class HealthBarGraphics implements ObjectGraphics {
    private static final float BORDER = 2f;

    private final GameField field;
    private final Tank tank;
    private final Texture pixel;
    private final TileMovement tileMovement;
    private final Rectangle rectangle;
    private final float verticalOffset;
    private final GridPoint2 origin = new GridPoint2();
    private final GridPoint2 destination = new GridPoint2();

    public HealthBarGraphics(TiledMapTileLayer tileLayer, GameField field, Tank tank) {
        this.field = field;
        this.tank = tank;
        tileMovement = new TileMovement(tileLayer, Interpolation.smooth);
        rectangle = new Rectangle(0f, 0f, tileLayer.getTileWidth() * 0.65f, 10f);
        verticalOffset = tileLayer.getTileHeight() * 0.35f;

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(1f, 1f, 1f, 1f);
        pixmap.fill();
        pixel = new Texture(pixmap);
        pixmap.dispose();
    }

    @Override
    public void draw(Batch batch) {
        origin.set(field.getTankOriginX(tank), field.getTankOriginY(tank));
        destination.set(tank.getX(), tank.getY());
        tileMovement.moveRectangleBetweenTileCenters(
                rectangle, origin, destination, field.getTankMovementProgress(tank)
        );
        float x = rectangle.x;
        float y = rectangle.y + verticalOffset;
        float fraction = tank.getMaxHealth() == 0 ? 0f
                : clamp((float) tank.getHealth() / tank.getMaxHealth(), 0f, 1f);

        float previousColor = batch.getPackedColor();
        try {
            batch.setColor(0.15f, 0.15f, 0.15f, 1f);
            batch.draw(pixel, x, y, rectangle.width, rectangle.height);
            if (fraction > 0f) {
                batch.setColor(1f - fraction, fraction, 0.1f, 1f);
                batch.draw(pixel, x + BORDER, y + BORDER,
                        (rectangle.width - 2f * BORDER) * fraction, rectangle.height - 2f * BORDER);
            }
        } finally {
            batch.setPackedColor(previousColor);
        }
    }

    @Override
    public void dispose() {
        pixel.dispose();
    }
}
