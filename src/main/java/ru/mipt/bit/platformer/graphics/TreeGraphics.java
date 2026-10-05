package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.model.Obstacle;

import static ru.mipt.bit.platformer.util.GdxGameUtils.createBoundingRectangle;
import static ru.mipt.bit.platformer.util.GdxGameUtils.drawTextureRegionUnscaled;
import static ru.mipt.bit.platformer.util.GdxGameUtils.moveRectangleAtTileCenter;

public class TreeGraphics {
    private final Texture texture;
    private final TextureRegion region;
    private final Rectangle rectangle;

    public TreeGraphics(TiledMapTileLayer tileLayer, Obstacle tree, String texturePath) {
        texture = new Texture(texturePath);
        region = new TextureRegion(texture);
        rectangle = createBoundingRectangle(region);
        moveRectangleAtTileCenter(
                tileLayer,
                rectangle,
                new GridPoint2(tree.getX(), tree.getY())
        );
    }

    public void draw(Batch batch) {
        drawTextureRegionUnscaled(batch, region, rectangle, 0f);
    }

    public void dispose() {
        texture.dispose();
    }
}
