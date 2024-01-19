package net.blossom.hub.features.cosmetics;

import net.blossom.commons.DescriptionParser;
import net.blossom.core.BlossomCommand;
import net.blossom.core.gui.Gui;
import net.blossom.core.gui.GuiButton;
import net.blossom.core.gui.misc.ConfirmGui;
import net.blossom.core.utils.ComponentUtils;
import net.blossom.core.utils.ProgressBar;
import net.blossom.hub.features.HubPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static net.blossom.commons.StringUtils.fancyName;

public class CosmeticsCommand extends BlossomCommand {

    private final Component title = Component.text("Cosmetics", NamedTextColor.GOLD);
    private final Component bracketOne = ComponentUtils.normal("[", NamedTextColor.GRAY);
    private final Component bracketTwo = ComponentUtils.normal("]", NamedTextColor.GRAY);

    public CosmeticsCommand() {
        super("cosmetics");
        addSyntax((commandSender, commandContext) -> {
            HubPlayer player = (HubPlayer) commandSender;
            player.openGui(createTypesGui(player));
        });
    }

    private Gui createTypesGui(HubPlayer player) {
        Gui.Builder typesGui = Gui.builder(InventoryType.CHEST_6_ROW, title)
                .withFiller(GuiButton.FILLER)
                .withButton(49, GuiButton.RETURN);
        Collection<Cosmetic<?>> cosmetics = Cosmetic.getAll();
        for (var entry : CosmeticType.buttonValues.entrySet()) {
            int slot = entry.getKey();
            CosmeticType type = entry.getValue();
            int total = 0;
            int owned = 0;
            for (Cosmetic<?> cosmetic : cosmetics) {
                if (cosmetic.type() == type) {
                    total++;
                }
                if (player.hasCosmetic(cosmetic)) {
                    owned++;
                }
            }
            float percent;
            if (owned == 0 || total == 0) {
                percent = 0.0f;
            }
            else {
                percent = (float) owned / total;
            }
            Component progress = ProgressBar.create(
                    percent, 15, "-", NamedTextColor.GOLD, NamedTextColor.GREEN
            );
            typesGui.withButton(
                    slot,
                    new GuiButton(
                            ItemStack.builder(type.getMaterial())
                                    .displayName(type.getFancyName())
                                    .lore(
                                            Component.empty(),
                                            ComponentUtils.normal(
                                                    "Owned: " + owned + "/" + total, NamedTextColor.GOLD
                                            ),
                                            bracketOne.append(progress).append(bracketTwo).append(ComponentUtils.normal(
                                                    " " + (int) (percent * 100) + "% Completed", NamedTextColor.GOLD
                                            )),
                                            ComponentUtils.normal(
                                                    "Click to view cosmetics", NamedTextColor.GRAY
                                            )
                                    )
                                    .build(),
                            (gui, p, clickType) -> {
                                Gui cosmeticGui = createCosmeticGui(player, type);
                                cosmeticGui.open(p);
                            }
                    )
            );
        }
        return typesGui.build();
    }

    private Gui createCosmeticGui(HubPlayer player, CosmeticType type) {
        Gui.Builder cosmeticGui = Gui.builder(InventoryType.CHEST_6_ROW, type.getFancyName())
                .withFiller(GuiButton.FILLER)
                .withButton(49, GuiButton.RETURN);
        List<Cosmetic<?>> cosmetics = Cosmetic.getAll().stream()
                .filter(cosmetic -> cosmetic.type() == type)
                .toList();
        for (int i = 0; i < cosmetics.size(); i++) {
            Cosmetic<?> cosmetic = cosmetics.get(i);
            List<Component> lore = new ArrayList<>();
            lore.add(Component.empty());
            DescriptionParser.parse(cosmetic.description()).forEach(s -> lore.add(ComponentUtils.normal(s, NamedTextColor.GRAY)));
            if (player.hasCosmetic(cosmetic)) {
                lore.add(ComponentUtils.normal("Click to equip", NamedTextColor.DARK_GREEN));
            } else {
                lore.add(ComponentUtils.normal("Click to purchase", NamedTextColor.GOLD));
                lore.add(ComponentUtils.normal("Cost: " + cosmetic.shardsCost(), NamedTextColor.GOLD));
            }
            lore.add(Component.empty());
            lore.add(ComponentUtils.normal("Rarity: " + fancyName(cosmetic.rarity()), cosmetic.rarity().getColor()));

            GuiButton button = new GuiButton(
                    ItemStack.builder(cosmetic.icon())
                            .displayName(ComponentUtils.normal(cosmetic.name(), cosmetic.rarity().getColor()))
                            .lore(lore)
                            .build(),
                    (gui, p, clickType) -> {
                        if (player.hasCosmetic(cosmetic)) {
                            player.setActiveCosmetic(cosmetic);
                        }
                        else {
                            if (player.getShards() >= cosmetic.shardsCost()) {
                                Gui g = ConfirmGui.create(
                                        Component.text("Are you sure you want to purchase this cosmetic?", NamedTextColor.GRAY),
                                        (gui1, player1, clickType1) -> {
                                            gui1.close(player1);
                                            ((HubPlayer) player1).addOwnedCosmetic(cosmetic);
                                            ((HubPlayer) player1).setActiveCosmetic(cosmetic);
                                            ((HubPlayer) player1).removeShards(cosmetic.shardsCost());
                                        },
                                        (gui12, player12, clickType12) -> gui12.openParent(player12),
                                        cosmeticGui.build()
                                );
                                g.open(p);
                            }
                        }
                    }
            );
            cosmeticGui.withButton(i, button);
        }
        return cosmeticGui.build();
    }

}
