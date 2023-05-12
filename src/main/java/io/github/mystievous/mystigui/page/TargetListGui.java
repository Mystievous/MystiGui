package io.github.mystievous.mystigui.page;

import io.github.mystievous.mystigui.element.Element;
import io.github.mystievous.mystigui.element.TargetElement;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class TargetListGui<T> extends ListGui {

    public TargetListGui(Plugin plugin, Component name, Function<T, ItemStack> representation, List<T> targetList, BiConsumer<Player, T> onClick, Element lastElement) {
        super(plugin, name, lastElement);
        for (T target : targetList) {
            if (target != null) {
                TargetElement<T> element = new TargetElement<>(target, representation, onClick);
                addElement(element);
            }
        }
    }
}
