package gui;

import java.io.Serializable;

public class WindowConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String title;
    private final int x, y, width, height;
    private final int state;

    public WindowConfig(String title, int x, int y, int width, int height, int state) {
        this.title = title;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.state = state;
    }

    public String getTitle() { return title; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getState() { return state; }
}