package top.shapes;

import javafx.scene.shape.Circle;
import javafx.util.Pair;
import top.Controller;

import java.util.List;

public class Sector extends AbstractShape {

    private Point a, b, c;
    private Line ab, ac;
    private int radius;

    public Sector(Point a, Point b, Point c) {
        this.a = a;
        this.b = b;
        this.c = c;
        double x = Math.pow(a.getX() - b.getX(), 2);
        double y = Math.pow(a.getY() - b.getY(), 2);
        radius = (int)Math.sqrt(x + y);
    }

    @Override
    public void draw() {

        int determinant = det(a,b,c);

        int x = 0;
        int y = radius;
        int d = 1 - radius;
        drawAllOctants(x, y, determinant);

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
            drawAllOctants(x, y, determinant);
        }

        int ac_len = (int)Math.sqrt(Math.pow(a.getX() - c.getX(),2) + Math.pow(a.getY() - c.getY(),2));
        int dx = c.getX() - a.getX();
        int dy = c.getY() - a.getY();
        dx = (int)(((double)dx/(double)ac_len) * radius + a.getX());
        dy = (int)(((double)dy/(double)ac_len) * radius + a.getY());

        ab = new Line(a,b);
        ac = new Line(a, new Point(dx,dy));
        ab.draw();
        ac.draw();
    }

    private void drawAllOctants(int x, int y, int determinant) {

        int centerX = a.getX();
        int centerY = a.getY();

        if(determinant > 0) {
            drawPixelPositive(x + centerX, y + centerY);
            drawPixelPositive(centerX - x, centerY - y);
            drawPixelPositive(x + centerX, centerY - y);
            drawPixelPositive(centerX - x, y + centerY);
            drawPixelPositive(y + centerX, x + centerY);
            drawPixelPositive(centerX - y, centerY - x);
            drawPixelPositive(y + centerX, centerY - x);
            drawPixelPositive(centerX - y, x + centerY);
        } else {
            drawPixelNegative(x + centerX, y + centerY);
            drawPixelNegative(centerX - x, centerY - y);
            drawPixelNegative(x + centerX, centerY - y);
            drawPixelNegative(centerX - x, y + centerY);
            drawPixelNegative(y + centerX, x + centerY);
            drawPixelNegative(centerX - y, centerY - x);
            drawPixelNegative(y + centerX, centerY - x);
            drawPixelNegative(centerX - y, x + centerY);
        }
    }

    private void drawPixelPositive(int x, int y) {
        if(det(a,b,new Point(x,y)) > 0 && det(a,c, new Point(x,y)) < 0) {
            Controller.getWriter().setColor(x, y, color);
        }
    }

    private void drawPixelNegative(int x, int y) {
        if(det(a,b,new Point(x,y)) > 0 || det(a,c, new Point(x,y)) < 0) {
            Controller.getWriter().setColor(x, y, color);
        }
    }

    private int det(Point a, Point b, Point c) {
        return a.getX() * b.getY() - a.getX() * c.getY() - a.getY() * b.getX()
                + a.getY() * c.getX() + b.getX() * c.getY() - b.getY() * c.getX();
    }

    @Override
    public void clear() {
        // TODO
    }

    @Override
    public List<Circle> generatePoints() {
        // TODO
        return null;
    }
}
