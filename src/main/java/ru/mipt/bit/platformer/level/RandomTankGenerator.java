package ru.mipt.bit.platformer.level;

import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.GameField;
import ru.mipt.bit.platformer.model.Tank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomTankGenerator {
    private final Random random;

    public RandomTankGenerator() {
        this(new Random());
    }

    public RandomTankGenerator(Random random) {
        this.random = random;
    }

    public void generate(GameField field, int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Tank count must not be negative");
        }
        List<int[]> freeCells = new ArrayList<>();
        for (int y = 0; y < field.getHeight(); y++) {
            for (int x = 0; x < field.getWidth(); x++) {
                if (field.isCellFree(x, y)) {
                    freeCells.add(new int[]{x, y});
                }
            }
        }
        if (count > freeCells.size()) {
            throw new IllegalArgumentException("Not enough free cells for tanks");
        }
        Collections.shuffle(freeCells, random);
        Direction[] directions = Direction.values();
        for (int i = 0; i < count; i++) {
            int[] cell = freeCells.get(i);
            field.addTank(new Tank(cell[0], cell[1], directions[random.nextInt(directions.length)]));
        }
    }
}
