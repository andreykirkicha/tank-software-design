package ru.mipt.bit.platformer.model;

public class Tree implements Obstacle {
    private final int x;
    private final int y;

    public Tree(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
