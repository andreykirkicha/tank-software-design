package ru.mipt.bit.platformer.command;

import ru.mipt.bit.platformer.application.HealthBarVisibility;

/**
 * Application: use case переключения полосок здоровья через выходной порт
 * HealthBarVisibility; не зависит от конкретного графического адаптера.
 */
public class ToggleHealthBarsCommand implements Command {
    private final HealthBarVisibility graphics;

    public ToggleHealthBarsCommand(HealthBarVisibility graphics) {
        this.graphics = graphics;
    }

    @Override
    public void execute() {
        graphics.toggleHealthBars();
    }
}
