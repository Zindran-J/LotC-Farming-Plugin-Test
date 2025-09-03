package mc.lotcFarmingPluginTest;

import listeners.FarmingFunctions;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import scheduleHandler.handler;

// TODO:
// 1.0 Start adjustment on crop drop table.

// 3.1 Update the harvest check: If the incorrect tool is being used, do nothing and print "This is the wrong tool to harvest with!"
//     If the correct tool is used but the crop is not mature, do nothing and print "This crop is not yet ready to harvest."
//
// 4.0 Alter the crop break event to make it drop no crops or seeds if broken by anything other than the correct tool
// 4.1 This includes things like pistons, crop trampling, darkness, water, explosions, or a piston moving the supporting block.

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
