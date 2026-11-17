package ru.mipt.bit.platformer.model;

import java.util.Objects;
import ru.mipt.bit.platformer.model.state.HealthTankStatePolicy;
import ru.mipt.bit.platformer.model.state.TankState;
import ru.mipt.bit.platformer.model.state.TankStatePolicy;

/**
 * Domain: сущность танка и контекст шаблона State.
 * Делегирует скорость и возможность стрельбы текущему TankState;
 * выбирает состояние через заменяемую доменную TankStatePolicy.
 */
public class Tank implements Movable {
    public static final float MOVEMENT_DURATION = 0.4f;
    public static final float SHOT_COOLDOWN = 0.4f;
    public static final int DEFAULT_HEALTH = 100;
    public static final int DEFAULT_BULLET_DAMAGE = 50;

    private int x;
    private int y;
    private Direction direction;
    private int health;
    private final int maxHealth;
    private final int bulletDamage;
    private float shotCooldown;
    private final TankStatePolicy statePolicy;
    private TankState state;

    public Tank(int x, int y, Direction direction) {
        this(x, y, direction, DEFAULT_HEALTH, DEFAULT_BULLET_DAMAGE);
    }

    public Tank(int x, int y, Direction direction, int health, int bulletDamage) {
        this(x, y, direction, health, bulletDamage, new HealthTankStatePolicy());
    }

    public Tank(int x, int y, Direction direction, int health, int bulletDamage, TankStatePolicy statePolicy) {
        if (health < 0 || bulletDamage <= 0) {
            throw new IllegalArgumentException("Health must be non-negative and bullet damage positive");
        }
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.health = health;
        this.maxHealth = health;
        this.bulletDamage = bulletDamage;
        this.statePolicy = Objects.requireNonNull(statePolicy, "State policy must not be null");
        refreshState();
    }

    public TankState getState() {
        return state;
    }

    /** Re-evaluates the policy, also usable when a future booster starts or expires. */
    public void refreshState() {
        state = Objects.requireNonNull(statePolicy.select(health, maxHealth), "State must not be null");
    }

    @Override
    public float getMovementDuration() {
        return state.getMovementDuration();
    }

    public boolean canShoot() {
        return isReady() && state.canShoot();
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public boolean isReady() {
        // Repeated frame subtraction can leave a tiny positive rounding remainder.
        return shotCooldown <= 0.000001f && health >= 0;
    }

    public void shoot(GameField field) {
        if (field.contains(this) && !field.isTankMoving(this) && canShoot()) {
            shotCooldown = state.getShotCooldown();
            field.addBullet(new Bullet(getNextX(), getNextY(), direction, bulletDamage));
        }
    }

    public void takeDamage(int damage, GameField field) {
        if (damage <= 0) {
            throw new IllegalArgumentException("Damage must be positive");
        }
        health -= damage;
        refreshState();
        if (health < 0) {
            field.removeObject(this);
        }
    }

    @Override
    public void live(float tick, GameField field) {
        shotCooldown = Math.max(0f, shotCooldown - tick);
        field.advanceTankMove(this, tick);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public int getNextX() {
        return x + direction.getDeltaX();
    }

    public int getNextY() {
        return y + direction.getDeltaY();
    }

    public void move() {
        x = getNextX();
        y = getNextY();
    }
}
