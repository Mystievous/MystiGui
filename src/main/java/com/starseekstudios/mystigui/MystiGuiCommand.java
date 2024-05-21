package com.starseekstudios.mystigui;

import com.starseekstudios.mysticore.TextUtil;
import dev.jorel.commandapi.annotations.*;
import org.bukkit.command.CommandSender;

@Command("mystigui")
@Permission("mystigui.command")
@Help("Manage gui functions.")
public class MystiGuiCommand {

    @Default
    public static void mystigui(CommandSender sender) {
        sender.sendMessage(TextUtil.formatText("--- MystiGui Help ---"));
        sender.sendMessage(TextUtil.formatText("/mystigui -- Show the help menu."));
        sender.sendMessage(TextUtil.formatText("/mystigui reload -- Reload gui icons from the config file."));
    }

    @Subcommand("reload")
    public static void reload(CommandSender sender) {
        Icons.reloadConfig();
        sender.sendMessage(TextUtil.formatText("Reloaded gui icons."));
    }

}
