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
import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.control.ControlStrategy;
import ru.mipt.bit.platformer.control.KeyboardControlStrategy;
import ru.mipt.bit.platformer.control.RandomControlStrategy;
import ru.mipt.bit.platformer.control.TankController;
import ru.mipt.bit.platformer.graphics.GameFieldGraphics;
import ru.mipt.bit.platformer.input.InputHandler;
import ru.mipt.bit.platformer.level.FileGameFieldLoader;
import ru.mipt.bit.platformer.level.GameFieldLoader;
import ru.mipt.bit.platformer.level.RandomGameFieldGenerator;
import ru.mipt.bit.platformer.level.RandomTankGenerator;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.GameField;
import ru.mipt.bit.platformer.model.Tank;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.badlogic.gdx.Input.Keys.*;
import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static ru.mipt.bit.platformer.util.GdxGameUtils.*;

public class GameDesktopLauncher implements ApplicationListener {

    private static final int LEVEL_WIDTH = 10;
    private static final int LEVEL_HEIGHT = 8;
    private static final int RANDOM_TREE_COUNT = 12;
    private static final int AI_TANK_COUNT = 4;

    private final boolean randomLevel;

    private Batch batch;

    private TiledMap level;
    private MapRenderer levelRenderer;
    private GameFieldGraphics fieldGraphics;
    private ControlStrategy keyboardStrategy;
    private ControlStrategy randomStrategy;

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
        fieldGraphics = new GameFieldGraphics(gameField, groundLayer);
        new RandomTankGenerator().generate(gameField, AI_TANK_COUNT);
        keyboardStrategy = new KeyboardControlStrategy(inputHandler);
        randomStrategy = new RandomControlStrategy();
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
        List<Command> commands = new ArrayList<>();
        for (Tank tank : gameField.getTanks()) {
            ControlStrategy strategy = tank == gameField.getTank() ? keyboardStrategy : randomStrategy;
            Command command = new TankController(gameField, tank, strategy).nextCommand();
            if (command != null) {
                commands.add(command);
            }
        }
        for (Command command : commands) {
            command.execute();
        }
        gameField.live(deltaTime);
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

        fieldGraphics.draw(batch);

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
        fieldGraphics.dispose();
        ((com.badlogic.gdx.utils.Disposable) levelRenderer).dispose();
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
