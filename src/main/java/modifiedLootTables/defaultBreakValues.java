package modifiedLootTables;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.ItemStack;
import scheduleHandler.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class defaultBreakValues implements Listener {
    Map<Material, Material> seeds = new HashMap<>() {{
        put(Material.CARROTS, Material.CARROT);
        put(Material.POTATOES, Material.POTATO);
        put(Material.BEETROOTS, Material.BEETROOT_SEEDS);
        put(Material.WHEAT, Material.WHEAT_SEEDS);
        put(Material.NETHER_WART, Material.NETHER_WART);
        put(Material.COCOA, Material.COCOA_BEANS);
        put(Material.SUGAR_CANE, Material.SUGAR_CANE);
        put(Material.CACTUS, Material.CACTUS);
        put(Material.MELON, Material.MELON_SEEDS);
        put(Material.MELON_STEM, Material.MELON_SEEDS);
        put(Material.PUMPKIN, Material.PUMPKIN_SEEDS);
        put(Material.PUMPKIN_STEM, Material.PUMPKIN_SEEDS);
        put(Material.RED_MUSHROOM, Material.RED_MUSHROOM);
        put(Material.RED_MUSHROOM_BLOCK, Material.RED_MUSHROOM);
        put(Material.BROWN_MUSHROOM, Material.BROWN_MUSHROOM);
        put(Material.BROWN_MUSHROOM_BLOCK, Material.BROWN_MUSHROOM);
    }};

    // This set contains ALL harvestable crops.
    final Set<Material> fullCropSet = Set.of(Material.CARROTS, Material.POTATOES, Material.BEETROOTS, Material.WHEAT,
            Material.NETHER_WART, Material.COCOA, Material.SUGAR_CANE, Material.CACTUS, Material.MELON, Material.PUMPKIN,
            Material.RED_MUSHROOM, Material.RED_MUSHROOM_BLOCK,
            Material.BROWN_MUSHROOM, Material.BROWN_MUSHROOM_BLOCK);

    // This set contains all crops that can be trampled by a liquid (Lava, Water, etc.)
    final Set<Material> liquidAffectedCropSet = Set.of(Material.CARROTS, Material.POTATOES, Material.BEETROOTS,
            Material.WHEAT, Material.NETHER_WART, Material.COCOA, Material.SUGAR_CANE, Material.CACTUS,
            Material.MELON_STEM, Material.PUMPKIN_STEM, Material.RED_MUSHROOM, Material.BROWN_MUSHROOM);

    final Set<Material> validFarmland = Set.of(Material.FARMLAND, Material.SOUL_SAND, Material.SAND);

    public void dropSingleItem(Block crop) {
        crop.getWorld().dropItemNaturally(
                crop.getLocation(),
                new ItemStack(seeds.get(crop.getType()), 1)
        );
    }

    // Find what block is dropping an item. If it's a crop, set the items dropped to 1 seed.
    //   This is to prevent things that aim to break the block from exploiting the system.
    @EventHandler
    public void playerCropBreak(BlockBreakEvent playerBreak) {
        Block crop = playerBreak.getBlock();
        if (fullCropSet.contains(crop.getType())) {
            playerBreak.setDropItems(false);
            dropSingleItem(crop);
        }
    }

    @EventHandler
    public void liquidCropBreak(BlockPhysicsEvent liquidBreak) {
        // This method checks for water breaking the crop itself.
        //   Note: This will activate if a liquid even touches the crop in any block connecting to it.
        Block block = liquidBreak.getBlock();
        Material source = liquidBreak.getSourceBlock().getType();
        if (source == Material.LAVA || source == Material.WATER){
            if (liquidAffectedCropSet.contains(block.getType())) {
                liquidBreak.setCancelled(true);
                handler.placeBlock(block, Material.AIR.createBlockData());
                dropSingleItem(block);
            }
        }
    }

    @EventHandler
    public void pistonPushCropBreak(BlockPistonExtendEvent physicsBreak) {
        // This method prevents a piston from pushing the crop itself or its supporting block.
        for (Block block : physicsBreak.getBlocks()) {
            if (fullCropSet.contains(block.getType()) || validFarmland.contains(block.getType())) {
                physicsBreak.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void pistonPullCropBreak(BlockPistonRetractEvent physicsBreak) {
        // This method prevents a piston from pulling the crop's supporting block.
        for (Block block : physicsBreak.getBlocks()) {
            if (validFarmland.contains(block.getType())) {
                physicsBreak.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void trampleCropBreak (EntityChangeBlockEvent interaction) {
        // This method makes it so trampling a crop will only ever give the seed back.
        //   Note: Why not just disable trampling entirely...?
        Block  block = interaction.getBlock();
        if (block.getType() == Material.FARMLAND) {
            Block crop = block.getRelative(BlockFace.UP);
            if (fullCropSet.contains(crop.getType())) {
                dropSingleItem(crop);
                crop.setType(Material.AIR);
            }
        }
    }
}

