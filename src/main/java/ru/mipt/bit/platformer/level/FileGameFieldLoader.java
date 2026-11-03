package ru.mipt.bit.platformer.level;

import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.GameField;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.Tree;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileGameFieldLoader implements GameFieldLoader {
    private static final char TREE = 'T';
    private static final char TANK = 'X';
    private static final char EMPTY = '_';

    private final Reader reader;
    private final Path path;

    public FileGameFieldLoader(Reader reader) {
        if (reader == null) {
            throw new IllegalArgumentException("Reader must not be null");
        }
        this.reader = reader;
        this.path = null;
    }

    public FileGameFieldLoader(Path path) {
        if (path == null) {
            throw new IllegalArgumentException("Path must not be null");
        }
        this.path = path;
        this.reader = null;
    }

    @Override
    public GameField load() throws IOException {
        List<String> rows;
        if (path == null) {
            rows = readRows(reader);
        } else {
            try (Reader fileReader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                rows = readRows(fileReader);
            }
        }
        validateRectangle(rows);

        int height = rows.size();
        int width = rows.get(0).length();
        Tank tank = null;
        List<Tree> trees = new ArrayList<>();

        for (int row = 0; row < height; row++) {
            for (int x = 0; x < width; x++) {
                char cell = rows.get(row).charAt(x);
                int y = height - row - 1;
                if (cell == TREE) {
                    trees.add(new Tree(x, y));
                } else if (cell == TANK) {
                    if (tank != null) {
                        throw invalid("Level must contain exactly one tank");
                    }
                    tank = new Tank(x, y, Direction.RIGHT);
                } else if (cell != EMPTY) {
                    throw invalid("Unknown cell '" + cell + "' at row " + (row + 1) + ", column " + (x + 1));
                }
            }
        }

        if (tank == null) {
            throw invalid("Level must contain exactly one tank");
        }
        return new GameField(width, height, tank, trees.toArray(new Tree[0]));
    }

    private List<String> readRows(Reader source) throws IOException {
        List<String> rows = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(source);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            rows.add(line);
        }
        if (rows.isEmpty()) {
            throw invalid("Level must not be empty");
        }
        return rows;
    }

    private void validateRectangle(List<String> rows) throws IOException {
        int width = rows.get(0).length();
        if (width == 0) {
            throw invalid("Level rows must not be empty");
        }
        for (int row = 1; row < rows.size(); row++) {
            if (rows.get(row).length() != width) {
                throw invalid("All level rows must have the same width");
            }
        }
    }

    private IOException invalid(String message) {
        return new IOException("Invalid level: " + message);
    }
}
