package mc.lotcFarmingPluginTest;

import commands.croptrample;
import listeners.FarmingFunctions;
import lombok.Getter;
import modifiedLootTables.blocks.*;
import modifiedLootTables.customCropTable;
import modifiedLootTables.defaultBreakValues;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import scheduleHandler.handler;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class LotcFarmingPluginTest extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Loading Farming Plugin...");

        // Initialize Tables
        getLogger().info("Initializing Loot Tables...");
        this.beetrootTable = new beetroots(new NamespacedKey(this, "beetroot_drops"));
        this.brownMushroomTable = new brownMushroom(new NamespacedKey(this, "brown_mushroom_drops"));
        this.brownMushroomBlockTable = new brownMushroomBlock(new NamespacedKey(this, "brown_mushroom_block_drops"));
        this.redMushroomTable = new redMushroom(new NamespacedKey(this, "red_mushroom_drops"));
        this.redMushroomBlockTable = new redMushroomBlock(new NamespacedKey(this, "red_mushroom_block_drops"));
        this.cactusTable = new cactus(new NamespacedKey(this, "cactus_drops"));
        this.carrotTable = new carrots(new NamespacedKey(this, "carrot_drops"));
        this.cocoaTable = new cocoa(new NamespacedKey(this, "cocoa_drops"));
        this.melonTable = new melon(new NamespacedKey(this, "melon_drops"));
        this.netherwartTable = new netherwart(new NamespacedKey(this, "netherwart_drops"));
        this.potatoTable = new potatoes(new NamespacedKey(this, "potato_drops"));
        this.pumpkinTable = new pumpkin(new NamespacedKey(this, "pumpkin_drops"));
        this.sugarcaneTable = new sugarcane(new NamespacedKey(this, "sugarcane_drops"));
        this.wheatTable = new wheat(new NamespacedKey(this, "wheat_drops"));
        this.lootTables = new HashMap<>() {{
            put(Material.BEETROOTS, getBeetrootTable());
            put(Material.BROWN_MUSHROOM, getBrownMushroomTable());
            put(Material.BROWN_MUSHROOM_BLOCK, getBrownMushroomBlockTable());
            put(Material.RED_MUSHROOM, getRedMushroomTable());
            put(Material.RED_MUSHROOM_BLOCK, getRedMushroomBlockTable());
            put(Material.CACTUS, getCactusTable());
            put(Material.CARROTS, getCarrotTable());
            put(Material.COCOA, getCocoaTable());
            put(Material.MELON, getMelonTable());
            put(Material.NETHER_WART, getNetherwartTable());
            put(Material.POTATOES, getPotatoTable());
            put(Material.PUMPKIN, getPumpkinTable());
            put(Material.SUGAR_CANE, getSugarcaneTable());
            put(Material.WHEAT, getWheatTable());
        }};
        getLogger().info("Loot Tables Initialized.");

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
        File croptrampleCsv = new File(getDataFolder(), "croptrample.csv");

        // Check to see if the file exists as plugins/Lotc-Farming-Plugin-Test/croptrample.csv.
        // If it doesn't, create the csv folder.
        try {
            if (!croptrampleCsv.exists()) {
                getLogger().info("Creating csv file...");
                if (!croptrampleCsv.createNewFile()) {
                    getLogger().warning("Failed to create csv file!");
                } else {
                    getLogger().info("CSV file created.");
                }
            } else  {
                getLogger().info("CSV file already exists.");
            }
        } catch (IOException error) {
            getLogger().severe("Failed to create croptrample.csv file in plugins/Lotc-Farming-Plugin-Test!");
        }

        // Initialize the command if it's set up properly in the plugin.yml file. If it doesn't log a severe error.
        if (this.getCommand("croptrample") != null) {
            Objects.requireNonNull(this.getCommand("croptrample")).setExecutor(new croptrample());
        } else {
            getLogger().severe("No croptrample command exists in plugin.yml!");
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

    public customCropTable getLootTable (Material material) {
        return lootTables.get(material);
    }


    // The way this should work is:
    // 1. ####Table is initialized.
    // 2. The Getter function is stored as a value inside a map with a material key.
    // 3.0 To access ####Table specifically, we call getLootTable with the material key.
    // 3.1 With that key, we call lootTables and pass the key which gives us the Getter function.
    // 3.2 That Getter function automatically returns the requested table.
    @Getter
    private beetroots beetrootTable;
    @Getter
    private brownMushroom brownMushroomTable;
    @Getter
    private brownMushroomBlock brownMushroomBlockTable;
    @Getter
    private redMushroom redMushroomTable;
    @Getter
    private redMushroomBlock redMushroomBlockTable;
    @Getter
    private cactus cactusTable;
    @Getter
    private carrots carrotTable;
    @Getter
    private cocoa cocoaTable;
    @Getter
    private melon melonTable;
    @Getter
    private netherwart netherwartTable;
    @Getter
    private potatoes potatoTable;
    @Getter
    private pumpkin pumpkinTable;
    @Getter
    private sugarcane sugarcaneTable;
    @Getter
    private wheat wheatTable;
    @Getter
    private Map<Material, customCropTable> lootTables;
}
