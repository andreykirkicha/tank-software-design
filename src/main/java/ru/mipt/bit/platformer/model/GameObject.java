package ru.mipt.bit.platformer.model;

public interface GameObject {
    int getX();

    int getY();

    default void live(float tick, GameField field) {
        // Static objects do not change over time.
    }
}
