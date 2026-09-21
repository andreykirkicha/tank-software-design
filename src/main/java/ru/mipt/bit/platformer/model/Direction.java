package ru.mipt.bit.platformer.model;

public enum Direction {
    UP(0, 1, 90f),
    DOWN(0, -1, -90f),
    LEFT(-1, 0, -180f),
    RIGHT(1, 0, 0f);

    private final int deltaX;
    private final int deltaY;
    private final float rotation;

    Direction(int deltaX, int deltaY, float rotation) {
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.rotation = rotation;
    }

    public int getDeltaX() {
        return deltaX;
    }

    public int getDeltaY() {
        return deltaY;
    }

    public float getRotation() {
        return rotation;
    }
}
