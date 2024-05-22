package com.starseekstudios.mystigui.widget;

import com.starseekstudios.mystigui.Gui;
import net.kyori.adventure.key.Key;
import org.joml.Vector2i;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FrameMultiplexerWidget extends Widget {

    private final HashMap<Key, FrameWidget> frames = new HashMap<>();

    private Key selectedKey;

    public FrameMultiplexerWidget(Vector2i size) {
        super();
        setSize(size);
    }

    public Optional<Key> getSelectedKey() {
        return Optional.ofNullable(selectedKey);
    }

    public FrameWidget getOrCreateFrame(Key key) {
        FrameWidget frameWidget = frames.get(key);
        if (frameWidget == null) {
            frameWidget = new FrameWidget(getSize());
            frames.put(key, frameWidget);
        }
        if (getSelectedKey().isEmpty()) {
            selectedKey = key;
        }
        return frameWidget;
    }

    private Optional<FrameWidget> getFrame(Key key) {
        return Optional.ofNullable(frames.get(key));
    }

    public void changeFrame(Key key) {
        if (!key.equals(selectedKey)) {
            if (!frames.containsKey(key)) {
                getOrCreateFrame(key);
            }
            selectedKey = key;
            onChange();
        }
    }

    @Override
    public Map<Vector2i, ItemWidget> render(Gui.GuiHolder guiHolder) {
        Optional<Key> key = getSelectedKey();
        if (key.isPresent()) {
            Optional<FrameWidget> frameWidget = getFrame(key.get());
            if (frameWidget.isPresent()) {
                return frameWidget.get().render(guiHolder);
            }
        }

        return new HashMap<>();
    }
}
