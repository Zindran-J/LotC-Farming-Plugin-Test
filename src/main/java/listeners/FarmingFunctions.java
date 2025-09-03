package listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class FarmingFunctions implements Listener{
    // Make a 'Harvest' function that activates when right-clicking a crop
    // When right-clicking the crop, check to see what item the player is holding and pass it and the crop
    //   to the 'Harvestable' function.
    // If the 'Harvestable' function returns FALSE, do nothing. Else, complete HarvestActivity function.
    @EventHandler
    public void harvest(PlayerInteractEvent e) {
        boolean harvestable;
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player user = e.getPlayer(); // Get player
            // Get the block that was clicked, and what its type is.
            Material block = e.getClickedBlock() != null ? e.getClickedBlock().getType() : null;
            // Get the tool that clicked on the block.
            ItemStack tool = e.getItem();
            // Check Harvestability, assuming that there is an item in hand and a block being aimed at.
            if (tool != null && block != null) {
                harvestable = checkHarvestability(tool, block);
            } else { harvestable = false; }
            if (harvestable) {
                e.getClickedBlock().breakNaturally();
            }
        }
    }
    public boolean checkHarvestability(ItemStack item, Material crop) {
        boolean isHoe = item.getType().name().endsWith("HOE");
        boolean isAxe = item.getType().name().endsWith("AXE");
        return switch (crop) {
            case Material.CARROTS, Material.POTATOES, Material.BEETROOTS, Material.WHEAT, Material.NETHER_WART, Material.SUGAR_CANE, Material.CACTUS -> isHoe;
            case Material.MELON, Material.PUMPKIN, Material.BAMBOO -> isAxe;
            default -> false;
        };
    }
}
