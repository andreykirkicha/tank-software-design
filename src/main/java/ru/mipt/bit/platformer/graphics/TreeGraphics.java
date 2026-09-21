package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.model.Tree;

import static ru.mipt.bit.platformer.util.GdxGameUtils.createBoundingRectangle;
import static ru.mipt.bit.platformer.util.GdxGameUtils.drawTextureRegionUnscaled;
import static ru.mipt.bit.platformer.util.GdxGameUtils.moveRectangleAtTileCenter;

public class TreeGraphics {
    private final Texture texture = new Texture("images/greenTree.png");
    private final TextureRegion region = new TextureRegion(texture);
    private final Rectangle rectangle = createBoundingRectangle(region);

    public TreeGraphics(TiledMapTileLayer tileLayer, Tree tree) {
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
