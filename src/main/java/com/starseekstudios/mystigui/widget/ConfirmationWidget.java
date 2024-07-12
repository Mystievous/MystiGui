package com.starseekstudios.mystigui.widget;

import com.starseekstudios.mysticore.ItemUtil;
import com.starseekstudios.mysticore.TextUtil;
import com.starseekstudios.mystigui.Gui;
import com.starseekstudios.mystigui.Icons;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.joml.Vector2i;

import java.util.Optional;
import java.util.function.Consumer;

public class ConfirmationWidget extends FrameWidget {

    private Consumer<InventoryClickEvent> confirmClick;
    private Consumer<InventoryClickEvent> denyClick;

    public ConfirmationWidget(Vector2i size) {
        super(size);

        int buttonWidth = Math.floorDiv(size.x(), 2);
        int buttonHeight = size.y();
        Vector2i buttonSize = new Vector2i(buttonWidth, buttonHeight);

        int denyButtonX = Math.ceilDiv(size.x(), 2);

        fillBackground(-1);

        ItemStack confirmItem = Icons.confirmPart(TextUtil.formatText("Confirm"));
        ButtonWidget confirmButton = new ButtonWidget(buttonSize, confirmItem);
        confirmButton.setClickAction(inventoryClickEvent -> getConfirmClick().ifPresent(consumer -> consumer.accept(inventoryClickEvent)));
        putWidget(new Vector2i(0, 0), confirmButton);

        ItemStack denyItem = Icons.denyPart(TextUtil.errorMessage("Deny"));
        ButtonWidget denyButton = new ButtonWidget(buttonSize, denyItem);
        denyButton.setClickAction(inventoryClickEvent -> getDenyClick().ifPresent(consumer -> consumer.accept(inventoryClickEvent)));
        putWidget(new Vector2i(denyButtonX, 0), denyButton);
    }

    public void setConfirmClick(Consumer<InventoryClickEvent> confirmClick) {
        this.confirmClick = confirmClick;
    }

    public Optional<Consumer<InventoryClickEvent>> getConfirmClick() {
        return Optional.ofNullable(confirmClick);
    }

    public void setDenyClick(Consumer<InventoryClickEvent> denyClick) {
        this.denyClick = denyClick;
    }

    public Optional<Consumer<InventoryClickEvent>> getDenyClick() {
        return Optional.ofNullable(denyClick);
    }

}
