package io.github.mystievous.mystigui;

import io.github.mystievous.mystigui.widget.ItemWidget;
import io.github.mystievous.mystigui.widget.ListWidget;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

public class GuiCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Gui gui = new Gui(Component.text("name"), 5);

        ListWidget widget = new ListWidget(new Vector2i(4, 3));
        ItemWidget stick = new ItemWidget(new ItemStack(Material.STICK));
        stick.setClickAction(event -> {
            HumanEntity entity = event.getWhoClicked();
            entity.sendMessage(Component.text("Woah you clicked a thing!"));
        });
        widget.addWidget(stick);
        widget.addItem(new ItemStack(Material.TNT));
        widget.addItem(new ItemStack(Material.TNT));
        widget.addItem(new ItemStack(Material.AIR));
        widget.addItem(new ItemStack(Material.TNT));
        widget.addItem(new ItemStack(Material.TNT));
        widget.addItem(new ItemStack(Material.TNT));
        widget.addItem(new ItemStack(Material.TNT));
        widget.addItem(new ItemStack(Material.TNT));
        widget.addItem(new ItemStack(Material.TNT));
        widget.addItem(new ItemStack(Material.TNT));
        widget.addItem(new ItemStack(Material.TNT));
        widget.addItem(new ItemStack(Material.TNT));
        gui.putWidget(new Vector2i(0, 2), widget);
        gui.putWidget(new Vector2i(5, 2), widget);

        ItemWidget itemWidget = new ItemWidget(new ItemStack(Material.BROWN_BANNER));
        gui.putWidget(new Vector2i(1, 1), itemWidget);

        if (commandSender instanceof Player player) {
            player.openInventory(gui.renderInventory().getInventory());
        }

        return true;
    }
}
