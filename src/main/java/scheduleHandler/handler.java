package scheduleHandler;

import mc.lotcFarmingPluginTest.LotcFarmingPluginTest;
import modifiedLootTables.customCropTable;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.loot.LootContext;

import java.util.Map;

public class handler implements Listener {
    // This class will only ever be used for methods that require the scheduler to function and intermediary handling.

    // Get the plugin itself, and set the static variable for the handler to use
    static LotcFarmingPluginTest plugin;
    public handler(LotcFarmingPluginTest plugin) {
        handler.plugin = plugin;
    }

    public static void placeBlock(Block oldBlock, BlockData newBlock) {
        // This function takes the location and data of the old block, and replaces it with the data of the new block.
        Bukkit.getScheduler().runTask(plugin, () -> oldBlock.setBlockData(newBlock));
    }

    public static void adjustLootValues(int lootValue) {
        // Iterate through every single loot table
        for (Map.Entry<Material, customCropTable> entry : plugin.getLootTables().entrySet()) {
            entry.getValue().setLootValue(lootValue);
        }
    }

    public static void damageItem(ItemStack item, int unbreakingModifier) {
        // Store the chance of an item losing durability 100% chance by default.
        double damageChance = 1.0 / (1.0 + unbreakingModifier);
        if (item.getItemMeta() instanceof Damageable damageable) {
            int currentDurabilityLost = damageable.getDamage();
            // We use < here because Math.random() returns a number between 0.0 (inclusive) and 1.0 (exclusive).
            // For a visual example (imagine these are lines from 1.0 to 0.0 with the same rules as above):
            //     x is a valid durability damage roll, 0 is when unbreaking stops the damage.
            // No unbreaking: [ x x x x x x x x x x x x ]
            // Unbreaking 1:  [ 0 0 0 0 0 0 x x x x x x ]
            // Unbreaking 2:  [ 0 0 0 0 0 0 0 0 x x x x ]
            // Unbreaking 3:  [ 0 0 0 0 0 0 0 0 0 x x x ]
            // If we used > then we would invert the x's and 0's, effectively making Unbreaking worse at higher levels.
            if (Math.random() < damageChance) {
                int newDurabilityLost = currentDurabilityLost + 1;
                damageable.setDamage(newDurabilityLost);
                item.setItemMeta(damageable);
            }
        }
    }

    public static void harvestBlock(
            Material cropType,
            Player user,
            LootContext playerContext,
            Block clickedBlock,
            BlockData newCrop,
            ItemStack item,
            int unbreakingLevel) {
        getLootTable(cropType).fillInventory(user.getInventory(), null, playerContext);
        placeBlock(clickedBlock, newCrop);
        damageItem(item, unbreakingLevel);
    }

    public static customCropTable getLootTable(Material material) {
        return plugin.getLootTable(material);
    }
}
