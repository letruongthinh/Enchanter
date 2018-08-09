/*
 * Copyright (c) 2018 to Le Thinh
 */

package io.gtihub.lethinh.leaguecraft.block.impl;

import io.gtihub.lethinh.leaguecraft.LeagueCraft;
import io.gtihub.lethinh.leaguecraft.block.BlockMachine;
import io.gtihub.lethinh.leaguecraft.block.GenericMachine;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BlockEnchanter extends BlockMachine {

    private static final List<Integer> HELPER_SLOTS = new ArrayList<>();
    private static final short TO_ENCHANT = 10;
    private static final short OUTPUT = 16;
    private static final short INGREDIENTS_START = 37;
    private static final short INGREDIENTS_END = 39;
    private static final short LUCK_INCREASE = 41;
    private static final short KEEP_ENCHANT = 43;

    private final Random random = new Random();

    public BlockEnchanter(Block block, String... players) {
        super(GenericMachine.ENCHANTER, block, 54, "Enchanter", players);

        addItemMatrix0(0, (short) 1);
        addItemMatrix0(6, (short) 5);

        for (int i = 12; i < 15; ++i) {
            HELPER_SLOTS.add(i);
            inventory.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE));
        }
    }

    @Override
    public void work() {
        ItemStack toEnchant = inventory.getItem(TO_ENCHANT);
        ItemStack output = inventory.getItem(OUTPUT);

        if (toEnchant == null || output != null) {
            return;
        }

        ItemMeta meta = toEnchant.getItemMeta();
        Pattern pattern = Pattern.compile("\\[\\d+]$");
        Matcher matcher = pattern.matcher(meta.getDisplayName());
        int toEnchantItemLevel = 1;

        if (matcher.matches()) {
            toEnchantItemLevel = Integer.parseInt(matcher.group(1)) + 1;
            meta.setDisplayName(matcher.replaceAll("[" + toEnchantItemLevel + "]"));
        } else {
            meta.setDisplayName(meta.getDisplayName() + " [1]");
        }

        LeagueCraft plugin = LeagueCraft.instance;
        int combinedLevel = 0;

        List<Integer> testLevels = IntStream.rangeClosed(INGREDIENTS_START, INGREDIENTS_END).mapToObj(i -> inventory.getItem(i))
                .filter(stack -> stack != null && plugin.stacks.stream()
                        .anyMatch(s -> s.getItemMeta().getLocalizedName().equals(stack.getItemMeta().getLocalizedName())))
                .mapToInt(stack -> Integer.parseInt(stack.getItemMeta().getLocalizedName().substring("leaguecraft_enchant_stone_level_".length())))
                .boxed().collect(Collectors.toList());

        for (int testLevel : new HashSet<>(testLevels)) {
            long levelContaining = testLevels.stream().filter(t -> t == testLevel).count();
            combinedLevel += testLevel * levelContaining >> 1 + 1;
        }

        if (combinedLevel < toEnchantItemLevel) {
            return;
        }

        double chance = getEnchantChance();
        double need = plugin.getConfiguration().getConfig().getDouble("enchant_successful_rate");

        if (chance < need) {
            return;
        }


        ItemStack keep = inventory.getItem(KEEP_ENCHANT);

        if (keep == null || !keep.isSimilar(plugin.enchantKeepStone)) {
            inventory.setItem(TO_ENCHANT, null);
        }

        int remainLevel = combinedLevel - toEnchantItemLevel;

        if (remainLevel > 0) {
            for (int i = INGREDIENTS_START; i <= INGREDIENTS_END; ++i) {
                if (inventory.getItem(i) != null) {
                    continue;
                }

                if (remainLevel > 10) {
                    remainLevel -= 10;
                    inventory.setItem(i, stack);
                }

                for (ItemStack stack : plugin.stacks) {
                    if (!stack.getItemMeta().getLocalizedName().startsWith("leaguecraft_enchant_stone_level_")) {
                        continue;
                    }

                    int level = Integer.parseInt(stack.getItemMeta().getLocalizedName().substring("leaguecraft_enchant_stone_level_".length()));

                    if (level == remainLevel) {
                        inventory.setItem(i, stack);
                    }
                }
            }

            // TODO Work on enchant upgrade stones from 1 -> 10
        } else {
            for (int i = INGREDIENTS_START; i <= INGREDIENTS_END; ++i) {
                if (inventory.getItem(i) != null) {
                    inventory.setItem(i, null);
                }
            }
        }

        toEnchant.setItemMeta(meta);
        inventory.setItem(OUTPUT, toEnchant);
    }

    private void addItemMatrix0(int start, short damage) {
        int offset = 0;

        for (int row = 0; row < 3; ++row) {
            for (int column = 0; column < 3; ++column) {
                int slot = start + row * column + offset;

                if (slot == TO_ENCHANT || slot == OUTPUT) {
                    continue;
                }

                HELPER_SLOTS.add(slot);
                inventory.setItem(slot, new ItemStack(Material.STAINED_GLASS_PANE, 1, damage));
            }

            offset += 9;
        }
    }

    private double getEnchantChance() {
        ItemStack luckBook = inventory.getItem(LUCK_INCREASE);
        double chance = Math.random(); // 0.0 -> 1.0;

        if (luckBook == null) {
            return chance;
        }

        return (chance + random.nextInt(4)) / 5D + 0.1D;
    }

    /* Callbacks */
    @Override
    public boolean onInventoryInteract(ClickType clickType, InventoryAction action, InventoryType.SlotType slotType, ItemStack clicked, ItemStack cursor, int slot, InventoryView view, HumanEntity player) {
        return HELPER_SLOTS.contains(slot) && clicked.getType() == Material.STAINED_GLASS_PANE;
    }

}
