package ru.mipt.bit.platformer.model;

/**
 * Domain: контракт перемещаемой сущности с длительностью шага.
 * Это доменная абстракция, а не инфраструктурный порт.
 */
public interface Movable extends GameObject {
    Direction getDirection();

    void setDirection(Direction direction);

    int getNextX();

    int getNextY();

    void move();

    float getMovementDuration();
}
