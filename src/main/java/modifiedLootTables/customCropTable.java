package modifiedLootTables;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Random;

// Mock template for the loot tables
public interface customCropTable extends LootTable {
    @NotNull Collection<ItemStack> populateLoot (@Nullable Random random, @NotNull LootContext lootContext);
    void fillInventory(@NotNull Inventory inventory, @Nullable Random random, @NotNull LootContext lootContext);

    int lootValue = 0;

    void setLootValue(int fortune);
}
