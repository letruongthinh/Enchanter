/*
 * Copyright (c) 2018 to Le Thinh
 */

package io.gtihub.lethinh.leaguecraft.recipe;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class EnchantRecipe implements IEnchantRecipe<Enchantment> {

    private final Enchantment output;
    private final ItemStack[] ingredients;

    public EnchantRecipe(Enchantment output, ItemStack... ingredients) {
        this.output = output;
        this.ingredients = ingredients;
    }

    @Override
    public Enchantment getOutput() {
        return output;
    }

    @Override
    public ItemStack[] getIngredients() {
        return ingredients;
    }

}
