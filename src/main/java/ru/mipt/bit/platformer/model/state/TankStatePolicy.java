package ru.mipt.bit.platformer.model.state;

/**
 * Domain: правило выбора состояния при создании танка и изменении здоровья.
 * Это доменная стратегия, а не порт внешнего устройства. Можно внедрить правило,
 * учитывающее бустеры, не меняя Tank, команды, контроллер или графику.
 */
@FunctionalInterface
public interface TankStatePolicy {
    TankState select(int health, int maxHealth);
}
