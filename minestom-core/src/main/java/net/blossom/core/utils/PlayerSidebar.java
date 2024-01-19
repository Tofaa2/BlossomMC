package net.blossom.core.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.minestom.server.MinecraftServer;
import net.minestom.server.Viewable;
import net.minestom.server.entity.Player;
import net.minestom.server.scoreboard.Sidebar;
import net.minestom.server.timer.ExecutionType;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public final class PlayerSidebar implements Viewable {

    private final Sidebar sidebar;
    private List<Supplier<Component>> lines = new ArrayList<>();
    private Supplier<Component> title;
    private Collection<Player> viewers;
    private Task syncTask;

    public PlayerSidebar(Supplier<Component> title, @Nullable Duration updateInterval) {
        this.title = title;
        this.sidebar = new Sidebar(title.get());
        if (updateInterval != null) {
            this.syncTask = MinecraftServer.getSchedulerManager().scheduleTask(this::update, TaskSchedule.duration(updateInterval), TaskSchedule.duration(updateInterval), ExecutionType.ASYNC);
        }
    }

    public PlayerSidebar(Component title, @Nullable Duration updateInterval) {
        this(() -> title, updateInterval);
    }

    public PlayerSidebar(Supplier<Component> title) {
        this(title, null);
    }

    public PlayerSidebar(Component title) {
        this(() -> title, null);
    }


    public void addLine(Supplier<Component> line) {
        lines.add(line);
        sidebar.createLine(new Sidebar.ScoreboardLine(String.valueOf(lines.size() - 1), line.get(), lines.size() - 1));
        reconstructPositions();
    }
    // if we have 9 lines, 9th line is at 0, 8th line is at 1, 7th line is at 2, etc.
    private void reconstructPositions() {
        for (var line : sidebar.getLines()) {
            int index = Integer.parseInt(line.getId());
            int position = lines.size() - index - 1;
            sidebar.updateLineScore(line.getId(), position);
        }
    }

    public void addLine(Component line) {
        addLine(() -> line);
    }

    public void setLine(int line, Supplier<Component> content) {
        lines.set(line, content);
    }

    public void setLine(int line, Component content) {
        setLine(line, () -> content);
    }

    public void removeLine(int line) {
        lines.remove(line);
    }

    public void clearLines() {
        lines.clear();
    }

    public void setTitle(Supplier<Component> title) {
        this.title = title;
    }

    public void setTitle(Component title) {
        setTitle(() -> title);
    }

    public void setInterval(@Nullable Duration updateInterval) {
        if (syncTask != null) {
            syncTask.cancel();
        }
        if (updateInterval != null) {
            this.syncTask = MinecraftServer.getSchedulerManager().scheduleTask(this::update, TaskSchedule.duration(updateInterval), TaskSchedule.duration(updateInterval), ExecutionType.ASYNC);
        }
    }

    public void update() {
        for (int i = 0; i < lines.size(); i++) {
            updateLine(i);
        }
        updateTitle();
    }

    public void updateLine(int line) {
        sidebar.updateLineContent(String.valueOf(line), lines.get(line).get());
    }

    public void updateTitle() {
        sidebar.setTitle(title.get());
    }

    public void hide() {
        var viewers = sidebar.getViewers();
        this.viewers = viewers;
        viewers.forEach(sidebar::removeViewer);
    }

    public void show() {
        viewers.forEach(sidebar::addViewer);
        viewers = null;
    }

    @Override
    public boolean addViewer(@NotNull Player player) {
        return sidebar.addViewer(player);
    }

    @Override
    public boolean removeViewer(@NotNull Player player) {
        return sidebar.removeViewer(player);
    }

    @Override
    public @NotNull Set<@NotNull Player> getViewers() {
        return sidebar.getViewers();
    }

    public static @NotNull List<Component> createGradientAnimation(@NotNull Component text) {
        float step = 1f / 32f;

        TagResolver.Single textPlaceholder = Placeholder.component("text", text);
        List<Component> frames = new ArrayList<>((int) (2f / step));

        float phase = -1f;
        while (phase < 1) {
            frames.add(MiniMessage.miniMessage().deserialize("<bold><gradient:#00ffe1:#ff00a2:" + phase + "><text>", textPlaceholder));
            phase += step;
        }

        return frames;
    }

}
