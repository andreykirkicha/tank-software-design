package ru.mipt.bit.platformer.command;

/**
 * Application: контракт исполняемой команды (шаблон Command).
 * Внутренняя абстракция use cases, а не порт внешней системы.
 */
public interface Command {
    void execute();
}
