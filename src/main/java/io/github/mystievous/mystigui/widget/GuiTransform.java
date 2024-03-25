package io.github.mystievous.mystigui.widget;

import org.joml.Vector2i;

public class GuiTransform {

    private int left;
    private int top;
    private int width;
    private int height;

    public GuiTransform(int left, int top, int width, int height) {
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
    }

    public Vector2i getPosition() {
        return new Vector2i(left, top);
    }

    public void setPosition(Vector2i position) {
        setLeft(position.x);
        setTop(position.y);
    }

    public Vector2i getSize() {
        return new Vector2i(width, height);
    }

    public void setSize(Vector2i size) {
        setWidth(size.x);
        setHeight(size.y);
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
