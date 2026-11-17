package ru.mipt.bit.platformer.model.state;

import ru.mipt.bit.platformer.model.Tank;

/**
 * Domain: состояние танка (шаблон State), определяющее движение и стрельбу.
 * Новые состояния могут менять также время перезарядки, например для бустера.
 */
public interface TankState {
    float getMovementDuration();

    default float getShotCooldown() {
        return Tank.SHOT_COOLDOWN;
    }

    default boolean canShoot() {
        return true;
    }
}
