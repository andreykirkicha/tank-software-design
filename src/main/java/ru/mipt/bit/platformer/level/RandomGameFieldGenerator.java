package ru.mipt.bit.platformer.level;

import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.GameField;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.Tree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomGameFieldGenerator implements GameFieldLoader {
    private final int width;
    private final int height;
    private final int treeCount;
    private final Random random;

    public RandomGameFieldGenerator(int width, int height, int treeCount) {
        this(width, height, treeCount, new Random());
    }

    public RandomGameFieldGenerator(int width, int height, int treeCount, Random random) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Field dimensions must be positive");
        }
        if (treeCount < 0 || treeCount >= width * height) {
            throw new IllegalArgumentException("Tree count must be between 0 and field size - 1");
        }
        if (random == null) {
            throw new IllegalArgumentException("Random must not be null");
        }

        this.width = width;
        this.height = height;
        this.treeCount = treeCount;
        this.random = random;
    }

    @Override
    public GameField load() throws IOException {
        List<Integer> cells = new ArrayList<>();
        for (int cell = 0; cell < width * height; cell++) {
            cells.add(cell);
        }
        Collections.shuffle(cells, random);

        int tankCell = cells.get(0);
        Tank tank = new Tank(x(tankCell), y(tankCell), randomDirection());
        Tree[] trees = new Tree[treeCount];
        for (int i = 0; i < treeCount; i++) {
            int treeCell = cells.get(i + 1);
            trees[i] = new Tree(x(treeCell), y(treeCell));
        }

        return new GameField(width, height, tank, trees);
    }

    private int x(int cell) {
        return cell % width;
    }

    private int y(int cell) {
        return cell / width;
    }

    private Direction randomDirection() {
        Direction[] directions = Direction.values();
        return directions[random.nextInt(directions.length)];
    }
}
