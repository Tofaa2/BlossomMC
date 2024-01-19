package net.blossom.core.utils;

import net.blossom.core.Blossom;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.NBTException;
import org.jglrxavpok.hephaistos.parser.SNBTParser;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public final class InventoryUtils {

    private InventoryUtils(){}

    public static Map<Integer, String> serializeInventory(Inventory inventory) {
        Map<Integer, String> serialized = new HashMap<>();
        IntStream.range(0, inventory.getSize()).forEach(i -> {
            ItemStack stack = inventory.getItemStack(i);
            if (stack.isAir()) return;
            serialized.put(i, serializeStack(stack));
        });
        return serialized;
    }

    public static Map<Integer, String> serializeInventory(Map<Integer, ItemStack> inventory) {
        Map<Integer, String> serialized = new HashMap<>();
        for (var entry : inventory.entrySet()) {
            ItemStack stack  = entry.getValue();
            int i = entry.getKey();
            if (stack.isAir()) continue;
            serialized.put(i, serializeStack(stack));
        }
        return serialized;
    }

    public static void deserializeInventory(Inventory inventory, Map<Integer, String> serialized) {
        for (var entry : serialized.entrySet()) {
            ItemStack stack = deserializeStack(entry.getValue());
            if (stack.isAir()) {
                Blossom.LOGGER.warn("ItemStack is air: {}", entry.getValue());
                continue;
            }
            inventory.setItemStack(entry.getKey(), stack);
        }
    }

    public static Map<Integer, ItemStack> deserializeInventory(Map<Integer, String> serialized) {
        Map<Integer, ItemStack> inventory = new HashMap<>();
        for (var entry : serialized.entrySet()) {
            ItemStack stack = deserializeStack(entry.getValue());
            if (stack.isAir()) {
                Blossom.LOGGER.warn("ItemStack is air: {}", entry.getValue());
                continue;
            }
            inventory.put(entry.getKey(), stack);
        }
        return inventory;
    }

    public static String serializeStack(ItemStack stack) {
        return stack.toItemNBT().toSNBT();
    }

    public static ItemStack deserializeStack(String serialized) {
        try {
            StringReader reader = new StringReader(serialized);
            SNBTParser parser = new SNBTParser(reader);
            return ItemStack.fromItemNBT((NBTCompound) parser.parse());
        }
        catch (NBTException e) {
            Blossom.LOGGER.error("Failed to deserialize itemstack", e);
            return ItemStack.AIR;
        }
    }


}
