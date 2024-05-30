package com.starseekstudios.mystigui.widget;

import com.starseekstudios.mystigui.Gui;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class FrameMultiplexerWidget extends Widget {

    private HashMap<Key, FrameWidget> frames = new HashMap<>();

    private Key selectedKey;

    public FrameMultiplexerWidget(Vector2i size) {
        super();
        setSize(size);
        setOnReload(widget -> ((FrameMultiplexerWidget) widget).frames.forEach((key, frameWidget) -> {
            frameWidget.onReload();
        }));
    }

    @Override
    public void setGuiHolder(Gui.@Nullable GuiHolder guiHolder) {
        super.setGuiHolder(guiHolder);
        frames.values().forEach(frameWidget -> frameWidget.setGuiHolder(guiHolder));
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

    public boolean changeFrame(Key key) {
        if (!key.equals(selectedKey)) {
            if (!frames.containsKey(key)) {
                getOrCreateFrame(key);
            }
            selectedKey = key;
            return true;
        }
        return false;
    }

    @Override
    public Map<Vector2i, ItemWidget> render() {
        Optional<Key> key = getSelectedKey();
        if (key.isPresent()) {
            Optional<FrameWidget> frameWidget = getFrame(key.get());
            if (frameWidget.isPresent()) {
                return frameWidget.get().render();
            }
        }

        return new HashMap<>();
    }

    @Override
    public FrameMultiplexerWidget clone() {
        FrameMultiplexerWidget widget = (FrameMultiplexerWidget) super.clone();

        HashMap<Key, FrameWidget> frames = new HashMap<>();
        this.frames.forEach((key, value) -> frames.put(key, value.clone()));
        widget.frames = frames;

        widget.selectedKey = this.selectedKey;

        return widget;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FrameMultiplexerWidget that = (FrameMultiplexerWidget) o;
        return Objects.equals(frames, that.frames) && Objects.equals(selectedKey, that.selectedKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), frames, selectedKey);
    }
}
