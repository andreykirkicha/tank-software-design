package ru.mipt.bit.platformer.model.state;

import ru.mipt.bit.platformer.model.Tank;

/** Domain: среднее состояние, движение вдвое медленнее, стрельба доступна. */
public final class MediumTankState implements TankState {
    @Override
    public float getMovementDuration() {
        return 2f * Tank.MOVEMENT_DURATION;
    }
}
