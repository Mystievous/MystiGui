package com.starseekstudios.mystigui.widget;

import com.starseekstudios.mystigui.Gui;
import com.starseekstudios.mystigui.MystiGui;
import net.kyori.adventure.text.Component;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class FrameWidget extends Widget {

    protected final Map<Integer, Map<Vector2i, Widget>> widgets = new HashMap<>();
    protected final Map<Integer, Map<Vector2i, Widget>> widgetSlots = new HashMap<>();

    public FrameWidget(Vector2i size) {
        super();
        this.setSize(size);
    }

    public void putWidget(int layer, Vector2i position, Widget widget) {
        Vector2i widgetSize = widget.getSize();
        Map<Vector2i, Widget> addSlots = new HashMap<>();
        var layerWidgetSlots = widgetSlots.getOrDefault(layer, new HashMap<>());
        for (int x = position.x(); x < widgetSize.x() + position.x(); x++) {
            for (int y = position.y(); y < widgetSize.y() + position.y(); y++) {
                if (x < 0 || x >= getSize().x() || y < 0 || y >= getSize().y()) {
                    MystiGui.pluginLogger().warn("Tried to place a widget outside of frame bounds.");
                    return;
                }
                if (layerWidgetSlots.containsKey(new Vector2i(x, y))) {
                    MystiGui.pluginLogger().warn("Tried to place widget overlapping an already occupied slot in the layer.");
                    return;
                }
                addSlots.put(new Vector2i(x, y), widget);
            }
        }
        var layerWidgets = widgets.getOrDefault(layer, new HashMap<>());
        layerWidgets.put(position, widget);
        widgets.put(layer, layerWidgets);

        layerWidgetSlots.putAll(addSlots);
        widgetSlots.put(layer, layerWidgetSlots);
    }

    public void putWidget(Vector2i position, Widget widget) {
        putWidget(0, position, widget);
    }

    public void putLayer(int layer, Map<Vector2i, Widget> widgetMap) {
        widgetMap.forEach((widgetPos, widget) -> putWidget(layer, widgetPos, widget));
    }

    public void putLayer(Map<Vector2i, Widget> widgetMap) {
        putLayer(0, widgetMap);
    }

    public void putAll(Map<Integer, Map<Vector2i, Widget>> layerMap) {
        layerMap.forEach(this::putLayer);
    }

    @Override
    public Map<Vector2i, ItemWidget> render(Gui.GuiHolder guiHolder) {
        return render(guiHolder, widgets);
    }

    protected Map<Vector2i, ItemWidget> render(Gui.GuiHolder guiHolder, Map<Integer, Map<Vector2i, Widget>> widgets) {
        Map<Vector2i, ItemWidget> renderedItems = new HashMap<>();
        var layers = new ArrayList<>(widgets.entrySet());
        layers.sort(Comparator.comparingInt(Map.Entry::getKey));
        layers.forEach(layer -> {
            for (Map.Entry<Vector2i, Widget> widgetEntry : layer.getValue().entrySet()) {
                Vector2i widgetPosition = widgetEntry.getKey();
                Widget widget = widgetEntry.getValue();

                Map<Vector2i, ItemWidget> items = widget.render(guiHolder);

                for (Map.Entry<Vector2i, ItemWidget> itemEntry : items.entrySet()) {
                    Vector2i itemPosition = itemEntry.getKey();
                    ItemWidget itemValue = itemEntry.getValue();

                    Vector2i pos = new Vector2i(widgetPosition).add(itemPosition);

                    renderedItems.put(pos, itemValue);
                }
            }
        });
        return renderedItems;
    }
}
