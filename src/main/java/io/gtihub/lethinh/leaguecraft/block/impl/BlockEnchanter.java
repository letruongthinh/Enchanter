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
    private boolean successful = true;

    public BlockEnchanter(Block block, String... players) {
        super(GenericMachine.ENCHANTER, block, 54, "Enchanter", players);

        addItemMatrix0(0, TO_ENCHANT, (short) 1);
        addItemMatrix0(6, OUTPUT, (short) 5);

        for (int i = 12; i < 15; ++i) {
            HELPER_SLOTS.add(i);
            inventory.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE));
        }
    }

    @Override
    public void work() {
        ItemStack toEnchant = inventory.getItem(TO_ENCHANT);

        // Calculate next item level
        LeagueCraft plugin = LeagueCraft.getPlugin(LeagueCraft.class);
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

        ItemMeta meta = toEnchant.getItemMeta();
        Pattern pattern = Pattern.compile("\\[\\d+]$");
        Matcher matcher = pattern.matcher(meta.getDisplayName());
        int toEnchantItemLevel = 1;

        if (matcher.matches()) {
            toEnchantItemLevel = Integer.parseInt(matcher.group(1)) + combinedLevel;

            if (toEnchantItemLevel > 50) {
                toEnchantItemLevel = 50;
            }

            meta.setDisplayName(matcher.replaceAll("[" + toEnchantItemLevel + "]"));
        } else {
            meta.setDisplayName(meta.getDisplayName() + " [1]");
        }

        // Can item be enchanted successfully?
        double chance = getEnchantChance(plugin);
        double need = plugin.getConfiguration().getConfig().getDouble("enchant_successful_rate");

        if (chance < need) {
            successful = false;
            return;
        }

        // Should the enchanter keep the input item?
        ItemStack keep = inventory.getItem(KEEP_ENCHANT);

        if (keep == null || !keep.isSimilar(plugin.enchantKeepStone)) {
            inventory.setItem(TO_ENCHANT, null);
        }

        // Calculate the remaining level after enchanted
        int remainLevel = combinedLevel - toEnchantItemLevel;

        if (remainLevel > 0) {
            for (int i = INGREDIENTS_START; i <= INGREDIENTS_END; ++i) {
                if (inventory.getItem(i) != null) {
                    continue;
                }

                for (int j = plugin.enchantStoneLevels.length - 1; j >= 0; ++j) {
                    ItemStack enchantStone = plugin.enchantStoneLevels[j];
                    int level = Integer.parseInt(enchantStone.getItemMeta().getLocalizedName().substring("leaguecraft_enchant_stone_level_".length()));

                    if (level == remainLevel) {
                        inventory.setItem(i, enchantStone);
                        remainLevel -= level;
                    }

                    if (remainLevel == 0) {
                        break;
                    }
                }
            }
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

    @Override
    public boolean canWork() {
        if (inventory.getItem(TO_ENCHANT) == null) {
            successful = true;
            return false;
        }

        return successful && inventory.getItem(TO_ENCHANT) != null && inventory.getItem(OUTPUT) == null;
    }

    private void addItemMatrix0(int start, short center, short damage) {
        int offset = start;

        for (int row = 0; row < 2; ++row) {
            for (int i = offset; i < offset + 3; ++i) {
                if (i == center) {
                    continue;
                }

                inventory.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, damage));
            }

            offset += 9;
        }
    }

    private double getEnchantChance(LeagueCraft plugin) {
        ItemStack stack = inventory.getItem(LUCK_INCREASE);
        double chance = Math.random(); // 0.0 -> 1.0;

        if (!plugin.enchantLuckStone.isSimilar(stack)) {
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
