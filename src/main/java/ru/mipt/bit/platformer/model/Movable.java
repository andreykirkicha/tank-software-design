package ru.mipt.bit.platformer.model;

public interface Movable {
    int getX();

    int getY();

    Direction getDirection();

    void setDirection(Direction direction);

    int getNextX();

    int getNextY();

    void move();
}
