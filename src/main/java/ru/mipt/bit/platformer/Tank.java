package ru.mipt.bit.platformer;

public class Tank {
    private int x;
    private int y;
    private Direction direction;

    public Tank(int x, int y, Direction direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public int getNextX() {
        return x + direction.getDeltaX();
    }

    public int getNextY() {
        return y + direction.getDeltaY();
    }

    public void move() {
        x = getNextX();
        y = getNextY();
    }
}