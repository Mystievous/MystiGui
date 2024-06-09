package com.starseekstudios.mystigui;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class GuiListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(final InventoryClickEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof Gui.GuiHolder gui) {
            gui.onClick(event);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClose(final InventoryCloseEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof Gui.GuiHolder guiHolder) {
            guiHolder.onClose(event);
        }
    }
}
