package ru.mipt.bit.platformer.model.state;

import ru.mipt.bit.platformer.model.Tank;

/** Domain: лёгкое состояние, движение и стрельба с обычной скоростью. */
public final class LightTankState implements TankState {
    @Override
    public float getMovementDuration() {
        return Tank.MOVEMENT_DURATION;
    }
}
