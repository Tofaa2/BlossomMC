package net.blossom.core;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.ping.ResponseData;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.Difficulty;


public final class Blossom {

    public static final ComponentLogger LOGGER = ComponentLogger.logger("Blossom");

    public static NamespaceID newId(String value) {
        return NamespaceID.from("blossom", value);
    }

    //FINAL_DAMAGE = ( DMG * (1 + ( STR / 100 ) ) - (DMG * (DEF/(100+DEF)))
    public static double calculateFinalDamage(double base, double strength, double criticalChance, double criticalMultiplier, double defense) {
        double finalDamage = base * (1 + (strength / 100)) - (base * (defense / (100 + defense)));
        if (finalDamage <= 1) finalDamage = 1;
        if (Math.random() <= criticalChance) {
            finalDamage *= criticalMultiplier;
        }
        return finalDamage;
    }

    public static void main(String[] args) {
        MinecraftServer minestom = MinecraftServer.init();
        MinecraftServer.setBrandName("Blossom");
        MinecraftServer.setDifficulty(Difficulty.HARD);
        Feature.loadFeatures();

        ResponseData responseData = new ResponseData();
        responseData.setDescription(Component.text("Welcome to Blossom!"));
        MinecraftServer.getGlobalEventHandler().addListener(ServerListPingEvent.class, event -> event.setResponseData(responseData));

        MojangAuth.init();

        minestom.start(
                System.getProperty("blossom.host", "0.0.0.0"),
                Integer.parseInt(System.getProperty("blossom.port", "25565"))
        );
    }

}
