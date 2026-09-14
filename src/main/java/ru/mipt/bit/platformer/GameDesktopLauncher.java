package ru.mipt.bit.platformer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.util.TileMovement;

import static com.badlogic.gdx.Input.Keys.*;
import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static com.badlogic.gdx.math.MathUtils.isEqual;
import static ru.mipt.bit.platformer.util.GdxGameUtils.*;

public class GameDesktopLauncher implements ApplicationListener {

    private static final float MOVEMENT_SPEED = 0.4f;

    private Batch batch;

    private TiledMap level;
    private MapRenderer levelRenderer;
    private TileMovement tileMovement;

    private Texture blueTankTexture;
    private TextureRegion tankGraphics;
    private Rectangle tankRectangle;

    private Texture greenTreeTexture;
    private TextureRegion treeGraphics;
    private Rectangle treeRectangle;

    private GameField gameField;

    private GridPoint2 previousTankCoordinates;
    private GridPoint2 tankDestinationCoordinates;
    private float tankMovementProgress = 1f;

    @Override
    public void create() {
        batch = new SpriteBatch();

        level = new TmxMapLoader().load("level.tmx");
        levelRenderer = createSingleLayerMapRenderer(level, batch);
        TiledMapTileLayer groundLayer = getSingleLayer(level);
        tileMovement = new TileMovement(groundLayer, Interpolation.smooth);

        Tank tank = new Tank(1, 1, Direction.RIGHT);
        Tree[] trees = {
                new Tree(1, 3)
        };
        gameField = new GameField(10, 8, tank, trees);

        previousTankCoordinates = new GridPoint2(tank.getX(), tank.getY());
        tankDestinationCoordinates = new GridPoint2(tank.getX(), tank.getY());

        blueTankTexture = new Texture("images/tank_blue.png");
        tankGraphics = new TextureRegion(blueTankTexture);
        tankRectangle = createBoundingRectangle(tankGraphics);

        greenTreeTexture = new Texture("images/greenTree.png");
        treeGraphics = new TextureRegion(greenTreeTexture);
        treeRectangle = createBoundingRectangle(treeGraphics);

        Tree tree = gameField.getTrees()[0];
        moveRectangleAtTileCenter(
                groundLayer,
                treeRectangle,
                new GridPoint2(tree.getX(), tree.getY())
        );
    }

    @Override
    public void render() {
        clearScreen();

        float deltaTime = Gdx.graphics.getDeltaTime();

        updateGame();
        updateTankAnimation(deltaTime);
        renderGame();
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
    }

    private void updateGame() {
        if (!isEqual(tankMovementProgress, 1f)) {
            return;
        }

        Direction direction = getPressedDirection();
        if (direction == null) {
            return;
        }

        Tank tank = gameField.getTank();

        previousTankCoordinates.set(tank.getX(), tank.getY());

        gameField.moveTank(direction);

        tankDestinationCoordinates.set(tank.getX(), tank.getY());

        if (!previousTankCoordinates.equals(tankDestinationCoordinates)) {
            tankMovementProgress = 0f;
        }
    }

    private Direction getPressedDirection() {
        if (isKeyPressed(UP, W)) {
            return Direction.UP;
        }
        if (isKeyPressed(LEFT, A)) {
            return Direction.LEFT;
        }
        if (isKeyPressed(DOWN, S)) {
            return Direction.DOWN;
        }
        if (isKeyPressed(RIGHT, D)) {
            return Direction.RIGHT;
        }

        return null;
    }

    private boolean isKeyPressed(int firstKey, int secondKey) {
        return Gdx.input.isKeyPressed(firstKey)
                || Gdx.input.isKeyPressed(secondKey);
    }

    private void updateTankAnimation(float deltaTime) {
        tileMovement.moveRectangleBetweenTileCenters(
                tankRectangle,
                previousTankCoordinates,
                tankDestinationCoordinates,
                tankMovementProgress
        );

        tankMovementProgress = continueProgress(
                tankMovementProgress,
                deltaTime,
                MOVEMENT_SPEED
        );
    }

    private void renderGame() {
        levelRenderer.render();

        batch.begin();

        drawTextureRegionUnscaled(
                batch,
                tankGraphics,
                tankRectangle,
                getTankRotation(gameField.getTank().getDirection())
        );

        drawTextureRegionUnscaled(
                batch,
                treeGraphics,
                treeRectangle,
                0f
        );

        batch.end();
    }

    private float getTankRotation(Direction direction) {
        if (direction == Direction.UP) {
            return 90f;
        }
        if (direction == Direction.LEFT) {
            return -180f;
        }
        if (direction == Direction.DOWN) {
            return -90f;
        }

        return 0f;
    }

    @Override
    public void resize(int width, int height) {
        // do not react to window resizing
    }

    @Override
    public void pause() {
        // game doesn't get paused
    }

    @Override
    public void resume() {
        // game doesn't get paused
    }

    @Override
    public void dispose() {
        // dispose of all the native resources (classes which implement com.badlogic.gdx.utils.Disposable)
        greenTreeTexture.dispose();
        blueTankTexture.dispose();
        level.dispose();
        batch.dispose();
    }

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        // level width: 10 tiles x 128px, height: 8 tiles x 128px
        config.setWindowedMode(1280, 1024);
        new Lwjgl3Application(new GameDesktopLauncher(), config);
    }
}
