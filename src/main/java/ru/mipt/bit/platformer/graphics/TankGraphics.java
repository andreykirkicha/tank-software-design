package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.model.Movable;
import ru.mipt.bit.platformer.model.GameField;
import ru.mipt.bit.platformer.util.TileMovement;

import static ru.mipt.bit.platformer.util.GdxGameUtils.createBoundingRectangle;
import static ru.mipt.bit.platformer.util.GdxGameUtils.drawTextureRegionUnscaled;

public class TankGraphics implements ObjectGraphics {
    private final GameField field;
    private final Movable tank;
    private final Texture texture;
    private final TextureRegion region;
    private final Rectangle rectangle;
    private final TileMovement tileMovement;
    private final GridPoint2 previousCoordinates;
    private final GridPoint2 destinationCoordinates;

    public TankGraphics(TiledMapTileLayer tileLayer, GameField field, Movable tank, String texturePath) {
        this.field = field;
        this.tank = tank;
        texture = new Texture(texturePath);
        region = new TextureRegion(texture);
        rectangle = createBoundingRectangle(region);
        tileMovement = new TileMovement(tileLayer, Interpolation.smooth);
        previousCoordinates = new GridPoint2(tank.getX(), tank.getY());
        destinationCoordinates = new GridPoint2(tank.getX(), tank.getY());
    }

    @Override
    public void draw(Batch batch) {
        previousCoordinates.set(field.getTankOriginX(tank), field.getTankOriginY(tank));
        destinationCoordinates.set(tank.getX(), tank.getY());
        tileMovement.moveRectangleBetweenTileCenters(
                rectangle,
                previousCoordinates,
                destinationCoordinates,
                field.getTankMovementProgress(tank)
        );
        drawTextureRegionUnscaled(batch, region, rectangle, tank.getDirection().getRotation());
    }

    public void dispose() {
        texture.dispose();
    }
}
