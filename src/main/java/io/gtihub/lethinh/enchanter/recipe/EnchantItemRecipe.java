/*
 * Copyright (c) 2018 to Le Thinh
 */

package io.gtihub.lethinh.enchanter.recipe;

import org.bukkit.inventory.ItemStack;

/**
 * Tease this urself, I only add enchantment output/upgrade recipe
 */
public class EnchantItemRecipe implements IEnchantRecipe<ItemStack> {

    private final ItemStack output;
    private final ItemStack[] ingredients;

    public EnchantItemRecipe(ItemStack output, ItemStack[] ingredients) {
        this.output = output;
        this.ingredients = ingredients;
    }

    @Override
    public ItemStack getOutput() {
        return output.clone();
    }

    @Override
    public ItemStack[] getIngredients() {
        return ingredients;
    }

}
