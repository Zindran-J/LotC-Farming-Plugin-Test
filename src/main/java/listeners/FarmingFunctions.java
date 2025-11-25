package listeners;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import scheduleHandler.handler;

//NOTE:
// If CoreProtect is added, Update the "harvestActivity" method to do some extra looking into Cactus, Sugarcane,
// and Pumpkins to prevent an infinite glitch. If the block was placed by a player, make it only ever drop 1
// item/seed, regardless of enchants on the harvesting item. Melons will never drop enough to be infinite unless
// higher levels of fortune are added past 3.

public class FarmingFunctions implements Listener {

    public enum Weights {
        //
        WOODEN (1),
        STONE (1),
        IRON (2),
        GOLDEN (2),
        DIAMOND (3),
        NETHERITE (3);

        private final int weight;
        Weights(final int weight) {
            this.weight = weight;
        }

        private int weight() { return weight; }

        public static int getWeight(String itemType) {
            if (itemType.startsWith("WOODEN")) return WOODEN.weight();
            if (itemType.startsWith("STONE")) return STONE.weight();
            if (itemType.startsWith("IRON")) return IRON.weight();
            if (itemType.startsWith("GOLDEN")) return GOLDEN.weight();
            if (itemType.startsWith("DIAMOND")) return DIAMOND.weight();
            if (itemType.startsWith("NETHERITE")) return NETHERITE.weight();
            return 0;
        }
    }

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
                if (tool.getType() == Material.BONE_MEAL) {
                    return;
                }
                if (isCrop(block)) {
                    harvestCrop(tool, block, user);
                }
            }
        }
    }

    public boolean isCrop (Block block) {
        return switch (block.getType()) {
            case Material.CARROTS, Material.POTATOES, Material.BEETROOTS, Material.WHEAT, Material.NETHER_WART,
                 Material.COCOA, Material.SUGAR_CANE, Material.CACTUS, Material.MELON, Material.PUMPKIN, Material.RED_MUSHROOM, Material.RED_MUSHROOM_BLOCK, Material.BROWN_MUSHROOM, Material.BROWN_MUSHROOM_BLOCK -> true;
            default -> false;
        };
    }

    public void harvestCrop (ItemStack item, Block clickedBlock, Player user) {
        // This method will harvest a block, assuming the correct conditions are met, which can be inferred by
        //  reading the below code and comments.
        boolean isHoe = item.getType().name().endsWith("_HOE");
        boolean isAxe = item.getType().name().endsWith("_AXE");
        boolean isMature = false;

        // Store what the crop's type, and check for maturity.
        Material cropType = clickedBlock.getType();
        if (clickedBlock.getBlockData() instanceof Ageable ageable) {
            isMature = (ageable.getAge() == ageable.getMaximumAge());
        }

        // Create a new crop block. Default age = 0, so no adjustment needed.
        // If the crop is cocoa, we also need to preserve what way it faces.
        // setAir is for when the block doesn't need to be replanted.
        BlockData setAir = Material.AIR.createBlockData();
        BlockData newCrop;
        if (clickedBlock.getType() == Material.COCOA) {
            // Get current direction, create new crop, copy direction onto new crop.
            BlockFace currentDirection = ((Directional) clickedBlock.getBlockData()).getFacing();
            newCrop = clickedBlock.getType().createBlockData();
            ((Directional) newCrop).setFacing(currentDirection);
        } else {
            newCrop = clickedBlock.getType().createBlockData();
        }

        // Create LootContext for the player
        // Note: Luck can sometimes be null somehow, so we need to validate it first.
        float luck = 0.0f;
        AttributeInstance luckAttribute = user.getAttribute(Attribute.GENERIC_LUCK);
        if (luckAttribute != null) {
            luck = (float)  luckAttribute.getValue();
        }
        LootContext playerContext = new LootContext.Builder(user.getLocation())
                .lootedEntity(user)
                .killer(user)
                .luck(luck)
                .build();

        // Get the level of fortune and/or unbreaking on the player's tool.
        // First check to see if the tool is valid, then save its weight.
        // Of course, weights are arbitrary for the trial, but this is to show it works.
        int weight = Weights.getWeight(item.getType().name());
        int unbreakingLevel = item.getEnchantmentLevel(Enchantment.UNBREAKING);
        int fortuneLevel = item.getEnchantmentLevel(Enchantment.FORTUNE);
        int newLootValue = weight + handler.getBonusDrops(fortuneLevel);

        // Adjust loot tables to reflect the fortune.
        handler.adjustLootValues(newLootValue);

        switch (cropType) {
            // Check block types for valid crops.
            case Material.CARROTS, Material.POTATOES, Material.BEETROOTS, Material.WHEAT, Material.NETHER_WART,
                 Material.COCOA:
                // For certain crops like the ones in this case, they must be checked for maturity before harvesting.
                if (isHoe) {
                    if (isMature) {
                        handler.harvestBlock(cropType, user, playerContext, clickedBlock, newCrop, item, unbreakingLevel);
                    } else {
                        user.sendMessage("This crop is not mature yet!");
                    }
                } else {
                    user.sendMessage("This is the wrong tool to harvest with...");
                }
                break;

            case Material.SUGAR_CANE, Material.CACTUS, Material.RED_MUSHROOM, Material.BROWN_MUSHROOM:
                if (!isHoe) {
                    user.sendMessage("This is the wrong tool to harvest with...");
                } else {
                    handler.harvestBlock(cropType, user, playerContext, clickedBlock, setAir, item, unbreakingLevel);
                }
                break;

            case Material.MELON, Material.PUMPKIN, Material.RED_MUSHROOM_BLOCK, Material.BROWN_MUSHROOM_BLOCK:
                if (!isAxe) {
                    user.sendMessage("This is the wrong tool to harvest with...");
                } else {
                    handler.harvestBlock(cropType, user, playerContext, clickedBlock, setAir, item, unbreakingLevel);
                }
                break;
        }
        // reset the drop values after looting the block.
        handler.adjustLootValues(0);
    }
}
