package model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class RobotModel {
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    private volatile double x = 200;
    private volatile double y = 200;
    private volatile double direction = 0;

    private volatile int targetX = 250;
    private volatile int targetY = 150;

    // Настройки динамики (пиксели/радианы за один тик контроллера)
    private static final double MAX_VELOCITY = 3.0;
    private static final double MAX_ANGULAR_VELOCITY = 0.08;
    private static final double KP_STEERING = 3.0; // Коэффициент пропорционального руления

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    public void setTarget(int tx, int ty) {
        targetX = tx;
        targetY = ty;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getDirection() { return direction; }
    public int getTargetX() { return targetX; }
    public int getTargetY() { return targetY; }

    public void update() {
        double dist = Math.hypot(targetX - x, targetY - y);
        if (dist < 3.0) { // Увеличенный радиус остановки предотвращает джиттер
            return;
        }

        double angleToTarget = Math.atan2(targetY - y, targetX - x);
        double angularVelocity = calculateSteering(angleToTarget, direction);

        // Если робот смотрит почти на цель → полная скорость, иначе → манёвр
        double velocity = Math.abs(angularVelocity) < 0.03 ? MAX_VELOCITY : MAX_VELOCITY * 0.6;

        move(velocity, angularVelocity, 1.0);
        support.firePropertyChange("state", null, null);
    }

    private double calculateSteering(double targetAngle, double currentAngle) {
        // 1. Нормализуем разность углов в диапазон [-π, π]
        double diff = targetAngle - currentAngle;
        while (diff <= -Math.PI) diff += 2 * Math.PI;
        while (diff > Math.PI) diff -= 2 * Math.PI;

        // 2. Пропорциональное управление (P-регулятор) + ограничение
        double rawVelocity = diff * KP_STEERING;
        return Math.max(-MAX_ANGULAR_VELOCITY, Math.min(MAX_ANGULAR_VELOCITY, rawVelocity));
    }

    private void move(double velocity, double angularVelocity, double duration) {
        // Интегрирование кинематики дифференциального привода
        if (Math.abs(angularVelocity) < 1e-9) {
            x += velocity * duration * Math.cos(direction);
            y += velocity * duration * Math.sin(direction);
        } else {
            x += (velocity / angularVelocity) *
                    (Math.sin(direction + angularVelocity * duration) - Math.sin(direction));
            y -= (velocity / angularVelocity) *
                    (Math.cos(direction + angularVelocity * duration) - Math.cos(direction));
        }
        direction += angularVelocity * duration;

        // Нормализация направления в [0, 2π)
        while (direction < 0) direction += 2 * Math.PI;
        while (direction >= 2 * Math.PI) direction -= 2 * Math.PI;
    }
}