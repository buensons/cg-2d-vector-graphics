package top.shapes;

import javafx.scene.Cursor;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Pair;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MidpointCircle extends AbstractShape {

    private int radius;
    private int centerX, centerY;

    public MidpointCircle() {}

    public MidpointCircle(Point p1, Point p2) {
        centerX = p1.getX();
        centerY = p1.getY();

        int a = (int)Math.pow(p1.getX() - p2.getX(), 2);
        int b = (int)Math.pow(p1.getY() - p2.getY(), 2);
        radius = (int)Math.sqrt(a + b);
    }

    @Override
    public void draw() {
        int x = 0;
        int y = radius;
        int d = 1 - radius;
        drawAllOctants(x, y);

        while (y > x)
        {
            if ( d < 0 ) //move to E
                d += 2*x + 3;
            else //move to SE
            {
                d += 2*x - 2*y + 5;
                --y;
            }
            ++x;
            drawAllOctants(x, y);
        }
    }

    @Override
    public void clear() {
        int x = 0;
        int y = radius;
        int d = 1 - radius;
        clearAllOctants(x, y);

        while (y > x)
        {
            if ( d < 0 ) //move to E
                d += 2*x + 3;
            else //move to SE
            {
                d += 2*x - 2*y + 5;
                --y;
            }
            ++x;
            clearAllOctants(x, y);
        }
    }

    @Override
    public List<Circle> generatePoints() {
        var c = new Circle(centerX, centerY, 10, new Color(0,0,0,0));
        c.setCursor(Cursor.CLOSED_HAND);
        return Collections.singletonList(c);
    }

    private void drawAllOctants(int x, int y) {
        drawPixel(x + centerX, y + centerY);
        drawPixel(centerX - x, centerY - y);
        drawPixel(x + centerX, centerY - y);
        drawPixel(centerX - x, y + centerY);
        drawPixel(y + centerX, x + centerY);
        drawPixel(centerX - y, centerY - x);
        drawPixel(y + centerX, centerY - x);
        drawPixel(centerX - y, x + centerY);
    }

    private void clearAllOctants(int x, int y) {
        clearPixel(x + centerX, y + centerY);
        clearPixel(centerX - x, centerY - y);
        clearPixel(x + centerX, centerY - y);
        clearPixel(centerX - x, y + centerY);
        clearPixel(y + centerX, x + centerY);
        clearPixel(centerX - y, centerY - x);
        clearPixel(y + centerX, centerY - x);
        clearPixel(centerX - y, x + centerY);
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getCenterX() {
        return centerX;
    }

    public void setCenterX(int centerX) {
        this.centerX = centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public void setCenterY(int centerY) {
        this.centerY = centerY;
    }
}
