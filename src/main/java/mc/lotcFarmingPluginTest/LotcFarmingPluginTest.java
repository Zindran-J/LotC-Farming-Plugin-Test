package mc.lotcFarmingPluginTest;

import listeners.FarmingFunctions;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

// TODO:
// 1.0 Start adjustment on crop drop table.

// 2.0 Update the "Harvest" function to do some extra looking into Cactus and Sugarcane to prevent an infinite glitch.
// 2.1 For Sugarcane and Cactus, make sure to also check the NBT of the block to make sure the block was NOT placed
//           by a player. If placed by player, only give 1 block in return. This will be the origin tag.

// 3.1 Update the harvest check: If the incorrect tool is being used, do nothing and print "This is the wrong tool to harvest with!"
//     If the correct tool is used but the crop is not mature, do nothing and print "This crop is not yet ready to harvest."
//
// 4.0 Alter the crop break event to make it drop no crops or seeds if broken by anything other than the correct tool
// 4.1 This includes things like pistons, crop trampling, darkness, water, explosions, or a piston moving the supporting block.
//
// 5.0 Create a command that toggles crop trampling on/off for an individual player.
// 5.1 This will be /croptrample <enable/disable> <user>
// 5.2 The command must persist over restarts/shutdowns, and is NOT togglable by the player themselves.

public final class LotcFarmingPluginTest extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Farming Plugin Loaded!");
        // Start Listener
        getServer().getPluginManager().registerEvents(new FarmingFunctions(this), this);
    }

    @Override
    public void onDisable() {
        // Shut down plugin functions.
        getLogger().info("Farming Plugin turned off.");
        HandlerList.unregisterAll(this);
    }
}
