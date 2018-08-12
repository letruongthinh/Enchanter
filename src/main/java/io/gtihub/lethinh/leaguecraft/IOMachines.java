/*
 * Copyright (c) 2018 to Le Thinh
 */

package io.gtihub.lethinh.leaguecraft;

import io.gtihub.lethinh.leaguecraft.block.BlockMachine;
import io.gtihub.lethinh.leaguecraft.block.GenericMachine;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.NumberConversions;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public final class IOMachines {

    // Utility class, no need to get the constructor
    private IOMachines() {

    }

    public static void saveMachines(LeagueCraft plugin) throws IOException {
        if (BlockMachine.MACHINES.isEmpty()) {
            return;
        }

        File out = new File(plugin.getDataFolder(), "batches.yml");

        if (!out.exists()) {
            if (!out.createNewFile()) {
                throw new IOException("Couldn't create file batches.yml!");
            }
        }

        FileConfiguration machines = new YamlConfiguration();
        BlockMachine.MACHINES.forEach(machine -> machines.set(serializeLocation(machine.block.getLocation()), machine.machineType.getName()));
        machines.save(out);
    }

    public static void loadMachines(LeagueCraft plugin) {
        File in = new File(plugin.getDataFolder(), "batches.yml");

        if (!in.exists()) {
            return;
        }

        FileConfiguration machines = YamlConfiguration.loadConfiguration(in);

        for (String key : machines.getKeys(false)) {
            Location location = deserializeLocation(key);

            if (location == null) {
                continue;
            }

            String machineName = machines.getString(key);

            if (StringUtils.isBlank(machineName)) {
                continue;
            }

            Block block = location.getBlock();

            if (block.isEmpty() || block.isLiquid()) {
                continue;
            }

            for (GenericMachine machineType : GenericMachine.values()) {
                if (machineType.getName().equalsIgnoreCase(machineName)) {
                    BlockMachine.MACHINES.add(machineType.createBlockMachine(block));
                }
            }
        }
    }

    public static void saveMachinesData(LeagueCraft plugin) throws IOException {
        if (BlockMachine.MACHINES.isEmpty()) {
            return;
        }

        File dir = new File(plugin.getDataFolder(), "TickingBatches");

        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException("Couldn't create directory TickingBatches!");
            }
        }

        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (!file.delete()) {
                throw new IOException("Couldn't delete file " + file.getName());
            }
        }

        for (BlockMachine machine : BlockMachine.MACHINES) {
            File out = new File(dir, serializeLocation(machine.block.getLocation()) + ".yml");
            FileConfiguration machineData = new YamlConfiguration();
            Inventory inventory = machine.inventory;

            // Inventory
            machineData.set("invSize", inventory.getSize());
            machineData.set("invTitle", inventory.getTitle());

            ConfigurationSection stacksSection = machineData.createSection("stacks");

            for (int i = 0; i < inventory.getSize(); ++i) {
                ItemStack stack = inventory.getItem(i);

                if (stack == null) {
                    continue;
                }

                stacksSection.set("slot-" + i, stack);
            }

            // Additional data
            machineData.set("tickStopped", machine.isTickStopped());
            machineData.set("accessiblePlayers", machine.accessiblePlayers);

            machineData.save(out);
        }
    }

    public static void loadMachinesData(LeagueCraft plugin) throws IOException {
        if (BlockMachine.MACHINES.isEmpty()) {
            return;
        }

        File dir = new File(plugin.getDataFolder(), "TickingBatches");

        if (!dir.canRead()) {
            return;
        }

        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException("Couldn't create directory TickingBatches");
            }
        }

        for (File file : Objects.requireNonNull(dir.listFiles())) {
            // Doesn't have permission to read file
            if (!file.canRead()) {
                continue;
            }

            Location location = deserializeLocation(getFileNameNoExtension(file));

            if (location == null) {
                continue;
            }

            for (int i = 0; i < BlockMachine.MACHINES.size(); ++i) {
                BlockMachine machine = BlockMachine.MACHINES.get(i);

                if (!machine.block.getLocation().equals(location)) {
                    continue;
                }

                FileConfiguration machineData = YamlConfiguration.loadConfiguration(file);

                // Inventory
                int invSize = machineData.getInt("invSize");
                String invTitle = machineData.getString("invTitle");
                Inventory inventory = Bukkit.createInventory(null, invSize, invTitle);

                ConfigurationSection stacksSection = machineData.getConfigurationSection("stacks");

                try {
                    for (String key : stacksSection.getKeys(false)) {
                        int slot = Integer.parseInt(key.substring("slot-".length()));
                        ItemStack stack = stacksSection.getItemStack(key);

                        if (stack == null) {
                            continue;
                        }

                        inventory.setItem(slot, stack);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                // Additional data
                boolean tickStopped = machineData.getBoolean("tickStopped");
                List<String> accessiblePlayers = machineData.getStringList("accessiblePlayers");

                machine.inventory = inventory;
                machine.setTickStopped(tickStopped);
                machine.accessiblePlayers = accessiblePlayers;
                BlockMachine.MACHINES.set(i, machine);
            }
        }
    }

    private static String getFileNameNoExtension(File file) {
        String name = file.getName();

        if (name.lastIndexOf('.') > 0) {
            name = name.substring(0, name.lastIndexOf('.'));
        }

        return name;
    }

    private static String serializeLocation(Location location) {
        return location.getWorld().getName() + "," + location.getBlockX() + "," + location.getBlockY() + ","
                + location.getBlockZ();
    }

    private static Location deserializeLocation(String serialization) {
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
