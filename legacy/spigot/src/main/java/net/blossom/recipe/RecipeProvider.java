package net.blossom.recipe;

import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

public interface RecipeProvider {

    @NotNull Recipe provide();

}
