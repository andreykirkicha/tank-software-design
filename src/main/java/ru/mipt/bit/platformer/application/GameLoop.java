package ru.mipt.bit.platformer.application;

import java.util.ArrayList;
import java.util.List;
import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.command.ToggleHealthBarsCommand;
import ru.mipt.bit.platformer.control.ControlStrategy;
import ru.mipt.bit.platformer.control.TankController;
import ru.mipt.bit.platformer.model.GameField;
import ru.mipt.bit.platformer.model.Tank;

/**
 * Application: use case кадра. Собирает команды, выполняет их и обновляет домен.
 * Использует порты ввода, управления и отображения; не зависит от LibGDX.
 */
public final class GameLoop implements GameSession {
    private final GameField field;
    private final GameInput input;
    private final ControlStrategy playerStrategy;
    private final ControlStrategy botStrategy;
    private final HealthBarVisibility healthBars;

    public GameLoop(GameField field, GameInput input, ControlStrategy playerStrategy,
                    ControlStrategy botStrategy, HealthBarVisibility healthBars) {
        this.field = field;
        this.input = input;
        this.playerStrategy = playerStrategy;
        this.botStrategy = botStrategy;
        this.healthBars = healthBars;
    }

    @Override
    public void update(float deltaTime) {
        if (!Float.isFinite(deltaTime) || deltaTime < 0f) {
            throw new IllegalArgumentException("Tick must be finite and non-negative");
        }
        List<Command> commands = new ArrayList<>();
        if (input.isHealthBarToggleJustPressed()) {
            commands.add(new ToggleHealthBarsCommand(healthBars));
        }
        for (Tank tank : field.getTanks()) {
            ControlStrategy strategy = tank == field.getTank() ? playerStrategy : botStrategy;
            Command command = new TankController(field, tank, strategy).nextCommand();
            if (command != null) {
                commands.add(command);
            }
        }
        for (Command command : commands) {
            command.execute();
        }
        field.live(deltaTime);
    }
}
