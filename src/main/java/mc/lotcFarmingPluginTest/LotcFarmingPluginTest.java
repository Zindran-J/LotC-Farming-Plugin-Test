package mc.lotcFarmingPluginTest;

import org.bukkit.plugin.java.JavaPlugin;

// TODO:
// 1.0 Make a 'Harvest' function that activates when right clicking a crop
// 1.1 When right clicking the crop, check to see what item the player is holding.
// 1.1.0 Create a 'Tool_Check' function that takes the parameters [crop] and [tool] then returns either TRUE or FALSE.
//       This will make it extremely easy to modify what tools can break what crops, as it's essentially just a large switch.

// 1.2 For Beetroot, Wheat, Carrots, Potato, Nether Wart, Sugarcane, Cactus, and Brown/Red Mushrooms, the correct tools are:
//     wooden_hoe, stone_hoe, iron_hoe, gold_hoe, diamond_hoe, and netherite_hoe.
//     Note: For Sugarcane and Cactus, make sure to also check the NBT of the block to make sure the block was NOT placed
//           by a player. If placed by player, only give 1 block in return. This will be the origin tag.
// 1.3 For Pumpkins, Melons, and Brown/Red Mushroom Blocks, the correct tools are:
//     wooden_axe, stone_axe, iron_axe, gold_axe, diamond_axe, and netherite_axe.
// 1.4 If the incorrect tool is being used, do nothing and print "This is the wrong tool to harvest with!"
//     If the correct tool is used but the crop is not mature, do nothing and print "This crop is not yet ready to harvest."
//
// 2.0 Alter the crop break event to make it drop no crops or seeds if broken by anything other than the correct tool
// 2.1 This includes things like pistons, crop trampling, darkness, water, or explosions.
//
// 3.0 Create a command that toggles crop trampling on/off for an individual player.
// 3.1 This will be /croptrample <enable/disable> <user>
// 3.2 The command must persist over restarts/shutdowns, and is NOT togglable by the player themselves.

public final class LotcFarmingPluginTest extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
