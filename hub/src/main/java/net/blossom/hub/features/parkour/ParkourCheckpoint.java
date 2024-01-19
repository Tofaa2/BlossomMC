package net.blossom.hub.features.parkour;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;

public record ParkourCheckpoint(double x, double y, double z) {

    public Point toPoint() {
        return new Vec(x, y, z);
    }

}
