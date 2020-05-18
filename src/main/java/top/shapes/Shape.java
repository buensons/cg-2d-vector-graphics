package top.shapes;

import javafx.scene.shape.Circle;

import java.util.List;

public interface Shape {
    void save();
    void draw();
    void clear();
    List<Circle> generatePoints();
}
