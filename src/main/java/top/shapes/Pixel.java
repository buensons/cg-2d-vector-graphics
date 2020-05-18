package top.shapes;

import javafx.scene.paint.Color;

public class Pixel {
    private Color color;
    private int x, y;

    public Pixel(int x, int y, Color c) {
        this.x = x;
        this.y = y;
        color = c;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
