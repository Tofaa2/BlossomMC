package net.blossom.core.utils;

import net.minestom.server.coordinate.Pos;

public final class AnimationUtils {

    public static Pos[] createCircleMovement(Pos startPosition, double radius) {
        Pos[] positions = new Pos[360];
        for (int i = 0; i < 360; i++) {
            double angle = i * Math.PI / 180;
            double x = startPosition.x() + radius * Math.cos(angle);
            double z = startPosition.z() + radius * Math.sin(angle);
            positions[i] = new Pos(x, startPosition.y(), z);
        }
        return positions;
    }

    public static Pos[] forwardBackward(Pos startPosition, double distanceX) {
        double step = distanceX / 1000;
        Pos[] positions = new Pos[2000];
        for (int i = 0; i < 1000; i++) {
            double x = startPosition.x() + (i * step);
            positions[i] = new Pos(x, startPosition.y(), startPosition.z());
        }
        for (int i = 1000; i < 2000; i++) {
            double x = startPosition.x() + (i * step);
            positions[i] = new Pos(x, startPosition.y(), startPosition.z());
        }
        return positions;
    }

}
