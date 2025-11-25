package scheduleHandler;

import mc.lotcFarmingPluginTest.LotcFarmingPluginTest;
import modifiedLootTables.customCropTable;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.loot.LootContext;
import org.yaml.snakeyaml.Yaml;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;

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

    public static boolean enchantActivation (double activationChance, String enchant) {
        /*
            We use > here because Math.random() returns a number between 0.0 (inclusive) and 1.0 (exclusive).
            For a visual example (imagine these are lines from 1.0 to 0.0 with the same rules as above):
              x is a valid durability damage roll, 0 is when unbreaking stops the damage.
            No unbreaking: [ x x x x x x x x x x x x ]
            Unbreaking 1:  [ 0 0 0 0 0 0 x x x x x x ]
            Unbreaking 2:  [ 0 0 0 0 0 0 0 0 x x x x ]
            Unbreaking 3:  [ 0 0 0 0 0 0 0 0 0 x x x ]
            If we used < then we would invert the x's and 0's, effectively making the enchant worse at higher levels.
            We add different cases to allow possible custom enchants to be added in the future.

            Note: This function checks to see if the enchantment activates. If it does, it will return True.
        */
        switch (enchant) {
            case "Unbreaking":
                double rando = Math.random();
                 return rando > activationChance;
            case "Fortune": return Math.random() > activationChance;
            default: return false;
        }
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
        if (enchantActivation(noBonusChance, "Fortune") && fortuneLevel > 0) {
            // Using the Random utility, I believe we can use .nextInt() to get the exact functionality we want.
            Random random = new Random();
            return random.nextInt(fortuneLevel);
        } else {
            return 0;
        }
    }

    public static int getDurability (ItemStack item) {
        return item.getType().getMaxDurability() - ((Damageable) Objects.requireNonNull(item.getItemMeta())).getDamage();
    }

    public static void damageItem(ItemStack item, int unbreakingModifier, Player user) {
        // Store the chance of an item losing durability 100% chance by default.
        if (item.getItemMeta() instanceof Damageable damageable) {
            int currentDurabilityLost = damageable.getDamage();
            if (!enchantActivation(((double) 1 / (1 + unbreakingModifier)),"Unbreaking")) {
                int newDurabilityLost = currentDurabilityLost + 1;
                damageable.setDamage(newDurabilityLost);
                item.setItemMeta(damageable);
                if (getDurability(item) <= 0) {
                    // Item broke!
                    user.playSound(user.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
                    user.getInventory().clear(user.getInventory().getHeldItemSlot());
                }
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
        user.playSound(clickedBlock.getLocation(), Sound.BLOCK_BIG_DRIPLEAF_TILT_UP, 2, (float) (Math.random() + Math.random()));
        damageItem(item, unbreakingLevel, user);
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

    public static List<String> parseYamlFile(String fileName) {
        Yaml yaml = new Yaml();
        if (!fileExists(fileName)) {
            System.out.println("File does not exist! Contact development as soon as possible.");
            return null;
        }
        try (InputStream is = new FileInputStream(new File(plugin.getDataFolder(), fileName))){
            // If the file exists, this will return the entire file's list contents.
            Object data = yaml.load(is);
            if (data instanceof List<?> list) {
                List<String> cleaned = new ArrayList<>();
                for (Object o : list) {
                    cleaned.add(o.toString());
                }
                return cleaned;
            }
        } catch (IOException e) {
            System.out.println("Could not parse file: " + fileName);
        }
        return new ArrayList<>();
    }

    public static void writeToYAML(String fileName, String inputString) {
        // This should ONLY be called after verifying that the file exists.
        Yaml yaml = new Yaml();
        File file = new File(plugin.getDataFolder(), fileName);
        List<String> data = parseYamlFile(fileName);
        data.add(inputString);
        try (FileWriter writer = new FileWriter(file)) {
            yaml.dump(data, writer);
        } catch (IOException e) {
            // This should only happen if someone deleted the file while it was being modified.
            System.out.println("Could not write to file: " + fileName);
            e.printStackTrace();
        }
    }

    public static boolean existsInYAML(String fileName, String inputString) {
        // This checks the entire file to see if the exact string is found within.
        // Note: This function assumes that the file exists.
        List<String> data = parseYamlFile(fileName);
        for (String s : data) {
            if (s.equals(inputString)) {
                return true;
            }
        }
        return false;
    }

    public static void deleteFromYAML(String fileName, String remove) {
        // This should ONLY be called after verifying that the file exists.
        // It is also usually only called after another function checks that the
        //   thing being removed actually exists.
        Yaml yaml = new Yaml();
        File file = new File(plugin.getDataFolder(), fileName);
        List<String> data = parseYamlFile(fileName);
        data.remove(remove);
        try (FileWriter writer = new FileWriter(file)) {
            yaml.dump(data, writer);
        } catch (IOException e) {
            // This should only happen if someone deleted the file while it was being modified.
            System.out.println("Could not write to file: " + fileName);
            e.printStackTrace();
        }
    }
}
