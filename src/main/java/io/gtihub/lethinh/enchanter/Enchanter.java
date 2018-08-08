/*
 * Copyright (c) 2018 to Le Thinh
 */

package io.gtihub.lethinh.enchanter;

import io.gtihub.lethinh.enchanter.recipe.EnchantRecipe;
import io.gtihub.lethinh.enchanter.recipe.IEnchantRecipe;
import io.gtihub.lethinh.enchanter.recipe.RecipeLoader;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * I'm lazy to make the code clearer and easier, because of retyping some parts of code from the
 * original project is super duper boring
 */
public final class Enchanter extends JavaPlugin {

    public static Enchanter instance;
    public static final List<Block> WORKING_BATCHES = new ArrayList<>();

    public List<ItemStack> stacks;
    public ItemStack enchanter;
    public ItemStack enchantPedestal;

    @Override
    public void onEnable() {
        instance = this;

        if(!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        registerStacks();
        RecipeLoader.init();
        IOMachines.loadMachines();
        getServer().getPluginManager().registerEvents(new OnCraftEvent(), this);
        getServer().getPluginManager().registerEvents(new TickBatchEvent(), this);

        // Detect pedestals around and tick the enchanter
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Block block : WORKING_BATCHES) {
                    if (block.getType() != enchanter.getType() || block.getData() != enchanter.getData().getData()) {
                        continue;
                    }

                    Location enchanterLoc = block.getLocation();
                    World world = enchanterLoc.getWorld();
                    int r = 2;
                    List<Location> pedestals = new ArrayList<>();

                    for (double theta = Math.PI; theta < 2 * Math.PI; theta += 0.1D) {
                        double x = r * FastMath.cos(theta), z = r * FastMath.sin(theta);
                        Location pedestaLLoc = enchanterLoc.add(x, 0, z);

                        if (!WORKING_BATCHES.stream().map(Block::getLocation).collect(Collectors.toList()).contains(pedestaLLoc)) {
                            continue;
                        }

                        pedestals.add(pedestaLLoc);
                    }

                    if (pedestals.size() == 12) {
                        IEnchantRecipe recipe = RecipeLoader.findMatchingEnchantRecipe(pedestals.stream().flatMap(loc -> loc.getWorld().getNearbyEntities(enchanterLoc, 0D, 1D, 0D).stream()).filter(entity -> entity instanceof Item && !entity.isDead()).map(entity -> (Item) entity).map(Item::getItemStack).filter(Objects::nonNull).toArray(ItemStack[]::new));

                        if (recipe == null) {
                            continue;
                        }

                        if (recipe instanceof EnchantRecipe) {
                            // Spawn particles and remove ingredients
                            block.getWorld().spawnParticle(Particle.DRIP_LAVA, enchanterLoc, 3);
                            pedestals.forEach(loc -> {
                                loc.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, loc, 2);
                                loc.getWorld().getNearbyEntities(loc, 0D, 1D, 0D).stream().filter(entity -> entity instanceof Item &&
                                        Arrays.stream(recipe.getIngredients()).anyMatch(ingredient -> ((Item) entity).getItemStack().isSimilar(ingredient))).
                                        forEach(Entity::remove);
                            });

                            // Set output
                            Enchantment enchantment = ((EnchantRecipe) recipe).getOutput();
                            Entity entity = world.getNearbyEntities(enchanterLoc, 0D, 1D, 0D).stream().findFirst().orElse(null);

                            if (!(entity instanceof Item)) {
                                continue;
                            }

                            Item item = (Item) entity;
                            ItemStack toEnchant = item.getItemStack();
                            ItemMeta meta = toEnchant.getItemMeta();

                            if (toEnchant.getItemMeta().hasEnchant(enchantment)) {
                                meta.addEnchant(enchantment, meta.getEnchantLevel(enchantment) + 1, true);
                            } else {
                                meta.addEnchant(enchantment, 1, true);
                            }

                            toEnchant.setItemMeta(meta);
                            item.remove();
                            block.getWorld().dropItem(enchanterLoc, toEnchant);
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(this, 20L, 5L);
    }

    @Override
    public void onDisable() {
        try {
            // lazy af to do the log
            IOMachines.saveMachines();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isEnchanterBase(Block block) {
        return stacks.stream().anyMatch(stack -> stack.getType() == block.getType() && stack.getData().getData() == block.getData());
    }

    private void registerStacks() {
        stacks = new ArrayList<>();
        enchanter = new ItemStack(Material.ENCHANTMENT_TABLE);

        {
            ItemMeta meta = enchanter.getItemMeta();
            meta.setLocalizedName("enchanter_enchanter");
            meta.setDisplayName(ChatColor.DARK_GREEN + "Enchanter");
            meta.addEnchant(Enchantment.MENDING, 10, true);
            enchanter.setItemMeta(meta);
        }

        stacks.add(enchanter);

        enchantPedestal = new ItemStack(Material.ANVIL);


        {
            ItemMeta meta = enchantPedestal.getItemMeta();
            meta.setLocalizedName("enchanter_enchant_pedestal");
            meta.setDisplayName(ChatColor.DARK_BLUE + "Enchant Pedestal");
            meta.addEnchant(Enchantment.LUCK, 10, true);
            enchantPedestal.setItemMeta(meta);
        }

        stacks.add(enchantPedestal);
    }

}
