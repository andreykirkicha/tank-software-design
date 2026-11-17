package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Disposable;

/**
 * Infrastructure: внутренний контракт графических адаптеров LibGDX.
 * Зависит от Batch и Disposable, поэтому не является портом ядра.
 */
public interface ObjectGraphics extends Disposable {
    void draw(Batch batch);
}
