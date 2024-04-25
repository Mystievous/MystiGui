package io.github.mystievous.mystigui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

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
        Bukkit.getPluginCommand("gui").setExecutor(command);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
