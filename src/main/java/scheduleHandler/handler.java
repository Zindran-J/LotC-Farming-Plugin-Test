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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

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

    public static boolean enchantActivation (double activationChance) {
        /*
            We use < here because Math.random() returns a number between 0.0 (inclusive) and 1.0 (exclusive).
            For a visual example (imagine these are lines from 1.0 to 0.0 with the same rules as above):
              x is a valid durability damage roll, 0 is when unbreaking stops the damage.
            No unbreaking: [ x x x x x x x x x x x x ]
            Unbreaking 1:  [ 0 0 0 0 0 0 x x x x x x ]
            Unbreaking 2:  [ 0 0 0 0 0 0 0 0 x x x x ]
            Unbreaking 3:  [ 0 0 0 0 0 0 0 0 0 x x x ]
            If we used > then we would invert the x's and 0's, effectively making the enchant worse at higher levels.
        */
        return Math.random() < activationChance;
    }
    public static int getBonusDrops (int fortuneLevel) {
        /*
            For the fortune calculation, we will go with the default mechanics, except adjusted for the case of
            possibly adding higher fortune levels.

            Using the calculation to determine chance for no bonus drops, we use 2/(level + 2)
            If bonus drops happen, there is an equal chance for any number of drops between 2 and level + 1.
            - Minecraft Wiki
        */

        double noBonusChance = (double) 2/(fortuneLevel + 2);
        if (enchantActivation(noBonusChance)) {
            // Using the Random utility, I believe we can use .nextInt() to get the exact functionality we want.
            Random random = new Random();
            return random.nextInt(fortuneLevel);
        } else {
            return 0;
        }
    }

    public static void damageItem(ItemStack item, int unbreakingModifier) {
        // Store the chance of an item losing durability 100% chance by default.
        if (item.getItemMeta() instanceof Damageable damageable) {
            int currentDurabilityLost = damageable.getDamage();
            if (!enchantActivation(1.0 / (1.0 + unbreakingModifier))) {
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
        // Returns a specific crop's loot table.
        return plugin.getLootTable(material);
    }

    public static boolean fileExists(String path) {
        // This simply checks to see if a file exists in our data folder.
        File target = new File(plugin.getDataFolder(), path);
        return target.exists();
    }

    public static void writeToFile(String fileName, String inputString) {
        // This should ONLY be called after verifying that the file exists.
        File cropTrampleCSV = new File(plugin.getDataFolder(), fileName);
        try (FileWriter writer = new FileWriter(cropTrampleCSV, true)) {
            if (cropTrampleCSV.length() == 0) {
                writer.write(String.join(",",inputString));
            } else {
                writer.write(","+inputString);
            }
        } catch (IOException error) {
            plugin.getLogger().severe("Could not write to file: " + fileName);
        }
    }

    public static boolean existsInFile(String fileName, String inputString) {
        // This checks the entire folder to see if the exact string is found within.
        if (fileExists(fileName)) {
            File cropTrampleCSV = new File(plugin.getDataFolder(), fileName);

            String allLines;
            List<String> cleaned;
            try {
                // Collect every line and trim whitespace.
                allLines = Files.readString(cropTrampleCSV.toPath()).trim();
                cleaned = Arrays.stream(allLines.split(","))
                        .map(String::trim)
                        .toList();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            for (String line : cleaned) {
                if (line.equals(inputString)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void deleteFromFile(String fileName, String remove) {
        // This should ONLY be called after verifying that the file exists.
        // It is also usually only called after another function checks that the
        //   thing being removed actually exists.
        File cropTrampleCSV = new File(plugin.getDataFolder(), fileName);

        // This needs to be inside a try-catch block to make readAllLines() happy.
        String allLines;
        List<String> updatedLines;
        String newLines;
        try {
            // Collect every line and trim whitespace, then update them
            //  without keeping the specific name.
            allLines = Files.readString(cropTrampleCSV.toPath()).trim();
            updatedLines = Arrays.stream(allLines.split(","))
                    .map(String::trim)
                    .filter(user -> !user.equals(remove))
                    .toList();
            newLines = String.join(",", updatedLines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Attempt to empty the current CSV file then fill it with the updated lines.
        try {
            Files.writeString(cropTrampleCSV.toPath(), newLines, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        } catch (IOException e) {
            // This should never be reached, unless someone deletes the file mid-function.
            plugin.getLogger().severe("Someone deleted the CSV file while being modified!");
            throw new RuntimeException(e);
        }
    }
}
