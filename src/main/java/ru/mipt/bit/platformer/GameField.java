package ru.mipt.bit.platformer;

public class GameField {
    private final int width;
    private final int height;
    private final Tank tank;
    private final Tree[] trees;

    public GameField(int width, int height, Tank tank, Tree[] trees) {
        this.width = width;
        this.height = height;
        this.tank = tank;
        this.trees = trees;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Tank getTank() {
        return tank;
    }

    public Tree[] getTrees() {
        return trees;
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

        return isInsideField(nextX, nextY) && !hasTreeAt(nextX, nextY);
    }

    private boolean isInsideField(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    private boolean hasTreeAt(int x, int y) {
        for (Tree tree : trees) {
            if (tree.getX() == x && tree.getY() == y) {
                return true;
            }
        }

        return false;
    }
}