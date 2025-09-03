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
// If CoreProtect is added, Update the "harvestActivity" method to do some extra looking into Cactus, Sugarcane,
// Pumpkins, and Melons to prevent an infinite glitch. If the block was placed by a player, make it only ever drop 1
// item/seed, regardless of enchants on the harvesting item.

public class FarmingFunctions implements Listener {
    @EventHandler
    public void harvestActivity(PlayerInteractEvent e) {
        // Main method to harvest a farmable block.
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            // Collect info on the player, the block they clicked on, and what tool they are holding (if any).
            Player user = e.getPlayer();
            Block block = e.getClickedBlock() != null ? e.getClickedBlock() : null;
            ItemStack tool = e.getItem();

            // Check Harvestability, assuming that there is an item in hand and a block being aimed at.
            //   If the block is harvestable, harvest it, and replant if possible.
            if (tool != null && block != null) {
               if (isCrop(block)) {
                    harvestCrop(tool, block, user);
               }
            }
        }
    }

    public boolean isCrop (Block block) {
        return switch (block.getType()) {
            case Material.CARROTS, Material.POTATOES, Material.BEETROOTS, Material.WHEAT, Material.NETHER_WART,
                 Material.COCOA_BEANS, Material.SUGAR_CANE, Material.CACTUS, Material.MELON, Material.PUMPKIN -> true;
            default -> false;
        };
    }

    public void harvestCrop (ItemStack item, Block clickedBlock, Player user) {
        // This method will harvest a block, assuming the correct conditions are met, which can be inferred by
        //  reading the below code and comments.
        boolean isHoe = item.getType().name().endsWith("_HOE");
        boolean isAxe = item.getType().name().endsWith("_AXE");

        // Create a new crop block. Default age = 0, so no adjustment needed.
        BlockData newCrop = clickedBlock.getType().createBlockData();
        System.out.println("Material is: " + clickedBlock.getType());
        switch (clickedBlock.getType()) {
            // Check block types for valid crops.
            case Material.CARROTS, Material.POTATOES, Material.BEETROOTS, Material.WHEAT, Material.NETHER_WART,
                 Material.COCOA:
                // For certain crops like the ones in this case, they must be checked for maturity before harvesting.
                if (isHoe && (((Ageable) clickedBlock.getBlockData()).getAge() == ((Ageable) clickedBlock.getBlockData()).getMaximumAge())) {
                    clickedBlock.breakNaturally();
                    handler.placeBlock(clickedBlock, newCrop);
                } else {
                    if (!isHoe) {
                        user.sendMessage("This is the wrong tool to harvest with...");
                    } else {
                        user.sendMessage("This crop is not mature yet!");
                    }
                    break;
                }
                break;

            case Material.SUGAR_CANE, Material.CACTUS:
                if (isHoe) {
                    clickedBlock.breakNaturally();
                } else {
                    user.sendMessage("This is the wrong tool to harvest with...");
                    break;
                }

            case Material.MELON, Material.PUMPKIN:
                if (isAxe) {
                    clickedBlock.breakNaturally();
                } else {
                    user.sendMessage("This is the wrong tool to harvest with...");
                    break;
                }
        }
    }
}
