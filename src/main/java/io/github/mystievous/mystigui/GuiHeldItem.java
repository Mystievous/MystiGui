package io.github.mystievous.mystigui;

import io.github.mystievous.mysticore.NBTUtils;
import io.github.mystievous.mystigui.page.Gui;
import io.github.mystievous.mystigui.page.Openable;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

public class GuiHeldItem implements Listener {

    public static final String GUI_ID = "gui_id";

    private final JavaPlugin plugin;
    private final String guiId;
    private final ItemStack item;
    private final Openable openable;
    private String permission;

    public GuiHeldItem(JavaPlugin plugin, String guiId, ItemStack item, Openable openable) {
        this.plugin = plugin;
        this.guiId = guiId;
        this.item = item;
        NBTUtils.setString(plugin, GUI_ID, this.item, guiId);
        NBTUtils.noStack(plugin, this.item);
        NBTUtils.setNoUse(plugin, this.item);
        this.openable = openable;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Sets the permission required to use this item
     *
     * @param permission The permission string
     */
    public void setPermission(@Nullable String permission) {
        this.permission = permission;
    }

    /**
     * @param player player to pass into gui
     * @return the gui that is opened by this item
     */
    public Gui getGui(Player player) {
        return openable.getGui(player);
    }

    public ItemStack getItem() {
        return item;
    }

    /**
     * Checks if an items matches the criteria to open the gui
     *
     * @param item item to check
     * @return true, if the item has the proper tag for the gui
     */
    public boolean matchItem(ItemStack item) {
        return guiId.equals(NBTUtils.getString(plugin, GUI_ID, item));
    }

    public void openInventory(Player player) {
        openable.getGui(player).openInventory(player);
    }

    /**
     * Disallows players to craft using the item
     */
    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if (event.isCancelled())
            return;
        CraftingInventory inventory = event.getInventory();
        for (ItemStack item : inventory.getMatrix()) {
            if (matchItem(item)) {
                event.getWhoClicked().sendMessage(Component.text("Nice try ;)"));
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Player player = event.getPlayer();
            ItemStack item = event.getItem();
            if (event.getAction() == Action.PHYSICAL
                    || event.getAction() == Action.LEFT_CLICK_AIR
                    || event.getAction() == Action.LEFT_CLICK_BLOCK
                    || item == null)
                return;

            if (matchItem(item) && (permission == null || player.hasPermission(permission))) {
                Bukkit.getScheduler().runTask(plugin, () -> openable.getGui(player).openInventory(player));
            }
        });
    }

}
