package ru.mipt.bit.platformer.model;

public interface Movable extends GameObject {
    Direction getDirection();

    void setDirection(Direction direction);

    int getNextX();

    int getNextY();

    void move();
}
