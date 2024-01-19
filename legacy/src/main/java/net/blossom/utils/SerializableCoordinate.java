package net.blossom.utils;

import net.minestom.server.coordinate.Vec;

public interface  SerializableCoordinate {

    static net.minestom.server.coordinate.Pos fromJson(String json) {
        Pos pos = JsonUtils.castJson(json, Pos.class);
        if (pos == null) return null;
        return pos.toPos();
    }

    static Pos fromPos(net.minestom.server.coordinate.Pos pos) {
        return new Pos(pos.x(), pos.y(), pos.z(), pos.yaw(), pos.pitch());
    }

    static Point fromPoint(net.minestom.server.coordinate.Point point) {
        return new Point(point.x(), point.y(), point.z());
    }

    record Point(double x, double y, double z) implements SerializableCoordinate {
        @Override
        public net.minestom.server.coordinate.Point toPoint() {
            return new Vec(x, y, z);
        }

        @Override
        public net.minestom.server.coordinate.Pos toPos() {
            return new net.minestom.server.coordinate.Pos(x, y, z, 0, 0);
        }
    }

     record Pos(double x, double y, double z, float yaw, float pitch) implements SerializableCoordinate{
        @Override
        public net.minestom.server.coordinate.Point toPoint() {
            return new Vec(x, y, z);
        }

        @Override
        public net.minestom.server.coordinate.Pos toPos() {
            return new net.minestom.server.coordinate.Pos(x, y, z, yaw, pitch);
        }
    }

    double x();

    double y();

    double z();

    net.minestom.server.coordinate.Point toPoint();

    net.minestom.server.coordinate.Pos toPos();

}
