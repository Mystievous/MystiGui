package io.github.mystievous.mystigui.element;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class ButtonElement extends Element implements Clickable {


    // STATIC METHODS AND FIELDS

    // INSTANCE VARIABLES
    private Consumer<Player> consumer;


    // CONSTRUCTORS

    /**
     * Clickable Button element to go in a GUI
     *
     * @param item     The item to represent the element
     * @param consumer The consumer for the button to run
     */
    public ButtonElement(ItemStack item, Consumer<Player> consumer) {
        super(item);
        this.consumer = consumer;
    }

    public ButtonElement(ItemStack item) {
        this(item, null);
    }

    // ACCESSORS AND MUTATORS

    public void setConsumer(Consumer<Player> consumer) {
        this.consumer = consumer;
    }


    // METHODS

    public void use(Player player) {
        if (consumer != null) {
            consumer.accept(player);
        }
    }

}
