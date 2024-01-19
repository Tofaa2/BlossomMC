package net.blossom.core.utils;

import net.minestom.server.MinecraftServer;
import net.minestom.server.event.server.ServerTickMonitorEvent;
import net.minestom.server.monitoring.TickMonitor;

import java.util.concurrent.atomic.AtomicReference;

public class ServerProfiler {

    private static final AtomicReference<TickMonitor> tickMonitor = new AtomicReference<>();

    public static void start() {
        MinecraftServer.getGlobalEventHandler().addListener(ServerTickMonitorEvent.class, event -> {
            tickMonitor.set(event.getTickMonitor());
        });
    }


    public static long getTotalMemory() {
        return Runtime.getRuntime().totalMemory() / 1024 / 1024;
    }

    public static long getUsedMemory() {
        return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024;
    }

    public static long getFreeMemory() {
        return Runtime.getRuntime().freeMemory() / 1024 / 1024;
    }

    public static double getTps() {
        return Math.round(MinecraftServer.TICK_MS / getMspt()); // Round to 2 decimal places
    }

    public static void gc() {
        System.gc();
    }

    public static double getMspt() {
        return tickMonitor.get() == null ? -1 : tickMonitor.get().getTickTime();
    }


}
