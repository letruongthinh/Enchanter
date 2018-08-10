/*
 * Copyright (c) 2018 to Le Thinh
 */

package io.gtihub.lethinh.leaguecraft;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Configuration {

    private FileConfiguration config;

    public Configuration(LeagueCraft plugin) {
        config = new YamlConfiguration();

        config.set("enchant_successful_rate", 0.3D);

        for (int i = 0; i < plugin.enchantStoneLevels.length; ++i) {
            setStackName("enchant_stone_level_" + (i + 1), "&5Đá cường hóa cấp " + (i + 1));
            setStackLore("enchant_stone_level_" + (i + 1), "&4Nâng cấp vật phẩm");
        }

        setStackName("enchant_keep_stone", "&5Đá giữ vật phẩm khi cường hóa");
        setStackLore("enchant_keep_stone", "Giữ vật phẩm khi đã cường hóa item");

        setStackName("enchant_luck_stone", "&5Đá tăng may mắn khi cường hóa");
        setStackLore("enchant_luck_stone", "Tăng tỉ lệ cường hóa của vật phẩm");
    }

    public void load(File file) {
        if (!file.exists()) {
            return;
        }

        config = YamlConfiguration.loadConfiguration(file);
        LeagueCraft plugin = LeagueCraft.getPlugin(LeagueCraft.class);

        for (int i = 0; i < plugin.enchantStoneLevels.length; ++i) {
            ItemStack enchantStone = plugin.enchantStoneLevels[i];
            ItemMeta meta = enchantStone.getItemMeta();
            meta.setDisplayName(getStackName("enchant_stone_level" + (i + 1)));
            meta.setLore(getStackLore("enchant_stone_level" + (i + 1)));
            plugin.enchantStoneLevels[i].setItemMeta(meta);
        }
    }

    public void save(File file) throws IOException {
        config.save(file);
    }

    public String getStackName(String stack) {
        return config.getString(stack);
    }

    public void setStackName(String stack, String name) {
        config.set(stack, name);
    }

    public List<String> getStackLore(String stack) {
        return config.getStringList(stack);
    }

    public void setStackLore(String stack, String... lore) {
        config.set(stack, Arrays.asList(lore));
    }

    public FileConfiguration getConfig() {
        return config;
    }

}
