package net.blossom.hub.features;

import net.blossom.core.Blossom;
import net.blossom.core.BlossomPlayer;
import net.blossom.commons.CollectionUtils;
import net.blossom.core.utils.PlayerSidebar;
import net.blossom.hub.features.cosmetics.Cosmetic;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.network.player.PlayerConnection;
import net.minestom.server.sound.SoundEvent;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.*;
import java.util.function.Supplier;

import static net.blossom.core.utils.PlayerSidebar.createGradientAnimation;

public class HubPlayer extends BlossomPlayer {

    private static final Supplier<Component> TITLE_ANIMATION = CollectionUtils.listToSupplier(createGradientAnimation(Component.text("Blossom MC")));
    private static final Component SPLITTER = Component.text("--------------------", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE);
    private static final Supplier<Component> TOTAL_ONLINE = () -> Component.text("Online: ", NamedTextColor.YELLOW).append(Component.text(Blossom.getTotalOnline(), NamedTextColor.GOLD));

    final Document document;
    private PlayerSidebar playerSidebar;
    private Set<Cosmetic<?>> ownedCosmetics;
    private Cosmetic<?> activeCosmetic;

    public HubPlayer(
            @NotNull UUID uuid,
            @NotNull String username,
            @NotNull PlayerConnection playerConnection,
            @NotNull Document document
    ) {
        super(uuid, username, playerConnection);
        this.document = document;
        scheduleNextTick(e -> init());
    }

    public void init() {
        setupSidebar();
        setActiveCosmetic(Cosmetic.getByName(document.getString("active-cosmetic")));
        this.ownedCosmetics = new HashSet<>();
        if (document.containsKey("owned-cosmetics")) {
            List<String> ownedCosmetics = document.getList("owned-cosmetics", String.class);
            for (String cosmeticName : ownedCosmetics) {
                Cosmetic<?> cosmetic = Cosmetic.getByName(cosmeticName);
                if (cosmetic != null) {
                    this.ownedCosmetics.add(cosmetic);
                }
            }
        }
    }

    @Override
    public void update(long time) {
        super.update(time);
    }

    @Override
    public void tick(long time) {
        super.tick(time);
    }

    public void attemptDoubleJump() {
        setFlying(false);
        setAllowFlying(false);
        scheduleNextTick(e -> {
            setVelocity(getPosition().direction().mul(50).withY(10));
            playSound(Sound.sound(
                    SoundEvent.ENTITY_GENERIC_EXPLODE,
                    Sound.Source.PLAYER,
                    1.0f,
                    1.0f
            ));
            setAllowFlying(true);
        });
        playSound(Sound.sound(
                SoundEvent.ENTITY_GENERIC_EXPLODE,
                Sound.Source.PLAYER,
                1.0f,
                1.0f
        ));
    }

    public @Nullable Cosmetic<?> getActiveCosmetic() {
        return activeCosmetic;
    }

    public void setActiveCosmetic(@Nullable Cosmetic<?> cosmetic) {
        if (cosmetic == null) {
            sendMessage("<red>You do not own this cosmetic.");
        }
        if (this.activeCosmetic != null) {
            if (this.activeCosmetic.onUnequip() != null) {
                this.activeCosmetic.onUnequip().accept(this);
            }
        }
        this.activeCosmetic = cosmetic;
        if (cosmetic == null) {
            document.remove("active-cosmetic");
        }
        else {
            document.put("active-cosmetic", cosmetic.name());
            sendMessage("<green>Active cosmetic set to <gold>" + cosmetic.name() + "<green>.");
            if (cosmetic.onEquip() != null) {
                cosmetic.onEquip().accept(this);
            }
        }
    }

    public boolean hasCosmetic(Cosmetic<?> cosmetic) {
        return ownedCosmetics.contains(cosmetic);
    }

    public void addOwnedCosmetic(Cosmetic<?> cosmetic) {
        ownedCosmetics.add(cosmetic);
        refreshOwnedCosmetics();
    }

    public void removeOwnedCosmetic() {
        ownedCosmetics.remove(activeCosmetic);
        refreshOwnedCosmetics();
    }

    private void refreshOwnedCosmetics() {
        List<String> ownedCosmetics = new ArrayList<>();
        for (Cosmetic<?> ownedCosmetic : this.ownedCosmetics) {
            ownedCosmetics.add(ownedCosmetic.name());
        }
        document.put("owned-cosmetics", ownedCosmetics);
    }

    public @NotNull Set<Cosmetic<?>> getOwnedCosmetics() {
        return ownedCosmetics;
    }


    public Map<String, Float> getParkourTimes() {
        if (!document.containsKey("parkour-times")) {
            document.put("parkour-times", new HashMap<String, Float>());
        }
        return (Map<String, Float>) document.get("parkour-times", Map.class);
    }

    public PlayerSidebar getPlayerSidebar() {
        return playerSidebar;
    }

    public int getShards() {
        return document.getInteger("shards", 0);
    }

    public void setShards(int shards) {
        document.put("shards", shards);
    }

    public void addShards(int shards) {
        setShards(getShards() + shards);
    }

    public void removeShards(int shards) {
        setShards(getShards() - shards);
    }

    private void setupSidebar() {
        playerSidebar = new PlayerSidebar(TITLE_ANIMATION, Duration.ofMillis(100));
        playerSidebar.addLine(SPLITTER);
        playerSidebar.addLine(() -> Component.text("Shards: ", NamedTextColor.YELLOW).append(Component.text(getShards(), NamedTextColor.GOLD)));
        playerSidebar.addLine(TOTAL_ONLINE);
        scheduleNextTick((e) -> playerSidebar.addViewer(this));
    }
}
