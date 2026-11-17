package ru.mipt.bit.platformer.application;

/** Application: входной порт выполнения одного кадра игры без отрисовки. */
public interface GameSession {
    void update(float deltaTime);
}
