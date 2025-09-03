package scheduleHandler;

import mc.lotcFarmingPluginTest.LotcFarmingPluginTest;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.Listener;

public class handler implements Listener {
    // This class will only ever be used for functions that require the scheduler to function.

    // Get the plugin itself, and set the static variable for the scheduler to use
    static LotcFarmingPluginTest plugin;
    public handler(LotcFarmingPluginTest plugin) {
        handler.plugin = plugin;
    }

    public static void placeBlock(Block oldBlock, BlockData newBlock) {
        // This function takes the location and data of the old block, and replaces it with the data of the new block.
        Bukkit.getScheduler().runTask(plugin, () -> {
            oldBlock.setBlockData(newBlock);
        });
    }
}
