package ru.mipt.bit.platformer.model;

public interface GameFieldListener {
    void objectAdded(GameObject object);

    void objectRemoved(GameObject object);
}
