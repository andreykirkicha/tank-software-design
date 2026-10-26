package ru.mipt.bit.platformer.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameField {
    private final int width;
    private final int height;
    private final Movable tank;
    private final Obstacle[] obstacles;
    private final List<Movable> tanks = new ArrayList<>();
    private final Map<Movable, int[]> movementOrigins = new HashMap<>();

    public GameField(int width, int height, Movable tank, Obstacle[] obstacles) {
        this.width = width;
        this.height = height;
        this.tank = tank;
        this.obstacles = obstacles;
        tanks.add(tank);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Movable getTank() {
        return tank;
    }

    public Obstacle[] getObstacles() {
        return obstacles;
    }

    public List<Movable> getTanks() {
        return Collections.unmodifiableList(tanks);
    }

    public void addTank(Movable tank) {
        if (tanks.contains(tank) || !isCellFree(tank.getX(), tank.getY())) {
            throw new IllegalArgumentException("Tank must be placed in a free cell");
        }
        tanks.add(tank);
    }

    public void moveTank(Direction direction) {
        moveTank(tank, direction);
    }

    public boolean moveTank(Movable tank, Direction direction) {
        requireTank(tank);
        if (isTankMoving(tank)) {
            return false;
        }
        tank.setDirection(direction);

        if (!isCellFree(tank.getNextX(), tank.getNextY())) {
            return false;
        }

        // The destination is stored in the tank; keep its origin occupied until arrival.
        movementOrigins.put(tank, new int[]{tank.getX(), tank.getY()});
        tank.move();
        return true;
    }

    public boolean isTankMoving(Movable tank) {
        return movementOrigins.containsKey(tank);
    }

    public void finishTankMove(Movable tank) {
        requireTank(tank);
        movementOrigins.remove(tank);
    }

    public boolean isCellFree(int x, int y) {
        if (!isInsideField(x, y) || hasObstacleAt(x, y)) {
            return false;
        }
        for (Movable tank : tanks) {
            if (tank.getX() == x && tank.getY() == y) {
                return false;
            }
        }
        for (int[] origin : movementOrigins.values()) {
            if (origin[0] == x && origin[1] == y) {
                return false;
            }
        }
        return true;
    }

    private void requireTank(Movable tank) {
        if (!tanks.contains(tank)) {
            throw new IllegalArgumentException("Tank does not belong to this field");
        }
    }

    private boolean isInsideField(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    private boolean hasObstacleAt(int x, int y) {
        for (Obstacle obstacle : obstacles) {
            if (obstacle.getX() == x && obstacle.getY() == y) {
                return true;
            }
        }

        return false;
    }
}
