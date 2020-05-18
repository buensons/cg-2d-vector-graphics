package top.shapes;

import javafx.scene.shape.Circle;

public class Rectangle extends Polygon {

    public Rectangle(Point p1, Point p2) {
        super();
        lines.add(new Line(p1, new Point(p2.getX(), p1.getY())));
        lines.add(new Line(new Point(p2.getX(), p1.getY()), p2));
        lines.add(new Line(p2, new Point(p1.getX(), p2.getY())));
        lines.add(new Line(new Point(p1.getX(), p2.getY()), p1));
    }

    @Override
    public void update(Circle source, Circle destination) {

        int dx = (int)(destination.getCenterX() - source.getCenterX());
        int dy = (int)(destination.getCenterY() - source.getCenterY());

        lines.forEach(line -> {

            if(line.getX1() == source.getCenterX()) {
                line.setX1(line.getX1() + dx);
            }
            if(line.getY1() == source.getCenterY()) {
                line.setY1(line.getY1() + dy);
            }
            if(line.getX2() == source.getCenterX()) {
                line.setX2(line.getX2() + dx);
            }
            if(line.getY2() == source.getCenterY()) {
                line.setY2(line.getY2() + dy);
            }
        });
    }
}
