/*
 * Copyright (c) 2018 to Le Thinh
 */

package io.gtihub.lethinh.leaguecraft.recipe;

import org.bukkit.inventory.ItemStack;

public interface IEnchantRecipe<T> {

    T getOutput();

    ItemStack[] getIngredients();


}
