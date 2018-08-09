package io.gtihub.lethinh.leaguecraft.block;

import io.gtihub.lethinh.leaguecraft.LeagueCraft;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

/**
 * Backend of my Mantle plugin, too lazy to recode shits
 *
 * Created by Le Thinh
 */
public class BlockMachine {

    /* Static Members */
    public static final CopyOnWriteArrayList<BlockMachine> MACHINES = new CopyOnWriteArrayList<>();
    protected static final long DEFAULT_DELAY = 0x14, DEFAULT_PERIOD = 0xa;

    /* Instance Members */
    public final GenericMachine machineType;
    public final Block block;
    public Inventory inventory;
    public final BukkitRunnable runnable;
    public List<String> accessiblePlayers;

    /* Default constructor */
    public BlockMachine(GenericMachine machineType, Block block, int invSlots, String invName, String... players) {
        this(machineType, block, Bukkit.createInventory(null, invSlots, invName), players);
    }

    public BlockMachine(GenericMachine machineType, Block block, Inventory inventory, String... players) {
        this.machineType = machineType;
        this.block = block;
        this.accessiblePlayers = new ArrayList<>();
        this.accessiblePlayers.addAll(Arrays.asList(players));

        this.inventory = inventory;
        this.runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (!canWork()) {
                    return;
                }

                work();
            }
        };
    }

    /* Tick */
    private boolean tickStopped = false;

    public void setTickStopped(boolean tickStopped) {
        this.tickStopped = tickStopped;

        if (runnable != null) {
            if (tickStopped) {
                runnable.cancel();
            } else {
                handleUpdate(LeagueCraft.instance);
            }
        }
    }

    public boolean isTickStopped() {
        return tickStopped;
    }

    public void dropItems(ItemStack stack) {
        World world = block.getWorld();
        Location location = block.getLocation();
        world.dropItemNaturally(location, stack);

        if (getRealSlots() > 0) {
            IntStream.range(0, getRealSlots()).mapToObj(i -> inventory.getItem(i)).filter(invDrop -> invDrop != null && invDrop.getAmount() != 0).forEach(invDrop -> world.dropItemNaturally(location, invDrop));
        }
    }

    public int getRealSlots() {
        return 0;
    }

    /**
     * Run {@code runnable} synchronously or asynchronously
     *
     * @param plugin Instance of Mantle plugin
     */
    public void handleUpdate(LeagueCraft plugin) {

    }

    public void work() {

    }

    public boolean canWork() {
        return !isTickStopped();
    }

    public boolean canOpen(Player player) {
        if (accessiblePlayers.isEmpty()) {
            return accessiblePlayers.add(player.getName());
        }

        return accessiblePlayers.stream().anyMatch(p -> p.equals(player.getName()));
    }

    public boolean canBreak(Player player) {
        if (accessiblePlayers.isEmpty()) {
            return accessiblePlayers.add(player.getName());
        }

        return accessiblePlayers.stream().anyMatch(p -> p.equals(player.getName()));
    }

    /* Callbacks */
    public void onMachinePlaced(Player player, ItemStack heldItem) {
        setTickStopped(false);
    }

    public void onMachineBroken(Player player) {
    }

    public boolean onInventoryInteract(ClickType clickType, InventoryAction action, SlotType slotType, ItemStack clicked, ItemStack cursor, int slot, InventoryView view, HumanEntity player) {
        return false;
    }

    /* Object */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof BlockMachine && block.getLocation().equals(((BlockMachine) obj).block.getLocation());
    }

    @Override
    public int hashCode() {
        return block.getLocation().hashCode();
    }

}
