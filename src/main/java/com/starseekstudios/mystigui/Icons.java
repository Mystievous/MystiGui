package com.starseekstudios.mystigui;

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
        ItemStack item = makeIcon(null, config.blankSlot);
        item.editMeta(itemMeta -> {
            itemMeta.setHideTooltip(true);
            itemMeta.setMaxStackSize(1);
        });
        return makeIcon(item);
    }


    public static ItemStack playerHead(OfflinePlayer player) {
        ItemStack skull = makeIcon(null, config.playerHead);
        skull.editMeta(SkullMeta.class, skullMeta -> {
            skullMeta.setPlayerProfile(player.getPlayerProfile());
        });
        return skull;
    }


    public static ItemStack leftArrow(Component name, Color color) {
        return colorIcon(makeIcon(name, config.leftArrow), color);
    }

    public static ItemStack rightArrow(Component name, Color color) {
        return colorIcon(makeIcon(name, config.rightArrow), color);
    }

    public static ItemStack exitButton(Component name) {
        return makeIcon(name, config.exitButton);
    }

    private static ItemStack makeIcon(Component name, IconEntry entry) {
        ItemStack item = ItemUtil.createItem(name, entry.material(), entry.customModelData());
        item.editMeta(ItemUtil::hideExtraTooltip);
        return item;
    }

    private static ItemStack makeIcon(ItemStack itemStack) {
        ItemStack item = itemStack.clone();
        item.editMeta(ItemUtil::hideExtraTooltip);
        return item;
    }

    private static ItemStack colorIcon(ItemStack item, Color color) {
        item.editMeta(LeatherArmorMeta.class, leatherArmorMeta -> leatherArmorMeta.setColor(color.toBukkitColor()));
        return item;
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
        IconEntry exitButton = new IconEntry(Material.REDSTONE_BLOCK, 0);

        @Comment("Player Head `material` is discarded as it will always be `PLAYER_HEAD`")
        IconEntry playerHead = new IconEntry(Material.PLAYER_HEAD, 0);

        @Comment("Arrows will only have color if they are a type of leather armor.")
        IconEntry leftArrow = new IconEntry(Material.REDSTONE_BLOCK, 0);
        IconEntry rightArrow = new IconEntry(Material.EMERALD_BLOCK, 0);

    }

}
