package io.github.mystievous.mystigui.page;

import io.github.mystievous.mysticore.TextUtil;
import io.github.mystievous.mystigui.element.ButtonElement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.function.Consumer;

/**
 * Gui to confirm an action
 */
public class ConfirmationGUI extends PresetGui {

    /**
     * Gui to confirm an action
     *
     * @param title   Title of the window
     * @param confirm Action to perform when it's confirmed
     * @param cancel  Action to perform when it's canceled
     */
    public ConfirmationGUI(Plugin plugin, Component title, Consumer<Player> confirm, Consumer<Player> cancel) {
        super(plugin, title, 3);

        ItemStack confirmItem = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta confirmMeta = confirmItem.getItemMeta();
        confirmMeta.displayName(TextUtil.noItalic("Confirm").color(NamedTextColor.GREEN));
        confirmItem.setItemMeta(confirmMeta);
        ButtonElement confirmElement = new ButtonElement(confirmItem, confirm);
        placeElement(2, 3, confirmElement);

        ItemStack cancelItem = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta cancelMeta = cancelItem.getItemMeta();
        cancelMeta.displayName(TextUtil.noItalic("Cancel").color(NamedTextColor.RED));
        cancelItem.setItemMeta(cancelMeta);
        ButtonElement cancelElement = new ButtonElement(cancelItem, cancel);
        placeElement(2, 7, cancelElement);

    }
}
