package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;

import java.util.function.BooleanSupplier;

public class HealthBarGraphicsDecorator implements ObjectGraphics {
    private final ObjectGraphics wrapped;
    private final ObjectGraphics healthBar;
    private final BooleanSupplier visible;

    public HealthBarGraphicsDecorator(ObjectGraphics wrapped, ObjectGraphics healthBar, BooleanSupplier visible) {
        this.wrapped = wrapped;
        this.healthBar = healthBar;
        this.visible = visible;
    }

    @Override
    public void draw(Batch batch) {
        wrapped.draw(batch);
        if (visible.getAsBoolean()) {
            healthBar.draw(batch);
        }
    }

    @Override
    public void dispose() {
        wrapped.dispose();
        healthBar.dispose();
    }
}
