package modifiedLootTables;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import scheduleHandler.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// This class is made with the sole purpose of making it impossible to bypass needing to right-click on a crop to
// get more than 1 crop.

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

    // This set contains all valid farmland blocks.
    final Set<Material> validFarmland = Set.of(Material.FARMLAND, Material.SOUL_SAND, Material.SAND);

    public void dropSingleItem(Block crop) {
        // Simply put, it drops a single specific seed.
        crop.getWorld().dropItemNaturally(
                crop.getLocation(),
                new ItemStack(seeds.get(crop.getType()), 1)
        );
    }

    @EventHandler
    public void playerCropBreak(BlockBreakEvent playerBreak) {
        // If a player breaks a crop with anything (a left click), return 1 of the crop's seed.
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
        // This method makes a piston pushing a crop or its supporting block return only one seed.
        for (Block block : physicsBreak.getBlocks()) {
            if (fullCropSet.contains(block.getType())) {
                physicsBreak.setCancelled(true);
                handler.placeBlock(block, Material.AIR.createBlockData());
                dropSingleItem(block);
                return;
            } else if (validFarmland.contains(block.getType())) {
                Block crop = block.getRelative(BlockFace.UP);
                if (fullCropSet.contains(crop.getType())) {
                    dropSingleItem(crop);
                    crop.setType(Material.AIR);
                    handler.placeBlock(crop, Material.AIR.createBlockData());
                }
                return;
            }
        }
    }

    @EventHandler
    public void pistonRetractCropBreak(BlockPistonRetractEvent physicsBreak) {
        // This method makes a piston pulling a crop's supporting block return only one seed.
        for (Block block : physicsBreak.getBlocks()) {
            if (validFarmland.contains(block.getType())) {
                Block crop = block.getRelative(BlockFace.UP);
                if (fullCropSet.contains(crop.getType())) {
                    dropSingleItem(crop);
                    crop.setType(Material.AIR);
                    handler.placeBlock(crop, Material.AIR.createBlockData());
                    return;
                }
            }
        }
    }

    @EventHandler
    public void explosionCropBreak(EntityExplodeEvent explosionBreak) {
        // This method makes crops broken by creeper or tnt explosions only drop 1 seed.
        for (Block block : explosionBreak.blockList()) {
            if (fullCropSet.contains(block.getType())) {
                dropSingleItem(block);
                block.setType(Material.AIR);
            }
        }
    }

    @EventHandler
    public void netherExplosionCropBreak(BlockExplodeEvent explosionBreak) {
        // This method makes crops broken by explosions from beds or respawn anchors only drop 1 seed.
        for (Block block : explosionBreak.blockList()) {
            if (fullCropSet.contains(block.getType())) {
                dropSingleItem(block);
                block.setType(Material.AIR);
            }
        }
    }

    @EventHandler
    public void trampleCropBreak (EntityChangeBlockEvent trampleBreak) {
        // This method makes it so trampling a crop will only ever give the seed back.
        //   Note: Why not just disable trampling entirely...?

        Block block = trampleBreak.getBlock();
        if (block.getType() == Material.FARMLAND) {
            // If the player causing the trampling is in the list of people disabled by crop trampling
            // cancel the interaction.
            if (trampleBreak.getEntity() instanceof Player player) {
                if (handler.existsInFile("croptrample.csv",player.getName())) {
                    trampleBreak.setCancelled(true);
                    return;
                }
            }

            // Otherwise, trample like normal and return the default drop of 1 seed.
            Block crop = block.getRelative(BlockFace.UP);
            if (fullCropSet.contains(crop.getType())) {
                dropSingleItem(crop);
                crop.setType(Material.AIR);
            }
        }
    }

    @EventHandler
    public void supportCropBreak(BlockPhysicsEvent supportBreak) {
        Block crop = supportBreak.getBlock();
        if (fullCropSet.contains(crop.getType())) {
            Block supportingBlock = crop.getRelative(BlockFace.DOWN);
            if (supportingBlock.getType() == Material.AIR) {
                supportBreak.setCancelled(true);
                dropSingleItem(crop);
                crop.setType(Material.AIR);
            }
        }
    }
}
