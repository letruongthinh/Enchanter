/*
 * Copyright (c) 2018 to Le Thinh
 */

package io.gtihub.lethinh.leaguecraft;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * I'm lazy to make the code clearer and easier, because of retyping some parts of code from the
 * original project is super duper boring
 */
public final class LeagueCraft extends JavaPlugin {

    public List<ItemStack> stacks;
    public ItemStack enchanter;
    public final ItemStack enchantStoneLevels[] = new ItemStack[50];
    public ItemStack enchantLuckStone;
    public ItemStack enchantKeepStone;

    private Configuration configuration;

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        configuration = new Configuration(this);

        registerStacks();

        configuration.load(new File(getDataFolder(), "league_craft.yml"));

        IOMachines.loadMachines(this);

        try {
            IOMachines.loadMachinesData(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        getServer().getPluginManager().registerEvents(new MachineChangedEvent(), this);
    }

    @Override
    public void onDisable() {
        try {
            // lazy af to do the log
            IOMachines.saveMachines(this);
            IOMachines.saveMachinesData(this);
            configuration.save(new File(getDataFolder(), "league_craft.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public boolean isEnchanterBase(Block block) {
        return stacks.stream().anyMatch(stack -> stack.getType() == block.getType() && stack.getData().getData() == block.getData());
    }

    private void registerStacks() {
        stacks = new ArrayList<>();
        enchanter = new ItemStack(Material.ENCHANTMENT_TABLE);

        {
            ItemMeta meta = enchanter.getItemMeta();
            meta.setLocalizedName("leaguecraft_enchanter");
            meta.setDisplayName(ChatColor.DARK_GREEN + "Enchanter");
            meta.addEnchant(Enchantment.MENDING, 10, true);
            enchanter.setItemMeta(meta);
        }

        stacks.add(enchanter);

        for (int i = 0; i < enchantStoneLevels.length; ++i) {
            ItemStack enchantStone = new ItemStack(Material.EMERALD);
            ItemMeta meta = enchantStone.getItemMeta();
            int level = i + 1;
            meta.setLocalizedName("leaguecraft_enchant_stone_level_" + level);
            meta.setDisplayName(configuration.getStackName("enchant_stone_level_" + level));
            meta.setLore(configuration.getStackLore("enchant_stone_level_" + level));
            meta.addEnchant(Enchantment.DAMAGE_ALL, level, true);
            meta.addEnchant(Enchantment.DAMAGE_UNDEAD, level, true);
            enchantStone.setItemMeta(meta);
            enchantStoneLevels[i] = enchantStone;
            stacks.add(enchantStone);
        }

        enchantKeepStone = new ItemStack(Material.DIAMOND);

        {
            ItemMeta meta = enchantKeepStone.getItemMeta();
            meta.setLocalizedName("leaguecraft_enchant_keep_stone");
            meta.setDisplayName(configuration.getStackName("enchant_keep_stone"));
            meta.setLore(configuration.getStackLore("enchant_keep_stone"));
            meta.addEnchant(Enchantment.LOOT_BONUS_MOBS, 10, true);
            meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 10, true);
            enchantKeepStone.setItemMeta(meta);
        }

        stacks.add(enchantKeepStone);

        enchantLuckStone = new ItemStack(Material.DIAMOND);

        {
            ItemMeta meta = enchantLuckStone.getItemMeta();
            meta.setLocalizedName("leaguecraft_enchant_luck_stone");
            meta.setDisplayName(configuration.getStackName("enchant_luck_stone"));
            meta.setLore(configuration.getStackLore("enchant_luck_stone"));
            meta.addEnchant(Enchantment.LOOT_BONUS_MOBS, 10, true);
            meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 10, true);
            enchantLuckStone.setItemMeta(meta);
        }

        stacks.add(enchantLuckStone);
    }

}
