package ru.mipt.bit.platformer.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Domain: агрегат игрового поля; управляет объектами, движением и столкновениями.
 * Публикует события через выходной порт GameFieldListener без зависимости от UI.
 */
public class GameField {
    private final int width;
    private final int height;
    private final Tank tank;
    private final List<Obstacle> obstacles = new ArrayList<>();
    private final List<Tank> tanks = new ArrayList<>();
    private final List<GameObject> objects = new ArrayList<>();
    private final List<GameFieldListener> listeners = new ArrayList<>();
    private final Map<Movable, int[]> movementOrigins = new HashMap<>();
    private final Map<Movable, Float> movementProgress = new HashMap<>();

    public GameField(int width, int height, Tank tank, Obstacle[] obstacles) {
        this.width = width;
        this.height = height;
        this.tank = tank;
        for (Obstacle obstacle : obstacles) {
            this.obstacles.add(obstacle);
            addObject(obstacle);
        }
        addTank(tank);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Tank getTank() {
        return tank;
    }

    public Obstacle[] getObstacles() {
        return obstacles.toArray(new Obstacle[0]);
    }

    public List<Tank> getTanks() {
        return Collections.unmodifiableList(tanks);
    }

    public void addTank(Tank tank) {
        if (tanks.contains(tank) || !isCellFree(tank.getX(), tank.getY())) {
            throw new IllegalArgumentException("Tank must be placed in a free cell");
        }
        tanks.add(tank);
        addObject(tank);
    }

    public List<GameObject> getObjects() {
        return Collections.unmodifiableList(objects);
    }

    public boolean contains(GameObject object) {
        return objects.contains(object);
    }

    public void addListener(GameFieldListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
            // Initial objects use the same notification path as objects added during play.
            for (GameObject object : new ArrayList<>(objects)) {
                listener.objectAdded(object);
            }
        }
    }

    public void removeListener(GameFieldListener listener) {
        listeners.remove(listener);
    }

    private void addObject(GameObject object) {
        objects.add(object);
        for (GameFieldListener listener : new ArrayList<>(listeners)) {
            listener.objectAdded(object);
        }
    }

    public void removeObject(GameObject object) {
        if (!objects.remove(object)) {
            return;
        }
        tanks.remove(object);
        obstacles.remove(object);
        movementOrigins.remove(object);
        movementProgress.remove(object);
        for (GameFieldListener listener : new ArrayList<>(listeners)) {
            listener.objectRemoved(object);
        }
    }

    public void addBullet(Bullet bullet) {
        if (contains(bullet)) {
            throw new IllegalArgumentException("Bullet already belongs to this field");
        }
        addObject(bullet);
        // Check the spawn cell too: an adjacent target must be hit immediately.
        resolveBulletCollision(bullet);
    }

    public void resolveBulletCollision(Bullet bullet) {
        if (!contains(bullet)) {
            return;
        }
        if (!isInsideField(bullet.getX(), bullet.getY()) || hasObstacleAt(bullet.getX(), bullet.getY())) {
            removeObject(bullet);
            return;
        }
        for (Tank target : new ArrayList<>(tanks)) {
            int[] origin = movementOrigins.get(target);
            if (occupies(target, bullet.getX(), bullet.getY())
                    || origin != null && origin[0] == bullet.getX() && origin[1] == bullet.getY()) {
                removeObject(bullet);
                target.takeDamage(bullet.getDamage(), this);
                return;
            }
        }
        for (GameObject other : new ArrayList<>(objects)) {
            if (other instanceof Bullet && other != bullet && occupies(other, bullet.getX(), bullet.getY())) {
                removeObject(other);
                removeObject(bullet);
                return;
            }
        }
    }

    public void live(float tick) {
        if (!Float.isFinite(tick) || tick < 0f) {
            throw new IllegalArgumentException("Tick must be finite and non-negative");
        }
        // Small steps prevent a bullet from skipping a collision during a long frame.
        float remaining = tick;
        while (remaining > 0f) {
            float step = Math.min(remaining, Bullet.STEP_DURATION);
            for (GameObject object : new ArrayList<>(objects)) {
                if (contains(object)) {
                    object.live(step, this);
                }
            }
            remaining = Math.max(0f, remaining - step);
        }
    }

    public void moveTank(Direction direction) {
        moveTank(tank, direction);
    }

    public boolean moveTank(Movable tank, Direction direction) {
        requireTank(tank);
        if (isTankMoving(tank)) {
            return false;
        }
        tank.setDirection(direction);

        if (!isCellFree(tank.getNextX(), tank.getNextY())) {
            return false;
        }

        // The destination is stored in the tank; keep its origin occupied until arrival.
        movementOrigins.put(tank, new int[]{tank.getX(), tank.getY()});
        movementProgress.put(tank, 0f);
        tank.move();
        for (GameObject object : new ArrayList<>(objects)) {
            if (object instanceof Bullet) {
                resolveBulletCollision((Bullet) object);
            }
        }
        return true;
    }

    public boolean isTankMoving(Movable tank) {
        return movementOrigins.containsKey(tank);
    }

    public void finishTankMove(Movable tank) {
        requireTank(tank);
        movementOrigins.remove(tank);
        movementProgress.remove(tank);
    }

    public void advanceTankMove(Tank tank, float tick) {
        if (isTankMoving(tank)) {
            // Store distance, not time: a state change preserves the travelled part of the tile.
            float progress = movementProgress.get(tank) + tick / tank.getMovementDuration();
            movementProgress.put(tank, progress);
            if (progress + 0.000001f >= 1f) {
                finishTankMove(tank);
            }
        }
    }

    public float getTankMovementProgress(Movable tank) {
        return isTankMoving(tank) ? Math.min(1f, movementProgress.get(tank)) : 1f;
    }

    public int getTankOriginX(Movable tank) {
        return isTankMoving(tank) ? movementOrigins.get(tank)[0] : tank.getX();
    }

    public int getTankOriginY(Movable tank) {
        return isTankMoving(tank) ? movementOrigins.get(tank)[1] : tank.getY();
    }

    public boolean isCellFree(int x, int y) {
        if (!isInsideField(x, y) || hasObstacleAt(x, y)) {
            return false;
        }
        for (Movable tank : tanks) {
            if (tank.getX() == x && tank.getY() == y) {
                return false;
            }
        }
        for (int[] origin : movementOrigins.values()) {
            if (origin[0] == x && origin[1] == y) {
                return false;
            }
        }
        return true;
    }

    private void requireTank(Movable tank) {
        if (!tanks.contains(tank)) {
            throw new IllegalArgumentException("Tank does not belong to this field");
        }
    }

    private boolean isInsideField(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    private boolean hasObstacleAt(int x, int y) {
        for (Obstacle obstacle : obstacles) {
            if (obstacle.getX() == x && obstacle.getY() == y) {
                return true;
            }
        }

        return false;
    }

    private boolean occupies(GameObject object, int x, int y) {
        return object.getX() == x && object.getY() == y;
    }
}
