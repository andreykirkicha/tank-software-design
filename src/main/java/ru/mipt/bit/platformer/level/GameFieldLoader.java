package ru.mipt.bit.platformer.level;

import ru.mipt.bit.platformer.model.GameField;

import java.io.IOException;

public interface GameFieldLoader {
    GameField load() throws IOException;
}
