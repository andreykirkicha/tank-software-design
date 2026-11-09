package ru.mipt.bit.platformer.command;

import ru.mipt.bit.platformer.graphics.GameFieldGraphics;

public class ToggleHealthBarsCommand implements Command {
    private final GameFieldGraphics graphics;

    public ToggleHealthBarsCommand(GameFieldGraphics graphics) {
        this.graphics = graphics;
    }

    @Override
    public void execute() {
        graphics.toggleHealthBars();
    }
}
