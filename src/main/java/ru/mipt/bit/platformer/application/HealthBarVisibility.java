package ru.mipt.bit.platformer.application;

/** Application: выходной порт переключения отображения полосок здоровья. */
@FunctionalInterface
public interface HealthBarVisibility {
    void toggleHealthBars();
}
