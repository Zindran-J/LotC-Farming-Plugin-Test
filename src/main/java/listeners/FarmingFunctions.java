package listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import scheduleHandler.handler;

//TODO:
// Update the "Harvest" function to do some extra looking into Cactus and Sugarcane to prevent an infinite glitch.
// For Sugarcane and Cactus, make sure to also check the NBT of the block to make sure the block was NOT placed
//  by a player. If placed by player, only give 1 block in return. This will be the origin tag.

public class FarmingFunctions implements Listener{
    // Make a 'Harvest' function that activates when right-clicking a crop
    // When right-clicking the crop, check to see what item the player is holding and pass it and the crop
    //   to the 'Harvestable' function.
    // If the 'Harvestable' function returns FALSE, do nothing. Else, complete harvestActivity function.

    @EventHandler
    public void harvestActivity(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player user = e.getPlayer(); // Get player
            // Get the block that was clicked, and what its type is.
            Block block = e.getClickedBlock() != null ? e.getClickedBlock() : null;
            // Get the tool that clicked on the block.
            ItemStack tool = e.getItem();
            // Check Harvestability, assuming that there is an item in hand and a block being aimed at.
            //   If the block is harvestable, harvest it, and replant if possible.
            if (tool != null && block != null) {
               if (isCrop(block)) {
                    harvestCrop(tool, block);
               }
            }
        }
    }

    public boolean isCrop (Block block) {
        return switch (block.getType()) {
            case Material.CARROTS, Material.POTATOES, Material.BEETROOTS, Material.WHEAT, Material.NETHER_WART,
                 Material.COCOA_BEANS, Material.SUGAR_CANE, Material.CACTUS, Material.MELON, Material.PUMPKIN,
                 Material.BAMBOO -> true;
            default -> false;
        };
    }

    public void harvestCrop (ItemStack item, Block clickedBlock) {
        boolean isHoe = item.getType().name().endsWith("_HOE");
        boolean isAxe = item.getType().name().endsWith("_AXE");
        Material newCropType = clickedBlock.getType();
        BlockData newCrop = clickedBlock.getType().createBlockData();
        if (newCrop instanceof Ageable ageable) { ageable.setAge(0); }
        boolean isMature = ((Ageable) clickedBlock.getBlockData()).getAge() == ((Ageable) clickedBlock.getBlockData()).getMaximumAge();
        switch (clickedBlock.getType()) {
            case Material.CARROTS, Material.POTATOES, Material.BEETROOTS, Material.WHEAT, Material.NETHER_WART,
                 Material.COCOA:
                if (isHoe && isMature) {
                    clickedBlock.breakNaturally();
                    handler.placeBlock(clickedBlock, newCrop);
                } else {
                    break;
                }
            case Material.SUGAR_CANE, Material.CACTUS:
                if (isHoe) {
                    clickedBlock.breakNaturally();
                }
            case Material.MELON, Material.PUMPKIN, Material.BAMBOO:
                if (isAxe) {
                    clickedBlock.breakNaturally();
                }
        }
    }
}
