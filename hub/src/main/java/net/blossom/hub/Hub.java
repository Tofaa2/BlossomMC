package net.blossom.hub;

import net.blossom.core.Blossom;
import net.blossom.dbm.BlossomDatabase;
import net.blossom.dbm.DatabaseSettings;
import net.minestom.server.MinecraftServer;

public final class Hub {

    private Hub() {}
    private static BlossomDatabase database;

    public static BlossomDatabase getDatabase() {
        return database;
    }

    public static void main(String[] args) {
        Blossom.init(() -> database = new BlossomDatabase(new DatabaseSettings(Blossom.getSettings().mongoUri(), "hub")), null);
        MinecraftServer.getSchedulerManager().buildShutdownTask(database::close);
    }

}
