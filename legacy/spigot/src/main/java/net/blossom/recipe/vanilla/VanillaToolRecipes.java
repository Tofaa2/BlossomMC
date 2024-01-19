package net.blossom.recipe.vanilla;

import net.blossom.recipe.RecipeProvider;
import net.blossom.utils.DataSets;
import net.blossom.item.BlossomItem;
import org.bukkit.Material;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import static net.blossom.core.Blossom.newKey;

public final class VanillaToolRecipes {

    public static RecipeProvider[] ALL = new RecipeProvider[] {

            () -> new ShapedRecipe(newKey("wooden_sword"), BlossomItem.WOODEN_SWORD.getLinkedItem())
                    .shape(" x ", " x ", " s ")
                    .setIngredient('x', new RecipeChoice.MaterialChoice(
                            DataSets.LOGS.toArray(new Material[0])
                    ))
                    .setIngredient('s', Material.STICK),

            () -> new ShapedRecipe(newKey("stone_sword"), BlossomItem.STONE_SWORD.getLinkedItem())
                    .shape(" x ", " x ", " s ")
                    .setIngredient('x', Material.COBBLESTONE)
                    .setIngredient('s', Material.STICK),

            () -> new ShapedRecipe(newKey("iron_sword"), BlossomItem.IRON_SWORD.getLinkedItem())
                    .shape(" x ", " x ", " s ")
                    .setIngredient('x', Material.IRON_INGOT)
                    .setIngredient('s', Material.STICK),

            () -> new ShapedRecipe(newKey("diamond_sword"), BlossomItem.DIAMOND_SWORD.getLinkedItem())
                    .shape(" x ", " x ", " s ")
                    .setIngredient('x', Material.DIAMOND)
                    .setIngredient('s', Material.STICK),
            () -> new ShapedRecipe(newKey("gold_sword"), BlossomItem.GOLDEN_SWORD.getLinkedItem())
                    .shape(" x ", " x ", " s ")
                    .setIngredient('x', Material.GOLD_INGOT)
                    .setIngredient('s', Material.STICK),
            () -> new ShapedRecipe(newKey("netherite_sword"), BlossomItem.NETHERITE_SWORD.getLinkedItem())
                    .shape(" x ", " x ", " s ")
                    .setIngredient('x', Material.NETHERITE_INGOT)
                    .setIngredient('s', Material.STICK),

    };

    private VanillaToolRecipes() {}

}
