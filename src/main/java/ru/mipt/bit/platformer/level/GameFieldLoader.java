package ru.mipt.bit.platformer.level;

import ru.mipt.bit.platformer.model.GameField;

import java.io.IOException;

/**
 * Application: выходной порт загрузки начального агрегата GameField.
 * Реализуется файловым и случайным адаптерами; используется при сборке игры.
 * Пакет level сохранён для совместимости существующих клиентов.
 */
public interface GameFieldLoader {
    GameField load() throws IOException;
}
