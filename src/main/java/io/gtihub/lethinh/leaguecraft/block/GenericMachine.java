/*
 * Copyright (c) 2018 to Le Thinh
 */

package io.gtihub.lethinh.leaguecraft.block;

import io.gtihub.lethinh.leaguecraft.LeagueCraft;
import io.gtihub.lethinh.leaguecraft.block.impl.BlockEnchanter;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

/**
 * Define a real machine, with its name and obtaining {@link BlockMachine} of it
 * (Backend of my Mantle plugin, too lazy to recode shits, by the way)
 */
public enum GenericMachine {

    ENCHANTER {
        @Override
        public BlockMachine createBlockMachine(Block block, String... players) {
            return new BlockEnchanter(block, players);
        }
    };

    public String getName() {
        return name().toLowerCase();
    }

    public ItemStack getStackForBlock() {
        for (ItemStack stack : LeagueCraft.getPlugin(LeagueCraft.class).stacks) {
            if (!stack.getType().isBlock()) {
                continue;
            }

            String locName = stack.getItemMeta().getLocalizedName().replace("leaguecraft_", "");

            if (locName.equalsIgnoreCase(getName())) {
                return stack;
            }
        }

        return null;
    }

    public BlockMachine createBlockMachine(Block block, String... players) {
        throw new UnsupportedOperationException();
    }

}
