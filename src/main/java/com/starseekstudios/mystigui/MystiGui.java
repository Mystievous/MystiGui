package com.starseekstudios.mystigui;

import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
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

        GuiListener guiListener = new GuiListener();
        Bukkit.getPluginManager().registerEvents(guiListener, this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}