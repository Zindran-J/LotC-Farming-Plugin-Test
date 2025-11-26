package modifiedLootTables;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.jetbrains.annotations.NotNull;

/**
 * @param seed     Carrots, Wheat Seeds, etc.
 * @param cropDrop Netherwart, Wheat, etc.
 */
public record customCrop(ItemStack seed, ItemStack cropDrop) {

    public void adjustCropValues(int newCropValue) {
        seed.setAmount(newCropValue);
        cropDrop.setAmount(newCropValue);
    }

    public void fillInventory(@NotNull LootContext lootContext, @NotNull Inventory inventory, int value) {
        // Adjust crop values to get the numbers we want
        this.adjustCropValues(value);

        inventory.addItem(this.seed);
        inventory.addItem(this.cropDrop);

        // In the end, reset them back to default (1)
        this.adjustCropValues(1);
    }
}
