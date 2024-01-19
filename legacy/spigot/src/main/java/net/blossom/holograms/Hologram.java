package net.blossom.holograms;

import com.google.gson.reflect.TypeToken;
import net.blossom.core.Blossom;
import net.blossom.utils.DoNotSerialize;
import net.blossom.utils.JsonUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;

import java.io.File;

public final class Hologram {

    private static final File HOLOGRAMS_DIRECTORY = new File(Blossom.getDataFolder(), "holograms");

    private final String id;
    private Location location;
    private TextDisplay.TextAlignment alignment;
    private Component line;

    @DoNotSerialize
    private Entity linkedEntity;

    public Hologram(String id, Location location, TextDisplay.TextAlignment alignment, Component line) {
        this.id = id;
        this.location = location;
        this.alignment = alignment;
        this.line = line;
    }

    public void spawn() {
        if (this.linkedEntity != null) {
            this.linkedEntity.remove();
        }
        this.linkedEntity = location.getWorld().spawn(location, TextDisplay.class, textDisplay -> {
            textDisplay.text(line);
            textDisplay.setAlignment(alignment);
            textDisplay.setGravity(false);
            textDisplay.setSeeThrough(true);
            textDisplay.setPersistent(false);
        });
    }

    public void despawn() {
        if (this.linkedEntity != null) {
            this.linkedEntity.remove();
            this.linkedEntity = null;
        }
    }

    public void refresh() {
        despawn();
        spawn();
    }

    public void saveToStorage() {
        File file = new File(HOLOGRAMS_DIRECTORY, id + ".yml");
        JsonUtils.writeJson(file, this, TypeToken.get(Hologram.class).getType());
    }


    public boolean hasSpawned() {
        return linkedEntity != null && !linkedEntity.isDead() && !linkedEntity.isValid();
    }


}
