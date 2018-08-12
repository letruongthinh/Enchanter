/*
 * Copyright (c) 2018 to Le Thinh
 */

package io.gtihub.lethinh.leaguecraft.block.impl;

import io.gtihub.lethinh.leaguecraft.LeagueCraft;
import io.gtihub.lethinh.leaguecraft.Timer;
import io.gtihub.lethinh.leaguecraft.block.BlockMachine;
import io.gtihub.lethinh.leaguecraft.block.GenericMachine;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
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
    private final Timer timer = new Timer();
    private boolean resetTimer = true;
    private short damage = 0;

    public BlockEnchanter(Block block, String... players) {
        super(GenericMachine.ENCHANTER, block, 54, "Enchanter", players);

        addItemMatrix0(0, TO_ENCHANT, (short) 1);
        addItemMatrix0(6, OUTPUT, (short) 5);

        for (int i = 12; i < 15; ++i) {
            HELPER_SLOTS.add(i);
            inventory.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE));
        }

        for (int i = 27; i < inventory.getSize(); ++i) {
            if (i >= INGREDIENTS_START && i <= INGREDIENTS_END || i == LUCK_INCREASE || i == KEEP_ENCHANT) {
                continue;
            }

            HELPER_SLOTS.add(i);
            inventory.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE));
        }
    }

    @Override
    public void handleUpdate(LeagueCraft plugin) {
        runnable.runTaskTimerAsynchronously(plugin, DEFAULT_DELAY, DEFAULT_PERIOD);
    }

    @Override
    public void work() {
        if (resetTimer) {
            timer.reset();
            resetTimer = false;
            return;
        }

        ItemStack toEnchant = inventory.getItem(TO_ENCHANT);

        // Calculate next item level
        ItemMeta meta = toEnchant.getItemMeta();

        Pattern pattern = Pattern.compile("\\[\\d+]$");
        String input;

        if (meta.hasDisplayName()) {
            input = meta.getDisplayName();
        } else {
            String[] split = toEnchant.getType().name().toLowerCase().split("_");
            input = Arrays.stream(split).map(StringUtils::capitalize).collect(Collectors.joining(" "));
        }

        int[] curItemLevel = new int[]{0};// Not thread-safe
        Matcher matcher = pattern.matcher(input);

        if (matcher.matches()) {
            for (int i = 0; i < matcher.groupCount(); ++i) {
                System.out.print(i + " ");
            }

            curItemLevel[0] = Integer.parseInt(matcher.group(1));
            meta.setDisplayName(matcher.replaceAll("") + " [" + curItemLevel[0] + 1 + "]");
        } else {
            meta.setDisplayName(input + " [1]");
        }

        LeagueCraft plugin = LeagueCraft.getPlugin(LeagueCraft.class);

        int[] testLevels = IntStream.rangeClosed(INGREDIENTS_START, INGREDIENTS_END).mapToObj(i -> inventory.getItem(i))
                .filter(stack -> stack != null && plugin.stacks.stream().anyMatch(s -> s.isSimilar(stack)))
                .mapToInt(stack -> Integer.parseInt(stack.getItemMeta().getLocalizedName().substring("leaguecraft_enchant_stone_level_".length())))
                .toArray();
        boolean canUpgrade = testLevels.length == 3 && Arrays.stream(testLevels).allMatch(i -> i == curItemLevel[0] + 1);

        if (!canUpgrade) {
            resetTimer = true;
            return;
        }

        if (!timer.isDelayComplete(5000L)) {
            for (int i = 12; i < 15; ++i) {
                if (damage > 14) {
                    damage = 0;
                }

                inventory.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, damage));
                ++damage;
            }

            return;
        }

        // Remove upgrade stones
        IntStream.rangeClosed(INGREDIENTS_START, INGREDIENTS_END).forEach(i -> inventory.setItem(i, null));

        // Can item be enchanted successfully?
//        double chance = getEnchantChance(plugin);
//        double need = plugin.getConfiguration().getConfig().getDouble("enchant_successful_rate");
//
//        if (chance < need) {
//            return;
//        }

        // Should the enchanter keep the input item?
        ItemStack keep = inventory.getItem(KEEP_ENCHANT);

        if (!plugin.enchantKeepStone.isSimilar(keep)) {
            inventory.setItem(TO_ENCHANT, null);
        }

        if (meta.hasEnchant(Enchantment.DAMAGE_ALL)) {
            int lastEnchantLevel = meta.getEnchantLevel(Enchantment.DAMAGE_ALL);
            meta.removeEnchant(Enchantment.DAMAGE_ALL);
            meta.addEnchant(Enchantment.DAMAGE_ALL, lastEnchantLevel + 1, true);
        } else {
            meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
        }

        if (meta.hasEnchant(Enchantment.DAMAGE_UNDEAD)) {
            int lastEnchantLevel = meta.getEnchantLevel(Enchantment.DAMAGE_UNDEAD);
            meta.removeEnchant(Enchantment.DAMAGE_UNDEAD);
            meta.addEnchant(Enchantment.DAMAGE_UNDEAD, lastEnchantLevel + 1, true);
        } else {
            meta.addEnchant(Enchantment.DAMAGE_UNDEAD, 1, true);
        }

        for (int i = 12; i < 15; ++i) {
            HELPER_SLOTS.add(i);
            inventory.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE));
        }

        toEnchant.setItemMeta(meta);
        inventory.setItem(OUTPUT, toEnchant);
        resetTimer = true;
    }

    @Override
    public boolean canWork() {
        if (inventory.getItem(TO_ENCHANT) != null && inventory.getItem(OUTPUT) == null) {
            return true;
        }

        for (int i = 12; i < 15; ++i) {
            HELPER_SLOTS.add(i);
            inventory.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE));
        }

        return false;
    }

    private void addItemMatrix0(int start, short center, short damage) {
        int offset = start;

        for (int row = 0; row < 3; ++row) {
            for (int i = offset; i < offset + 3; ++i) {
                if (i == center) {
                    continue;
                }

                inventory.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, damage));
                HELPER_SLOTS.add(i);
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
    public void onMachinePlaced(Player player, ItemStack heldItem) {
        timer.reset();
    }

    @Override
    public boolean onInventoryInteract(ClickType clickType, InventoryAction action, InventoryType.SlotType slotType, ItemStack clicked, ItemStack cursor, int slot, InventoryView view, HumanEntity player) {
        return HELPER_SLOTS.contains(slot) && clicked.getType() == Material.STAINED_GLASS_PANE;
    }

}
