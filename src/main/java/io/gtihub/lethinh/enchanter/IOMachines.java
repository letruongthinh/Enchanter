/*
 * Copyright (c) 2018 to Le Thinh
 */

package io.gtihub.lethinh.enchanter;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.NumberConversions;

import java.io.File;
import java.io.IOException;

public final class IOMachines {

    private IOMachines() {

    }

    public static void saveMachines() throws IOException {
        if (Enchanter.WORKING_BATCHES.isEmpty()) {
            return;
        }

        File out = new File(Enchanter.instance.getDataFolder(), "tick_batches.yml");

        if (!out.exists()) {
            if (!out.createNewFile()) {
                throw new IOException("Couldn't create file tick_batches.yml!");
            }
        }

        FileConfiguration tick_batches = new YamlConfiguration();
        Enchanter.WORKING_BATCHES.forEach(batch -> tick_batches.set(serializeLocation(batch.getLocation()), batch.getType().name()));
        tick_batches.save(out);
    }

    public static void loadMachines() {
        File in = new File(Enchanter.instance.getDataFolder(), "tick_batches.yml");

        if (!in.exists()) {
            return;
        }

        FileConfiguration tickBatches = YamlConfiguration.loadConfiguration(in);

        for (String key : tickBatches.getKeys(false)) {
            Location location = deserializeLocation(key);

            if (location == null) {
                continue;
            }

            String batchType = tickBatches.getString(key);

            if (StringUtils.isBlank(batchType)) {
                continue;
            }

            Block block = location.getBlock();

            if (block.isEmpty() || block.isLiquid()) {
                continue;
            }

            Enchanter.WORKING_BATCHES.add(block);
        }
    }

    public static String serializeLocation(Location location) {
        return location.getWorld().getName() + "," + location.getBlockX() + "," + location.getBlockY() + ","
                + location.getBlockZ();
    }

    public static Location deserializeLocation(String serialization) {
        try {
            String[] split = serialization.split(",");

            if (split.length == 0) {
                return null;
            }

            return new Location(Bukkit.getWorld(split[0]), NumberConversions.toInt(split[1]),
                    NumberConversions.toInt(split[2]), NumberConversions.toInt(split[3]));
        } catch (Throwable t) {
            return null;
        }
    }


}
