package io.github.mystievous.mystigui;

import io.github.mystievous.mystigui.widget.ListWidget;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

public class GuiCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Gui gui = new Gui();

        ListWidget widget = new ListWidget(new Vector2i(4, 2));
        widget.addItem(new ItemStack(Material.STICK));
        widget.addItem(new ItemStack(Material.TNT));
        widget.addItem(new ItemStack(Material.TNT));
        widget.addItem(new ItemStack(Material.TNT));
        widget.addItem(new ItemStack(Material.TNT));
        gui.putWidget(new Vector2i(2, 3), widget);

        if (commandSender instanceof Player player) {
            player.openInventory(gui.render());
        }

        return true;
    }
}
