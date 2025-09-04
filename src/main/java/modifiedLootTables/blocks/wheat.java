package modifiedLootTables.blocks;

import lombok.Getter;
import lombok.Setter;
import modifiedLootTables.customCropTable;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class wheat implements customCropTable {
    public wheat(NamespacedKey key) {
        // Loot Table Constructor
        this.key = key;
    }


    @Override
    public @NotNull Collection<ItemStack> populateLoot(@Nullable Random random, @NotNull LootContext lootContext) {
        Collection<ItemStack> items = new ArrayList<>();
        items.add(new ItemStack(Material.WHEAT_SEEDS, getLootValue() - 1));
        items.add(new ItemStack(Material.WHEAT, getLootValue()));
        return items;
    }

    public void fillInventory(@NotNull Inventory inventory, @Nullable Random random, @NotNull LootContext lootContext) {
        for (ItemStack item : populateLoot(random, lootContext)) {
            inventory.addItem(item);
        }
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }

    @Setter @Getter
    private int lootValue;
    private final NamespacedKey key;
}
