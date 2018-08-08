/*
 * Copyright (c) 2018 to Le Thinh
 */

package io.gtihub.lethinh.enchanter;

import org.bukkit.Keyed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.Recipe;

public class OnCraftEvent implements Listener {

    @EventHandler
    public void onCraft(PrepareItemCraftEvent event) {
        Recipe recipe = event.getRecipe();

        if (!(recipe instanceof Keyed)) {
            return;
        }

        Keyed keyed = (Keyed) recipe;

        if(!keyed.getKey().getKey().equals("Enchanter")) {
            return;
        }

        event.getInventory().setResult(recipe.getResult());
    }

}
