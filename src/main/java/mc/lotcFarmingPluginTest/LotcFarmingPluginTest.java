package mc.lotcFarmingPluginTest;

import listeners.FarmingFunctions;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import scheduleHandler.handler;

//TODO:
// - 1.0: Start adjustment on crop drop table.
// - 1.1: If harvested with a wooden/stone tool, drop 1 crop and 1 seed. 2 crops for carrots/potatoes.
// - 1.2. If harvested with an iron/gold tool, drop 2 crops and 1 seed. 3 crops for carrots/potatoes.
// - 1.3. If harvested with a diamond/netherite tool, drop 3 crops and 1 seed. 4 crops for carrots/potatoes.
// - 2.0: Adjust how enchantments work on tools, and make it so breaking crops with any tool/weapon does nothing and
//        removes no durability, unless it's right clicked by the correct tool.
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
// - 2.4: The only exception to these tables I imagine is for Melons. Melons naturally give 4-6 per block, so we'll do 4
//        naturally, and add half of the current per level, rounded down.
//        - Fortune 1 ~ 6
//        - Fortune 2 ~ 9
//        - Fortune 3 ~ 13
// - 3.0: Alter the crop break event to make it only ever drop 1 crop/seed if somehow broken by anything other than the
//        correct tool. Such things that can do this are pistons, crop trampling, darkness, water, lava, explosions,
//        or a piston moving the supporting block. Mob griefing should be noted to also cause explosions and trampling.

public final class LotcFarmingPluginTest extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Farming Plugin Loaded!");
        // Start Listener
        getServer().getPluginManager().registerEvents(new FarmingFunctions(), this);
        getServer().getPluginManager().registerEvents(new handler(this), (Plugin) this);
    }

    @Override
    public void onDisable() {
        // Shut down plugin functions.
        getLogger().info("Farming Plugin turned off.");
        HandlerList.unregisterAll(this);
    }
}
