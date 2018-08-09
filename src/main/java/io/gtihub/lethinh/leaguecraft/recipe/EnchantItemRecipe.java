/*
 * Copyright (c) 2018 to Le Thinh
 */

package io.gtihub.lethinh.leaguecraft.recipe;

import org.bukkit.inventory.ItemStack;

/**
 * This is for enchanting custom item and the output is also custom,
 * {@EnchantRecipe} is only for adding/upgrading enchantment for item
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
