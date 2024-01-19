package net.blossom.item.update;

import com.google.gson.JsonObject;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface ItemComponentGenerator<T extends ItemComponent> {

    @NotNull Class<?> getComponentClass();

    @NotNull T generate(JsonObject json);

    @NotNull T generate(ItemStack stack);


}
