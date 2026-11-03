package ru.mipt.bit.platformer.model;

public class Bullet implements GameObject {
    public static final float STEP_DURATION = 0.1f;

    private int x;
    private int y;
    private final Direction direction;
    private final int damage;
    private float elapsed;

    public Bullet(int x, int y, Direction direction, int damage) {
        if (damage <= 0) {
            throw new IllegalArgumentException("Bullet damage must be positive");
        }
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.damage = damage;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getDamage() {
        return damage;
    }

    @Override
    public void live(float tick, GameField field) {
        elapsed += tick;
        while (elapsed + 0.000001f >= STEP_DURATION && field.contains(this)) {
            elapsed = Math.max(0f, elapsed - STEP_DURATION);
            x += direction.getDeltaX();
            y += direction.getDeltaY();
            field.resolveBulletCollision(this);
        }
    }
}
