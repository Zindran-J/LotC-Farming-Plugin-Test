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

//TODO:
// - 1.0: Start adjustment on crop drop table.
// - 1.1: If harvested with a wooden/stone tool, drop 1 crop and 1 seed. 2 crops for carrots/potatoes.
// - 1.2. If harvested with an iron/gold tool, drop 2 crops and 1 seed. 3 crops for carrots/potatoes.
// - 1.3. If harvested with a diamond/netherite tool, drop 3 crops and 1 seed. 4 crops for carrots/potatoes.
// - 2.0: Adjust how enchantments work on tools, and make it so breaking crops with any tool/weapon does nothing and
//        removes no durability, unless it's right clicked by the correct tool. Use BlockDropItemEvent to find when
//        this happens.
// - 2.1: Unbreaking 1 should increase durability by 1.5x, and further levels by another 0.5
//        That means Unbreaking 1 = 1.5x, Unbreaking 2 = 2x, Unbreaking 3 = 2.5x, etc. It's basically (0.5*level + 1)x.
//        Which can work for plugins that increase enchantment levels, maybe.
// - 2.2: Each level of Fortune should increase the crop/seed drop by 1. Crops remain the same, but add an extra crop
//        per 2 seeds if the crop is its own seed, which are carrots, potatoes, nether wart, sugarcane, cactus, cocoa,
//        and pumpkins.
//        This means (sc = seed-crop):
//        - Fortune 1 = 2 crop 2 seed (3 crop sc)
//        - Fortune 2 = 3 crop 3 seed (4 crop sc)
//        - Fortune 3 = 4 crop 4 seed (6 crop sc)
//        Note: for the calculation on seeds just use modulo 2 to get an easy full number.
// - 2.3: For plants that are replantable, automatically reduce the number of "seeds" given by 1 prior to distribution.
// - 3.0: Alter the crop break event to make it only ever drop 1 crop/seed if somehow broken by anything other than the
//        correct tool. Such things that can do this are pistons, crop trampling, darkness, water, lava, explosions,
//        or a piston moving the supporting block. Mob griefing should be noted to also cause explosions and trampling.

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
