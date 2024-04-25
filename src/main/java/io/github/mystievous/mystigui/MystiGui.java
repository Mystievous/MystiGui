package io.github.mystievous.mystigui;

import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class MystiGui extends JavaPlugin {

    private static MystiGui me;

    public static MystiGui getInstance() {
        if (me == null) {
            throw new NullPointerException("Tried to get MystiGui instance before it was loaded!");
        }

        return me;
    }

    public static ComponentLogger pluginLogger() {
        return getInstance().getComponentLogger();
    }

    @Override
    public void onEnable() {
        // Plugin startup logic

        me = this;

        GuiCommand command = new GuiCommand();
        PluginCommand pluginCommand = Bukkit.getPluginCommand("gui");
        if (pluginCommand != null) {
            pluginCommand.setExecutor(command);
        }

        GuiListener guiListener = new GuiListener();
        Bukkit.getPluginManager().registerEvents(guiListener, this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
