package top.shapes;

import javafx.scene.Cursor;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Pair;

import java.util.Arrays;
import java.util.List;

public class Line extends AbstractShape {

    private int x1, x2, y1, y2;
    private int thickness;

    public Line() {}

    public Line(Point p1, Point p2) {
        x1 = p1.getX();
        x2 = p2.getX();
        y1 = p1.getY();
        y2 = p2.getY();
        thickness = 1;
    }

    @Override
    public void draw() {
        drawPixels(color);
    }

    @Override
    public void clear() {
        drawPixels(Color.BLACK);
    }

    @Override
    public List<Circle> generatePoints() {
        var c1 = new Circle(x1,y1,10, new Color(0,0,0,0));
        var c2 = new Circle(x2,y2, 10, new Color(0,0,0,0));
        c1.setCursor(Cursor.CLOSED_HAND);
        c2.setCursor(Cursor.CLOSED_HAND);
        return Arrays.asList(c1, c2);
    }

    private void drawPixels(Color c) {
        int xRev = 1, yRev = 1;
        int xf = x1;
        int xb = x2;
        int yf = y1;
        int yb = y2;

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);

        if(x1 > x2) {
            xRev = -1;
        }

        if(y1 > y2) {
            yRev = -1;
        }

        if(dy > dx) {
            drawSteepLine(dx, dy, xRev, yRev, xf, yf, xb, yb, c);
        } else {
            drawLeveledLine(dx, dy, xRev, yRev, xf, yf, xb, yb, c);
        }
    }

    private void drawSteepLine(int dx, int dy, int xRev, int yRev, int xf, int yf, int xb, int yb, Color c) {
        int d, dE, dNE;
        d = 2*dx - dy;
        dE = 2*dx;
        dNE = 2*(dx - dy);

        while(yRev * yf - 1 < yb * yRev) {
            drawPixel(xf, yf, c);
            drawPixel(xb, yb, c);

            for(int i = 1; i <= (thickness-1)/2; i++) {
                drawPixel(xf + i, yf, c);
                drawPixel(xf - i, yf, c);
                drawPixel(xb + i, yb, c);
                drawPixel(xb - i, yb, c);
            }
            yf += yRev;
            yb -= yRev;

            if(d < 0) {
                d += dE;
            } else {
                d += dNE;
                xf += xRev;
                xb -= xRev;
            }
        }
    }

    private void drawLeveledLine(int dx, int dy, int xRev, int yRev, int xf, int yf, int xb, int yb, Color c) {
        int d, dE, dNE;
        d = 2*dy - dx;
        dE = 2*dy;
        dNE = 2*(dy - dx);

        while(xRev * xf - 1 < xb * xRev) {
            drawPixel(xf, yf, c);
            drawPixel(xb, yb, c);

            for(int i = 1; i <= (thickness-1)/2; i++) {
                drawPixel(xf, yf + i, c);
                drawPixel(xf, yf - i, c);
                drawPixel(xb, yb + i, c);
                drawPixel(xb, yb - i, c);
            }

            xf += xRev;
            xb -= xRev;

            if(d < 0) {
                d += dE;
            } else {
                d += dNE;
                yf += yRev;
                yb -= yRev;
            }
        }
    }

    public int getX1() {
        return x1;
    }

    public void setX1(int x1) {
        this.x1 = x1;
    }

    public int getX2() {
        return x2;
    }

    public void setX2(int x2) {
        this.x2 = x2;
    }

    public int getY1() {
        return y1;
    }

    public void setY1(int y1) {
        this.y1 = y1;
    }

    public int getY2() {
        return y2;
    }

    public void setY2(int y2) {
        this.y2 = y2;
    }

    public int getThickness() {
        return thickness;
    }

    public void setThickness(int thickness) {
        this.thickness = thickness;
    }
}
