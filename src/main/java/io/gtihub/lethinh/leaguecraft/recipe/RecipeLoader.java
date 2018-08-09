/*
 * Copyright (c) 2018 to Le Thinh
 */

package io.gtihub.lethinh.leaguecraft.recipe;

import io.gtihub.lethinh.leaguecraft.LeagueCraft;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeLoader {

    public static final List<IEnchantRecipe> ENCHANT_RECIPES = new ArrayList<>();


    public static void init() {
        LeagueCraft plugin = LeagueCraft.instance;

        {
            ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "enchanter"), LeagueCraft.enchanter);
            recipe.shape("ABA", "BCB", "ABA");
            recipe.setIngredient('A', Material.EMERALD_BLOCK);
            recipe.setIngredient('B', Material.IRON_BARDING);
            recipe.setIngredient('C', Material.ENCHANTMENT_TABLE);
            Bukkit.addRecipe(recipe);
        }

        {
            ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "enchant_pedestal"), LeagueCraft.enchantPedestal);
            recipe.shape("AAA", "BBB", "CCC");
            recipe.setIngredient('A', Material.IRON_BARDING);
            recipe.setIngredient('B', Material.ANVIL);
            recipe.setIngredient('C', Material.REDSTONE);
            Bukkit.addRecipe(recipe);
        }

        // Enchantment
        ENCHANT_RECIPES.add(new EnchantRecipe(Enchantment.SILK_TOUCH, new ItemStack(Material.EMERALD), new ItemStack(Material.QUARTZ)));
        ENCHANT_RECIPES.add(new EnchantRecipe(Enchantment.LOOT_BONUS_BLOCKS, new ItemStack(Material.LAPIS_BLOCK)));
        ENCHANT_RECIPES.add(new EnchantRecipe(Enchantment.DAMAGE_ALL,
                new ItemStack(Material.FERMENTED_SPIDER_EYE),
                new ItemStack(Material.FEATHER),
                new ItemStack(Material.DIAMOND),
                new ItemStack(Material.BONE)));
        ENCHANT_RECIPES.add(new EnchantRecipe(Enchantment.DAMAGE_UNDEAD,
                new ItemStack(Material.FERMENTED_SPIDER_EYE),
                new ItemStack(Material.BLAZE_ROD),
                new ItemStack(Material.NETHERRACK)));
        ENCHANT_RECIPES.add(new EnchantRecipe(Enchantment.FROST_WALKER,
                new ItemStack(Material.ICE), new ItemStack(Material.STICK)));
        ENCHANT_RECIPES.add(new EnchantRecipe(Enchantment.PROTECTION_ENVIRONMENTAL,
                new ItemStack(Material.OBSIDIAN),
                new ItemStack(Material.OBSIDIAN),
                new ItemStack(Material.ICE),
                new ItemStack(Material.IRON_BARDING)));
    }


    public static IEnchantRecipe findMatchingEnchantRecipe(ItemStack... stacks) {
        for (IEnchantRecipe recipe : ENCHANT_RECIPES) {
            List<ItemStack> toCheck = new ArrayList<>();

            for (ItemStack ingredient : recipe.getIngredients()) {
                for (ItemStack stack : stacks) {
                    if (ingredient.isSimilar(stack)) {
                        toCheck.add(stack);
                        break;
                    }
                }
            }

            return toCheck.size() == recipe.getIngredients().length ? recipe : null;
        }

        return null;
    }

}
