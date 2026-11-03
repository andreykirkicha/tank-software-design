package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.model.Bullet;

import static ru.mipt.bit.platformer.util.GdxGameUtils.createBoundingRectangle;
import static ru.mipt.bit.platformer.util.GdxGameUtils.drawTextureRegionUnscaled;
import static ru.mipt.bit.platformer.util.GdxGameUtils.moveRectangleAtTileCenter;

public class BulletGraphics implements ObjectGraphics {
    private final TiledMapTileLayer tileLayer;
    private final Bullet bullet;
    private final Texture texture;
    private final TextureRegion region;
    private final Rectangle rectangle;
    private final GridPoint2 coordinates = new GridPoint2();

    public BulletGraphics(TiledMapTileLayer tileLayer, Bullet bullet) {
        this.tileLayer = tileLayer;
        this.bullet = bullet;
        Pixmap pixmap = new Pixmap(20, 10, Pixmap.Format.RGBA8888);
        pixmap.setColor(1f, 0.85f, 0.1f, 1f);
        pixmap.fill();
        texture = new Texture(pixmap);
        pixmap.dispose();
        region = new TextureRegion(texture);
        rectangle = createBoundingRectangle(region);
    }

    @Override
    public void draw(Batch batch) {
        coordinates.set(bullet.getX(), bullet.getY());
        moveRectangleAtTileCenter(tileLayer, rectangle, coordinates);
        drawTextureRegionUnscaled(batch, region, rectangle, bullet.getDirection().getRotation());
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}
