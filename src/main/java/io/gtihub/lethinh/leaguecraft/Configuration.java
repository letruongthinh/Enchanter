/*
 * Copyright (c) 2018 to Le Thinh
 */

package io.gtihub.lethinh.leaguecraft;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Configuration {

    private FileConfiguration config;

    public Configuration(LeagueCraft plugin) {
        config = new YamlConfiguration();

        config.set("enchant_successful_rate", 0.3D);

        setStackName(plugin.enchantStoneLevel1, "&2Đá cường hóa cấp 1");
        setStackLore(plugin.enchantStoneLevel1, "Nâng cấp vật phẩm");

        setStackName(plugin.enchantStoneLevel2, "&4Đá cường hóa cấp 2");
        setStackLore(plugin.enchantStoneLevel2, "Nâng cấp vật phẩm");

        setStackName(plugin.enchantStoneLevel3, "&5Đá cường hóa cấp 3");
        setStackLore(plugin.enchantStoneLevel3, "Nâng cấp vật phẩm");

        setStackName(plugin.enchantKeepStone, "&5Đá giữ vật phẩm khi cường hóa");
        setStackLore(plugin.enchantKeepStone, "Giữ vật phẩm khi đã cường hóa item");

        setStackName(plugin.enchantLuckStone, "&5Đá tăng may mắn khi cường hóa");
        setStackLore(plugin.enchantLuckStone, "Tăng tỉ lệ cường hóa của vật phẩm");
    }

    public void load(File file) {
        if (!file.exists()) {
            return;
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public void save(File file) throws IOException {
        config.save(file);
    }

    public String getStackName(ItemStack stack) {
        return config.getString(replaceOriginalName0(stack));
    }

    public void setStackName(ItemStack stack, String name) {
        config.set(replaceOriginalName0(stack), name);
    }

    public List<String> getStackLore(ItemStack stack) {
        return config.getStringList(replaceOriginalName0(stack));
    }

    public void setStackLore(ItemStack stack, String... lore) {
        config.set(replaceOriginalName0(stack), Arrays.asList(lore));
    }

    private String replaceOriginalName0(ItemStack stack) {
        return stack.getItemMeta().getLocalizedName().replaceAll("leaguecraft_", "");
    }

    public FileConfiguration getConfig() {
        return config;
    }

}
