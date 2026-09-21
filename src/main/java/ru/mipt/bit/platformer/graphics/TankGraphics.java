package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.util.TileMovement;

import static com.badlogic.gdx.math.MathUtils.isEqual;
import static ru.mipt.bit.platformer.util.GdxGameUtils.continueProgress;
import static ru.mipt.bit.platformer.util.GdxGameUtils.createBoundingRectangle;
import static ru.mipt.bit.platformer.util.GdxGameUtils.drawTextureRegionUnscaled;

public class TankGraphics {
    private static final float MOVEMENT_SPEED = 0.4f;

    private final Texture texture = new Texture("images/tank_blue.png");
    private final TextureRegion region = new TextureRegion(texture);
    private final Rectangle rectangle = createBoundingRectangle(region);
    private final TileMovement tileMovement;
    private final GridPoint2 previousCoordinates;
    private final GridPoint2 destinationCoordinates;
    private float movementProgress = 1f;

    public TankGraphics(TiledMapTileLayer tileLayer, Tank tank) {
        tileMovement = new TileMovement(tileLayer, Interpolation.smooth);
        previousCoordinates = new GridPoint2(tank.getX(), tank.getY());
        destinationCoordinates = new GridPoint2(tank.getX(), tank.getY());
        update(0f);
    }

    public boolean isMoving() {
        return !isEqual(movementProgress, 1f);
    }

    public void moveTank(Tank tank) {
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
        movementProgress = continueProgress(movementProgress, deltaTime, MOVEMENT_SPEED);
    }

    public void draw(Batch batch, Tank tank) {
        drawTextureRegionUnscaled(batch, region, rectangle, tank.getDirection().getRotation());
    }

    public void dispose() {
        texture.dispose();
    }
}
