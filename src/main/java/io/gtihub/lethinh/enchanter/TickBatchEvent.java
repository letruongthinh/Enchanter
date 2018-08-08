/*
 * Copyright (c) 2018 to Le Thinh
 */

package io.gtihub.lethinh.enchanter;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class TickBatchEvent implements Listener {

    @EventHandler
    public void onEnchanterPlaced(BlockPlaceEvent event) {
        Block block = event.getBlock();

        if (!Enchanter.instance.isEnchanterBase(block)) {
            return;
        }

        Enchanter.WORKING_BATCHES.add(block);
    }

    @EventHandler
    public void onEnchanterBroken(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (!Enchanter.instance.isEnchanterBase(block)) {
            return;
        }

        Enchanter.WORKING_BATCHES.removeIf(b -> b.getLocation().equals(block.getLocation()));
    }

    @EventHandler
    public void onEnchanterOpened(PlayerInteractEvent event) {
        ItemStack heldItem = event.getItem();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block block = event.getClickedBlock();
        Location location = block.getLocation();
        Player player = event.getPlayer();
        Enchanter plugin = Enchanter.instance;

        for (Block batch : Enchanter.WORKING_BATCHES) {
            if (!block.getLocation().equals(batch.getLocation())) {
                continue;
            }

            event.setCancelled(true);

            if (heldItem != null && block.getType() == plugin.enchantPedestal.getType() && block.getData() == plugin.enchantPedestal.getData().getData()) {
                Item item = block.getWorld().dropItemNaturally(location, new ItemStack(heldItem.getType(), 1, (short) 1, heldItem.getData().getData()));
                item.setMetadata("cannotPickup", new FixedMetadataValue(Enchanter.instance, true));
            } else if (block.getType() == plugin.enchanter.getType() && block.getData() == plugin.enchanter.getData().getData()) {
                for (Entity entity : block.getWorld().getNearbyEntities(location, 0D, 1D, 0D)) {
                    if (!(entity instanceof Item) || entity.isDead()) {
                        continue;
                    }

                    Item item = (Item) entity;
                    ItemStack stack = item.getItemStack();

                    if (stack == null) {
                        continue;
                    }

                    player.getInventory().setItemInMainHand(stack);
                }
            }
        }
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        Item item = event.getItem();

        if (item.hasMetadata("cannotPickup")) {
            event.setCancelled(true);
        }
    }

}
