package mc.lotcFarmingPluginTest;

import commands.croptrample;
import listeners.FarmingFunctions;
import modifiedLootTables.customCrop;
import modifiedLootTables.defaultBreakValues;
import org.bukkit.Material;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;
import scheduleHandler.handler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class LotcFarmingPluginTest extends JavaPlugin {

    @Override
    public void onEnable() {
        // Create data folder if none already exist.
        getLogger().info("Initializing command(s)...");
        if (!getDataFolder().exists()) {
            getLogger().info("Creating data folder...");
            if(!getDataFolder().mkdirs()) {
                getLogger().warning("Failed to create data folder!");
            } else {
                getLogger().info("Data folder created.");
            }
        }

        // Create a variable to point to the file.
        File croptrampleYAML = new File(getDataFolder(), "croptrample.yaml");

        // Check to see if the file exists as plugins/Lotc-Farming-Plugin-Test/croptrample.csv.
        // If it doesn't, create the csv folder.
        if (!croptrampleYAML.exists()) {
            getLogger().info("Creating YAML file...");
            Yaml yaml = new Yaml();
            try (FileWriter file = new FileWriter(croptrampleYAML)) {
                yaml.dump(new ArrayList<>(), file);
                getLogger().info("YAML file created.");
            } catch (IOException e) {
                getLogger().severe("Failed to create croptrample.yaml file in plugins/Lotc-Farming-Plugin-Test!");
            }
        } else {
            getLogger().info("YAML file already exists.");
        }

        // Initialize the command(s) if it's set up properly in the plugin.yml file. If it doesn't log a severe error.
        if (this.getCommand("croptrample") != null) {
            Objects.requireNonNull(this.getCommand("croptrample")).setExecutor(new croptrample());
        } else {
            getLogger().severe("No croptrample command exists in plugin.yml!");
        }

        if (this.getCommand("toggletrample") != null) {
            Objects.requireNonNull(this.getCommand("toggletrample")).setExecutor(new croptrample());
        } else {
            getLogger().severe("No toggletrample command exists in plugin.yml!");
        }

        getLogger().info("Command(s) initialized.");

        // Start Listener
        getLogger().info("Starting listeners...");
        getServer().getPluginManager().registerEvents(new FarmingFunctions(), this);
        getServer().getPluginManager().registerEvents(new handler(this), this);
        getServer().getPluginManager().registerEvents(new defaultBreakValues(), this);
        getLogger().info("Listeners started.");

        getLogger().info("Farming Plugin Loaded!");
    }

    @Override
    public void onDisable() {
        // Shut down plugin functions.
        getLogger().info("Farming Plugin turned off.");
        HandlerList.unregisterAll(this);
    }

    private final Map<Material, customCrop> crops = new HashMap<>() {{
        // This map is used for all our crop loot tables.
        // The Key is the block broken (ex: Material.CARROTS)
        // The drops are from the "customCrop" class.
        // - Seed, Item Drop
        put(Material.BEETROOTS, new customCrop(new ItemStack(Material.BEETROOT, 1), new ItemStack(Material.BEETROOT_SEEDS, 1)));
        put(Material.BROWN_MUSHROOM, new customCrop(new ItemStack(Material.BROWN_MUSHROOM, 1), new ItemStack(Material.BROWN_MUSHROOM, 1)));
        put(Material.BROWN_MUSHROOM_BLOCK, new customCrop(new ItemStack(Material.BROWN_MUSHROOM, 1), new ItemStack(Material.BROWN_MUSHROOM, 1)));
        put(Material.CACTUS, new customCrop(new ItemStack(Material.CACTUS, 1), new ItemStack(Material.CACTUS, 1)));
        put(Material.CARROTS, new customCrop(new ItemStack(Material.CARROT, 1), new ItemStack(Material.CARROT, 1)));
        put(Material.COCOA, new customCrop(new ItemStack(Material.COCOA_BEANS, 1), new ItemStack(Material.COCOA_BEANS, 1)));
        put(Material.MELON, new customCrop(new ItemStack(Material.MELON_SEEDS, 1), new ItemStack(Material.MELON_SLICE, 1)));
        put(Material.NETHER_WART, new customCrop(new ItemStack(Material.NETHER_WART, 1), new ItemStack(Material.NETHER_WART, 1)));
        put(Material.POTATOES, new customCrop(new ItemStack(Material.POTATO, 1), new ItemStack(Material.POTATO, 1)));
        put(Material.PUMPKIN, new customCrop(new ItemStack(Material.PUMPKIN_SEEDS, 1), new ItemStack(Material.PUMPKIN, 1)));
        put(Material.RED_MUSHROOM, new customCrop(new ItemStack(Material.RED_MUSHROOM, 1), new ItemStack(Material.RED_MUSHROOM, 1)));
        put(Material.RED_MUSHROOM_BLOCK, new customCrop(new ItemStack(Material.RED_MUSHROOM, 1), new ItemStack(Material.RED_MUSHROOM, 1)));
        put(Material.SUGAR_CANE, new customCrop(new ItemStack(Material.SUGAR_CANE, 1), new ItemStack(Material.SUGAR_CANE, 1)));
        put(Material.WHEAT, new customCrop(new ItemStack(Material.WHEAT_SEEDS, 1), new ItemStack(Material.WHEAT, 1)));
    }};

    public customCrop getLootTable (Material material) {
        return crops.get(material);
    }
}
