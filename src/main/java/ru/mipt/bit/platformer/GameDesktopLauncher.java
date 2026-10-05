package ru.mipt.bit.platformer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import ru.mipt.bit.platformer.graphics.TankGraphics;
import ru.mipt.bit.platformer.graphics.TreeGraphics;
import ru.mipt.bit.platformer.input.InputHandler;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.GameField;
import ru.mipt.bit.platformer.model.Movable;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.Tree;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.badlogic.gdx.Input.Keys.*;
import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static ru.mipt.bit.platformer.util.GdxGameUtils.*;

public class GameDesktopLauncher implements ApplicationListener {

    private static final String TANK_TEXTURE_PATH = "images/tank_blue.png";
    private static final String TREE_TEXTURE_PATH = "images/greenTree.png";
    private static final float TANK_MOVEMENT_SPEED = 0.4f;

    private Batch batch;

    private TiledMap level;
    private MapRenderer levelRenderer;
    private TankGraphics tankGraphics;
    private TreeGraphics[] treesGraphics;
    private InputHandler inputHandler;

    private GameField gameField;

    @Override
    public void create() {
        batch = new SpriteBatch();

        level = new TmxMapLoader().load("level.tmx");
        levelRenderer = createSingleLayerMapRenderer(level, batch);
        TiledMapTileLayer groundLayer = getSingleLayer(level);
        inputHandler = new InputHandler(Gdx.input::isKeyPressed, createControls());

        Tank tank = new Tank(1, 1, Direction.RIGHT);
        Tree[] trees = {
                new Tree(1, 3),
                new Tree(7, 5)
        };
        gameField = new GameField(10, 8, tank, trees);

        tankGraphics = new TankGraphics(groundLayer, tank, TANK_TEXTURE_PATH, TANK_MOVEMENT_SPEED);
        treesGraphics = new TreeGraphics[trees.length];
        for (int i = 0; i < trees.length; i++) {
            treesGraphics[i] = new TreeGraphics(groundLayer, trees[i], TREE_TEXTURE_PATH);
        }
    }

    @Override
    public void render() {
        clearScreen();

        float deltaTime = Gdx.graphics.getDeltaTime();

        updateGame();
        tankGraphics.update(deltaTime);
        renderGame();
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
    }

    private void updateGame() {
        if (tankGraphics.isMoving()) {
            return;
        }

        Direction direction = inputHandler.getPressedDirection();
        if (direction == null) {
            return;
        }

        Movable tank = gameField.getTank();

        gameField.moveTank(direction);
        tankGraphics.moveTank(tank);
    }

    private Map<Integer, Direction> createControls() {
        Map<Integer, Direction> controls = new LinkedHashMap<>();
        controls.put(UP, Direction.UP);
        controls.put(W, Direction.UP);
        controls.put(LEFT, Direction.LEFT);
        controls.put(A, Direction.LEFT);
        controls.put(DOWN, Direction.DOWN);
        controls.put(S, Direction.DOWN);
        controls.put(RIGHT, Direction.RIGHT);
        controls.put(D, Direction.RIGHT);
        return controls;
    }

    private void renderGame() {
        levelRenderer.render();

        batch.begin();

        tankGraphics.draw(batch, gameField.getTank());
        for (TreeGraphics treeGraphics : treesGraphics) {
            treeGraphics.draw(batch);
        }

        batch.end();
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
        for (TreeGraphics treeGraphics : treesGraphics) {
            treeGraphics.dispose();
        }
        tankGraphics.dispose();
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
