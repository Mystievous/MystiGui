package io.github.mystievous.mystigui.page;

import io.github.mystievous.mystigui.element.Element;
import io.github.mystievous.mystigui.element.PlayerElement;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class PlayerGui extends ListGui {

    public PlayerGui(Plugin plugin, Component name, Function<OfflinePlayer, List<Component>> loreBuilder, List<OfflinePlayer> playerList, BiConsumer<Player, OfflinePlayer> onClick, Element lastElement) {
        super(plugin, name, lastElement);
        for (OfflinePlayer player : playerList) {
            if (player != null) {
                PlayerElement element = new PlayerElement(player, loreBuilder, onClick);
                addElement(element);
            }
        }
    }

}
