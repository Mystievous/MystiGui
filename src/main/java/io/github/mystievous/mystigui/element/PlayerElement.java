package io.github.mystievous.mystigui.element;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Element representing an OfflinePlayer
 */
public class PlayerElement extends TargetElement<OfflinePlayer> {

    public static ItemStack getPlayerSkull(OfflinePlayer player, Function<OfflinePlayer, List<Component>> loreBuilder) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        if (!(skull.getItemMeta() instanceof SkullMeta skullMeta)) {
            throw new IllegalArgumentException("Template item must be a skull!");
        }
        skullMeta.setOwningPlayer(player);
        String playerName = player.getName();
        String itemName = playerName != null ? player.getName() : player.getUniqueId().toString();
        skullMeta.displayName(Component.text(itemName).decoration(TextDecoration.ITALIC, false));
        skullMeta.lore(loreBuilder.apply(player));
        skullMeta.setCustomModelData(1);
        skull.setItemMeta(skullMeta);
        return skull;
    }

    public PlayerElement(OfflinePlayer target, Function<OfflinePlayer, List<Component>> loreBuilder, BiConsumer<Player, OfflinePlayer> onClick) {
        super(target, (player) -> getPlayerSkull(player, loreBuilder), onClick);
    }

}
