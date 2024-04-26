package io.github.mystievous.mystigui;

import io.github.mystievous.mystigui.widget.WidgetChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GuiListener implements Listener {

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof Gui.GuiHolder gui) {
            gui.onClick(event);
        }
    }

    @EventHandler
    public void onWidgetChange(final WidgetChangeEvent event) {
        
    }

}
