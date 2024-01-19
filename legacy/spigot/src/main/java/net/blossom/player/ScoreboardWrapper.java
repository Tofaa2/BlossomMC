package net.blossom.player;

import net.blossom.utils.UnicodeCharacters;
import net.blossom.core.Blossom;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.api.sidebar.component.ComponentSidebarLayout;
import net.megavex.scoreboardlibrary.api.sidebar.component.SidebarComponent;
import net.megavex.scoreboardlibrary.api.sidebar.component.animation.CollectionSidebarAnimation;
import net.megavex.scoreboardlibrary.api.sidebar.component.animation.SidebarAnimation;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class ScoreboardWrapper {

    private static final Component RANK_COMPONENT = Component.text("  Rank: ", NamedTextColor.YELLOW);
    private static final Component BALANCE_COMPONENT = Component.text("  Balance: ", NamedTextColor.YELLOW);
    private static final Component ONLINE_COUNT_LINE = Component.text("  Online: ", NamedTextColor.YELLOW);
    private static final Component TPS_LINE = Component.text("  TPS: ", NamedTextColor.YELLOW);
    private static final Component SERVER_TIME = Component.text("  Time: ", NamedTextColor.YELLOW);
    private static final Component LIVES_COMPONENT = Component.text("  Lives: ", NamedTextColor.YELLOW);
    private final BlossomPlayer player;
    private final Sidebar sidebar;
    private final ComponentSidebarLayout layout;
    private final SidebarAnimation<Component> titleAnimation;
    private boolean enabled = false;

    ScoreboardWrapper(BlossomPlayer player) {
        this.player = player;
        titleAnimation = createGradientAnimation(Component.text("Blossom MC"));
        var title = SidebarComponent.animatedLine(titleAnimation);
        SidebarComponent builder = SidebarComponent.builder()
                .addDynamicLine(() -> RANK_COMPONENT.append(player.getRank().getPrefixComponent()))
                .addDynamicLine(() -> BALANCE_COMPONENT.append(Component.text(player.getBalance() + UnicodeCharacters.COINS_ICON, NamedTextColor.GOLD)))
                .addDynamicLine(() -> LIVES_COMPONENT.append(Component.text(player.getLives(), NamedTextColor.RED)))
                .addBlankLine()
                .addDynamicLine(() -> ONLINE_COUNT_LINE.append(Component.text(Bukkit.getOnlinePlayers().size(), NamedTextColor.WHITE)))
                .addDynamicLine(() -> TPS_LINE.append(getTpsString()))
                .addDynamicLine(() -> SERVER_TIME.append(Component.text(Blossom.getServerTime(player.getWorld()), NamedTextColor.WHITE)))
                .build();
        layout = new ComponentSidebarLayout(title, builder);
        sidebar = Blossom.getScoreboardLibrary().createSidebar();
        layout.apply(sidebar);
    }


    private static Component getTpsString() {
        double[] tps = Bukkit.getTPS();
        double roundedTps = Math.round(tps[0] * 100.0) / 100.0; // Round to 2 decimal places
        NamedTextColor color;
        if (roundedTps >= 18.0) {
            color = NamedTextColor.GREEN;
        } else if (roundedTps >= 15.0) {
            color = NamedTextColor.YELLOW;
        } else {
            color = NamedTextColor.RED;
        }
        return Component.text(roundedTps, color);
    }

    void tick() {
        if (!enabled) {
            return;
        }
        titleAnimation.nextFrame();
        layout.apply(sidebar);
    }

    void enable() {
        sidebar.addPlayer(player.getPlayer());
        enabled = true;
    }

    void disable() {
        enabled = false;
        sidebar.removePlayer(player.getPlayer());
        sidebar.close();
    }

    private static @NotNull SidebarAnimation<Component> createGradientAnimation(@NotNull Component text) {
        float step = 1f / 8f;

        TagResolver.Single textPlaceholder = Placeholder.component("text", text);
        List<Component> frames = new ArrayList<>((int) (2f / step));

        float phase = -1f;
        while (phase < 1) {
            frames.add(MiniMessage.miniMessage().deserialize("<bold><gradient:#00ffe1:#ff00a2:" + phase + "><text>", textPlaceholder));
            phase += step;
        }

        return new CollectionSidebarAnimation<>(frames);
    }

}
