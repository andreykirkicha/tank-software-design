package ru.mipt.bit.platformer.model.state;

/** Domain: переходы между состояниями по доле оставшегося начального здоровья. */
public final class HealthTankStatePolicy implements TankStatePolicy {
    private final TankState light = new LightTankState();
    private final TankState medium = new MediumTankState();
    private final TankState heavy = new HeavyTankState();

    @Override
    public TankState select(int health, int maxHealth) {
        // Целочисленное сравнение сохраняет точность на границах 15% и 70%.
        // long предотвращает переполнение при больших значениях здоровья.
        if (maxHealth > 0 && (long) health * 100 > (long) maxHealth * 70) {
            return light;
        }
        if (maxHealth > 0 && (long) health * 100 >= (long) maxHealth * 15) {
            return medium;
        }
        return heavy;
    }
}
