package io.github.mystievous.mystigui;

import io.github.mystievous.mysticore.NBTUtils;
import io.github.mystievous.mysticore.interact.UsableItem;
import io.github.mystievous.mystigui.page.Gui;
import io.github.mystievous.mystigui.page.Openable;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

public class GuiHeldItem extends UsableItem {

    private final Openable openable;

    public GuiHeldItem(JavaPlugin plugin, String guiId, ItemStack item, Openable openable) {
        super(plugin, guiId, NBTUtils.noStack(NBTUtils.setNoUse(item)), event -> openForEventPlayer(openable, event));
        this.openable = openable;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private static void openForEventPlayer(Openable openable, PlayerInteractEvent event) {
        Player player = event.getPlayer();
        openable.getGui(player).openInventory(player);
    }

    /**
     * @param player player to pass into gui
     * @return the gui that is opened by this item
     */
    public Gui getGui(Player player) {
        return openable.getGui(player);
    }

    public void openInventory(Player player) {
        openable.getGui(player).openInventory(player);
    }

}
