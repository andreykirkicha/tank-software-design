package ru.mipt.bit.platformer.model.state;

import ru.mipt.bit.platformer.model.Tank;

/** Domain: тяжёлое состояние, движение втрое медленнее, стрельба запрещена. */
public final class HeavyTankState implements TankState {
    @Override
    public float getMovementDuration() {
        return 3f * Tank.MOVEMENT_DURATION;
    }

    @Override
    public boolean canShoot() {
        return false;
    }
}
