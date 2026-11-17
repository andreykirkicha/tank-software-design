package ru.mipt.bit.platformer.model;

/**
 * Domain: выходной порт событий жизненного цикла объектов (Observer).
 * Адаптер GameFieldGraphics отображает события; домен не знает о LibGDX.
 */
public interface GameFieldListener {
    void objectAdded(GameObject object);

    void objectRemoved(GameObject object);
}
