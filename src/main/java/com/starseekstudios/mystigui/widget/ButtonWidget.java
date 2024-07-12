package com.starseekstudios.mystigui.widget;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.joml.Vector2i;

import java.util.Optional;
import java.util.function.Consumer;

public class ButtonWidget extends FrameWidget {

    private Consumer<InventoryClickEvent> clickAction;

    public ButtonWidget(Vector2i size, ItemStack itemStack) {
        super(size);

        ItemWidget itemWidget = new ItemWidget(itemStack);
        itemWidget.setClickAction(inventoryClickEvent -> getClickAction().ifPresent(consumer -> consumer.accept(inventoryClickEvent)));
        ListWidget itemList = ListWidget.filled(getSize(), itemWidget);
        putWidget(new Vector2i(0, 0), itemList);
    }

    public void setClickAction(Consumer<InventoryClickEvent> onClick) {
        clickAction = onClick;
    }

    public Optional<Consumer<InventoryClickEvent>> getClickAction() {
        return Optional.ofNullable(clickAction);
    }

}
