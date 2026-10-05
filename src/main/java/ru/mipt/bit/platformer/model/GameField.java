package ru.mipt.bit.platformer.model;

public class GameField {
    private final int width;
    private final int height;
    private final Movable tank;
    private final Obstacle[] obstacles;

    public GameField(int width, int height, Movable tank, Obstacle[] obstacles) {
        this.width = width;
        this.height = height;
        this.tank = tank;
        this.obstacles = obstacles;
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

    public void moveTank(Direction direction) {
        tank.setDirection(direction);

        if (canTankMove()) {
            tank.move();
        }
    }

    private boolean canTankMove() {
        int nextX = tank.getNextX();
        int nextY = tank.getNextY();

        return isInsideField(nextX, nextY) && !hasObstacleAt(nextX, nextY);
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
