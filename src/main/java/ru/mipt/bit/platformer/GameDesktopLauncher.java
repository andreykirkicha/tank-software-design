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
import ru.mipt.bit.platformer.control.ControlStrategy;
import ru.mipt.bit.platformer.control.KeyboardControlStrategy;
import ru.mipt.bit.platformer.control.RandomControlStrategy;
import ru.mipt.bit.platformer.control.TankController;
import ru.mipt.bit.platformer.graphics.TankGraphics;
import ru.mipt.bit.platformer.graphics.TreeGraphics;
import ru.mipt.bit.platformer.input.InputHandler;
import ru.mipt.bit.platformer.level.FileGameFieldLoader;
import ru.mipt.bit.platformer.level.GameFieldLoader;
import ru.mipt.bit.platformer.level.RandomGameFieldGenerator;
import ru.mipt.bit.platformer.level.RandomTankGenerator;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.GameField;
import ru.mipt.bit.platformer.model.Movable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.badlogic.gdx.Input.Keys.*;
import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static ru.mipt.bit.platformer.util.GdxGameUtils.*;

public class GameDesktopLauncher implements ApplicationListener {

    private static final String PLAYER_TANK_TEXTURE_PATH = "images/tank_blue.png";
    private static final String AI_TANK_TEXTURE_PATH = "images/tank_orange.png";
    private static final String TREE_TEXTURE_PATH = "images/greenTree.png";
    private static final float TANK_MOVEMENT_SPEED = 0.4f;
    private static final int LEVEL_WIDTH = 10;
    private static final int LEVEL_HEIGHT = 8;
    private static final int RANDOM_TREE_COUNT = 12;
    private static final int AI_TANK_COUNT = 4;

    private final boolean randomLevel;

    private Batch batch;

    private TiledMap level;
    private MapRenderer levelRenderer;
    private TankGraphics[] tanksGraphics;
    private TankController[] tankControllers;
    private TreeGraphics[] treesGraphics;

    private GameField gameField;

    public GameDesktopLauncher() {
        this(false);
    }

    public GameDesktopLauncher(boolean randomLevel) {
        this.randomLevel = randomLevel;
    }

    @Override
    public void create() {
        batch = new SpriteBatch();

        level = new TmxMapLoader().load("level.tmx");
        levelRenderer = createSingleLayerMapRenderer(level, batch);
        TiledMapTileLayer groundLayer = getSingleLayer(level);
        InputHandler inputHandler = new InputHandler(Gdx.input::isKeyPressed, createControls());

        gameField = loadGameField();
        new RandomTankGenerator().generate(gameField, AI_TANK_COUNT);

        tanksGraphics = new TankGraphics[gameField.getTanks().size()];
        tankControllers = new TankController[tanksGraphics.length];
        for (int i = 0; i < tanksGraphics.length; i++) {
            Movable tank = gameField.getTanks().get(i);
            boolean playerControlled = tank == gameField.getTank();
            String texturePath = playerControlled ? PLAYER_TANK_TEXTURE_PATH : AI_TANK_TEXTURE_PATH;
            tanksGraphics[i] = new TankGraphics(groundLayer, tank, texturePath, TANK_MOVEMENT_SPEED);
            ControlStrategy strategy = playerControlled
                    ? new KeyboardControlStrategy(inputHandler) : new RandomControlStrategy();
            tankControllers[i] = new TankController(gameField, tank, strategy);
        }
        treesGraphics = new TreeGraphics[gameField.getObstacles().length];
        for (int i = 0; i < gameField.getObstacles().length; i++) {
            treesGraphics[i] = new TreeGraphics(groundLayer, gameField.getObstacles()[i], TREE_TEXTURE_PATH);
        }
    }

    private GameField loadGameField() {
        GameFieldLoader loader;
        if (randomLevel) {
            loader = new RandomGameFieldGenerator(LEVEL_WIDTH, LEVEL_HEIGHT, RANDOM_TREE_COUNT);
        } else {
            InputStream levelStream = GameDesktopLauncher.class.getClassLoader().getResourceAsStream("level.txt");
            if (levelStream == null) {
                throw new IllegalStateException("Level resource not found: level.txt");
            }
            loader = new FileGameFieldLoader(new InputStreamReader(levelStream, StandardCharsets.UTF_8));
        }

        try {
            return loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot load game level", e);
        }
    }

    @Override
    public void render() {
        clearScreen();

        float deltaTime = Gdx.graphics.getDeltaTime();

        updateGame(deltaTime);
        renderGame();
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
    }

    private void updateGame(float deltaTime) {
        for (int i = 0; i < tanksGraphics.length; i++) {
            tanksGraphics[i].update(deltaTime);
            Movable tank = gameField.getTanks().get(i);
            if (gameField.isTankMoving(tank) && !tanksGraphics[i].isMoving()) {
                gameField.finishTankMove(tank);
            }
        }
        for (int i = 0; i < tankControllers.length; i++) {
            tankControllers[i].update();
            Movable tank = gameField.getTanks().get(i);
            if (gameField.isTankMoving(tank) && !tanksGraphics[i].isMoving()) {
                tanksGraphics[i].moveTank(tank);
            }
        }
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

        for (int i = 0; i < tanksGraphics.length; i++) {
            tanksGraphics[i].draw(batch, gameField.getTanks().get(i));
        }
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
        for (TankGraphics tankGraphics : tanksGraphics) {
            tankGraphics.dispose();
        }
        level.dispose();
        batch.dispose();
    }

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        // level width: 10 tiles x 128px, height: 8 tiles x 128px
        config.setWindowedMode(1280, 1024);
        boolean randomLevel = args.length > 0 && "--random".equals(args[0]);
        new Lwjgl3Application(new GameDesktopLauncher(randomLevel), config);
    }
}
