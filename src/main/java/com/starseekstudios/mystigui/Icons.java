package com.starseekstudios.mystigui;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.starseekstudios.mysticore.Color;
import com.starseekstudios.mysticore.ItemUtil;
import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import de.exlll.configlib.YamlConfigurations;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.nio.file.Path;
import java.nio.file.Paths;
public class Icons {

    public static ItemStack blankSlot() {
        IconEntry entry = config.blankSlot;
        ItemStack itemStack = ItemUtil.createItem(null, entry.material(), entry.customModelData());
        itemStack.editMeta(itemMeta -> {
            itemMeta.setHideTooltip(true);
            itemMeta.setMaxStackSize(1);
        });
        return itemStack;
    }

    public static ItemStack playerHead(OfflinePlayer player) {
        IconEntry entry = config.playerHead;
        ItemStack skull = ItemUtil.createItem(null, Material.PLAYER_HEAD, entry.customModelData());
        skull.editMeta(SkullMeta.class, skullMeta -> {
            PlayerProfile profile = player.getPlayerProfile();
            skullMeta.setPlayerProfile(profile);
        });
        return skull;
    }

    public static ItemStack leftArrow(Component name, Color color) {
        return arrow(config.leftArrow, name, color);
    }

    public static ItemStack rightArrow(Component name, Color color) {
        return arrow(config.rightArrow, name, color);
    }

    private static ItemStack arrow(IconEntry entry, Component name, Color color) {
        ItemStack itemStack = ItemUtil.createItem(name, entry.material(), entry.customModelData());
        itemStack.editMeta(itemMeta -> {
            if (itemMeta instanceof LeatherArmorMeta leatherArmorMeta) {
                leatherArmorMeta.setColor(color.toBukkitColor());
            }
            itemMeta.setMaxStackSize(1);
            ItemUtil.hideExtraTooltip(itemMeta);
        });
        return itemStack;
    }

    private static final Path CONFIG_PATH = Paths.get(MystiGui.getInstance().getDataFolder().getPath(), "icons.yml");
    private static IconConfig config;

    public static void reloadConfig() {
        config = YamlConfigurations.update(CONFIG_PATH, IconConfig.class);
    }

    public record IconEntry(Material material, int customModelData) {
    }

    @Configuration
    private static class IconConfig {

        IconEntry blankSlot = new IconEntry(Material.WHITE_STAINED_GLASS_PANE, 0);

        @Comment("Player Head `material` is discarded as it will always be `PLAYER_HEAD`")
        IconEntry playerHead = new IconEntry(Material.PLAYER_HEAD, 0);

        @Comment("Arrows will only have color if they are a type of leather armor.")
        IconEntry leftArrow = new IconEntry(Material.REDSTONE_BLOCK, 0);
        IconEntry rightArrow = new IconEntry(Material.EMERALD_BLOCK, 0);

    }

}
