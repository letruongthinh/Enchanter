/*
 * Copyright (c) 2018 to Le Thinh
 */

package io.gtihub.lethinh.leaguecraft;

import io.gtihub.lethinh.leaguecraft.recipe.RecipeLoader;
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

    public static LeagueCraft instance;

    public List<ItemStack> stacks;
    public ItemStack enchanter;
    public ItemStack enchantPedestal;
    public ItemStack enchantStoneLevel1;
    public ItemStack enchantStoneLevel2;
    public ItemStack enchantStoneLevel3;
    public ItemStack enchantLuckStone;
    public ItemStack enchantKeepStone;

    private Configuration configuration;

    @Override
    public void onEnable() {
        instance = this;

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        configuration = new Configuration(this);
        configuration.load(new File(getDataFolder(), "league_craft.yml"));

        registerStacks();
        RecipeLoader.init();
        IOMachines.loadMachines();
        getServer().getPluginManager().registerEvents(new MachineChangedEvent(), this);
    }

    @Override
    public void onDisable() {
        try {
            // lazy af to do the log
            IOMachines.saveMachines();
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

        enchantPedestal = new ItemStack(Material.ANVIL);


        {
            ItemMeta meta = enchantPedestal.getItemMeta();
            meta.setLocalizedName("leaguecraft_enchant_pedestal");
            meta.setDisplayName(ChatColor.DARK_BLUE + "Enchant Pedestal");
            meta.addEnchant(Enchantment.LUCK, 10, true);
            enchantPedestal.setItemMeta(meta);
        }

        stacks.add(enchantPedestal);

        enchantStoneLevel1 = new ItemStack(Material.EMERALD);

        {
            ItemMeta meta = enchantStoneLevel1.getItemMeta();
            meta.setLocalizedName("leaguecraft_enchant_stone_level_1");
            meta.setDisplayName(configuration.getStackName(enchantStoneLevel1));
            meta.setLore(configuration.getStackLore(enchantStoneLevel1));
            meta.addEnchant(Enchantment.DAMAGE_ALL, 3, true);
            meta.addEnchant(Enchantment.DAMAGE_UNDEAD, 3, true);
        }

        stacks.add(enchantStoneLevel1);

        enchantStoneLevel2 = new ItemStack(Material.EMERALD);

        {
            ItemMeta meta = enchantStoneLevel2.getItemMeta();
            meta.setLocalizedName("leaguecraft_enchant_stone_level_2");
            meta.setDisplayName(configuration.getStackName(enchantStoneLevel2));
            meta.setLore(configuration.getStackLore(enchantStoneLevel2));
            meta.addEnchant(Enchantment.DAMAGE_ALL, 6, true);
            meta.addEnchant(Enchantment.DAMAGE_UNDEAD, 6, true);
        }

        stacks.add(enchantStoneLevel2);

        enchantStoneLevel3 = new ItemStack(Material.EMERALD);

        {
            ItemMeta meta = enchantStoneLevel3.getItemMeta();
            meta.setLocalizedName("leaguecraft_enchant_stone_level_3");
            meta.setDisplayName(configuration.getStackName(enchantStoneLevel3));
            meta.setLore(configuration.getStackLore(enchantStoneLevel3));
            meta.addEnchant(Enchantment.DAMAGE_ALL, 10, true);
            meta.addEnchant(Enchantment.DAMAGE_UNDEAD, 10, true);
        }

        stacks.add(enchantStoneLevel3);

        enchantKeepStone = new ItemStack(Material.IRON_INGOT);

        {
            ItemMeta meta = enchantKeepStone.getItemMeta();
            meta.setLocalizedName("leaguecraft_enchant_keep_stone");
            meta.setDisplayName(configuration.getStackName(enchantKeepStone));
            meta.setLore(configuration.getStackLore(enchantKeepStone));
            meta.addEnchant(Enchantment.DAMAGE_ALL, 10, true);
            meta.addEnchant(Enchantment.DAMAGE_UNDEAD, 10, true);
        }

        stacks.add(enchantKeepStone);

        enchantLuckStone = new ItemStack(Material.DIAMOND);

        {
            ItemMeta meta = enchantLuckStone.getItemMeta();
            meta.setLocalizedName("leaguecraft_enchant_luck_stone");
            meta.setDisplayName(configuration.getStackName(enchantLuckStone));
            meta.setLore(configuration.getStackLore(enchantLuckStone));
            meta.addEnchant(Enchantment.LOOT_BONUS_MOBS, 10, true);
            meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 10, true);
        }

        stacks.add(enchantLuckStone);
    }

}
