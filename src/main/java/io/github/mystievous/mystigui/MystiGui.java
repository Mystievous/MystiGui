package io.github.mystievous.mystigui;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class MystiGui extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        GuiCommand command = new GuiCommand();
        Bukkit.getPluginCommand("gui").setExecutor(command);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
