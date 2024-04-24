package io.github.mystievous.mystigui;

import io.github.mystievous.mystigui.widget.Widget;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.joml.Vector2i;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Gui extends Widget {

    public static final int GUI_WIDTH = 9;

    private Map<Vector2i, Widget> widgets;
    private Set<Vector2i> blockedSlots;
    private final int slots;

    public Gui(int rows) {
        super();
        this.widgets = new HashMap<>();
        this.blockedSlots = new HashSet<>();

        this.slots = rows * GUI_WIDTH;
        this.setSize(new Vector2i(GUI_WIDTH, rows));
    }

    public void putWidget(Vector2i position, Widget widget) {
        Vector2i widgetSize = widget.getSize();
        Set<Vector2i> widgetSlots = new HashSet<>();
        for (int x = position.x(); x < widgetSize.x() + position.x(); x++) {
            for (int y = position.y(); y < widgetSize.y() + position.y(); y++) {
                if (widgets.containsKey(new Vector2i(x, y))) {
                    Bukkit.getLogger().warning("Tried to place widget overlapping already occupied slot.");
                    return;
                }
                widgetSlots.add(new Vector2i(x, y));
            }
        }
        widgets.put(position, widget);
        blockedSlots.addAll(widgetSlots);
    }

    public Map<Vector2i, ItemStack> render() {
        Map<Vector2i, ItemStack> renderedItems = new HashMap<>();
        for (Map.Entry<Vector2i, Widget> widgetEntry : widgets.entrySet()) {
            Vector2i widgetPosition = widgetEntry.getKey();
            Widget widget = widgetEntry.getValue();

            Map<Vector2i, ItemStack> items = widget.render();

            for (Map.Entry<Vector2i, ItemStack> itemEntry : items.entrySet()) {
                Vector2i itemPosition = itemEntry.getKey();
                ItemStack itemValue = itemEntry.getValue();

                Vector2i pos = new Vector2i(widgetPosition).add(itemPosition);

                renderedItems.put(pos, itemValue);
            }

        }
        return renderedItems;
    }

    public Inventory renderInventory() {
        Inventory inventory = Bukkit.createInventory(null, slots, Component.text("Inventory"));
        render().forEach((vector2i, itemStack) -> {
            inventory.setItem(vectorToIndex(vector2i), itemStack);
        });
        return inventory;
    }

}
