package io.github.mystievous.mystigui.element;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class TargetElement<T> extends Element implements Clickable {

    private final T target;
    private final BiConsumer<Player, T> onClick;

    public TargetElement(T target, Function<T, ItemStack> representation, BiConsumer<Player, T> onClick) {
        super(representation.apply(target));
        this.target = target;
        this.onClick = onClick;
    }

    @Override
    public void use(Player player) {
        onClick.accept(player, target);
    }
}
