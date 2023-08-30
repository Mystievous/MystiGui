package io.github.mystievous.mystigui.element;

import io.github.mystievous.mysticore.Palette;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Represents a clickable button element that can be used in a GUI.
 */
public class ButtonElement extends Element implements Clickable {

    /**
     * Creates a ButtonElement with a predefined action to give an item to the player's inventory.
     *
     * @param itemStack The item stack to give.
     * @return The created ButtonElement.
     */
    public static ButtonElement createGiveItemButton(ItemStack itemStack) {
        return new ButtonElement(itemStack, player -> {
            PlayerInventory inventory = player.getInventory();
            Map<Integer, ItemStack> leftovers = inventory.addItem(itemStack);
            if (!leftovers.isEmpty()) {
                player.sendMessage(Component.text("Your inventory is full!", Palette.NEGATIVE_COLOR.toTextColor()));
            }
        });
    }

    // Instance variables
    private Consumer<Player> consumer;


    // Constructors

    /**
     * Creates a clickable button element to be used in a GUI.
     *
     * @param item     The item to represent the element.
     * @param consumer The consumer for the button to run when clicked.
     */
    public ButtonElement(ItemStack item, Consumer<Player> consumer) {
        super(item);
        this.consumer = consumer;
    }

    /**
     * Creates a clickable button element with no action.
     *
     * @param item The item to represent the element.
     */
    public ButtonElement(ItemStack item) {
        this(item, null);
    }

    // Accessors and Mutators

    /**
     * Sets the consumer for the button element.
     *
     * @param consumer The consumer to set.
     */
    public void setConsumer(Consumer<Player> consumer) {
        this.consumer = consumer;
    }


    // Methods

    /**
     * Uses the button element, invoking its associated consumer action.
     *
     * @param player The player who clicked the button.
     */
    public void use(Player player) {
        if (consumer != null) {
            consumer.accept(player);
        }
    }

}
