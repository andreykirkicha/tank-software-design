package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.model.Movable;
import ru.mipt.bit.platformer.util.TileMovement;

import static com.badlogic.gdx.math.MathUtils.isEqual;
import static ru.mipt.bit.platformer.util.GdxGameUtils.continueProgress;
import static ru.mipt.bit.platformer.util.GdxGameUtils.createBoundingRectangle;
import static ru.mipt.bit.platformer.util.GdxGameUtils.drawTextureRegionUnscaled;

public class TankGraphics {
    private final float movementSpeed;
    private final Texture texture;
    private final TextureRegion region;
    private final Rectangle rectangle;
    private final TileMovement tileMovement;
    private final GridPoint2 previousCoordinates;
    private final GridPoint2 destinationCoordinates;
    private float movementProgress = 1f;

    public TankGraphics(TiledMapTileLayer tileLayer, Movable tank, String texturePath, float movementSpeed) {
        this.movementSpeed = movementSpeed;
        texture = new Texture(texturePath);
        region = new TextureRegion(texture);
        rectangle = createBoundingRectangle(region);
        tileMovement = new TileMovement(tileLayer, Interpolation.smooth);
        previousCoordinates = new GridPoint2(tank.getX(), tank.getY());
        destinationCoordinates = new GridPoint2(tank.getX(), tank.getY());
        update(0f);
    }

    public boolean isMoving() {
        return !isEqual(movementProgress, 1f);
    }

    public void moveTank(Movable tank) {
        previousCoordinates.set(destinationCoordinates);
        destinationCoordinates.set(tank.getX(), tank.getY());

        if (!previousCoordinates.equals(destinationCoordinates)) {
            movementProgress = 0f;
        }
    }

    public void update(float deltaTime) {
        tileMovement.moveRectangleBetweenTileCenters(
                rectangle,
                previousCoordinates,
                destinationCoordinates,
                movementProgress
        );
        movementProgress = continueProgress(movementProgress, deltaTime, movementSpeed);
    }

    public void draw(Batch batch, Movable tank) {
        drawTextureRegionUnscaled(batch, region, rectangle, tank.getDirection().getRotation());
    }

    public void dispose() {
        texture.dispose();
    }
}
