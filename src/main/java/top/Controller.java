package top;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import top.shapes.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

public class Controller {

    @FXML public Canvas canvas;
    @FXML public ChoiceBox<String> selectAlgo;
    @FXML public Pane pane;
    @FXML public ColorPicker colorPicker;
    @FXML public Button clearAll;
    @FXML public Button saveButton;
    @FXML public Button loadButton;
    @FXML public ToggleButton isPatternFilling;
    @FXML public ChoiceBox<Integer> thicknessChoice;
    @FXML public Button loadPatternBtn;
    @FXML public Label patternLabel;
    @FXML public Button clipBtn;
    @FXML public ToggleButton fillBtn;
    @FXML public ColorPicker fillPicker;

    private static PixelWriter writer;
    private GraphicsContext gc;
    private Map<Circle, Shape> moveMap;
    private Map<Polygon, List<Circle>> polyToPoints;
    private Point firstPoint, secondPoint;
    private boolean creatingPolygon;
    private Polygon polygon;
    private Image fillingImage;
    private Polygon clippingPolygon = null;

    // LAB PART
    private Point pointC;
    private Sector sector;
    //

    // handle shape movement
    private Circle previousPoint;

    public Controller() {
        moveMap = new HashMap<>();
        polyToPoints = new HashMap<>();
        firstPoint = null;
        secondPoint = null;
        polygon = null;
        creatingPolygon = false;
    }

    @FXML
    public void initialize() {
        gc = canvas.getGraphicsContext2D();
        writer = gc.getPixelWriter();

        selectAlgo.getItems().addAll("Line", "Circle", "Rectangle", "Polygon", "Sector");
        selectAlgo.setValue("Line");

        for(int i = 1; i < 14; i+=2) {
            thicknessChoice.getItems().add(i);
        }
        thicknessChoice.setValue(1);

        clearAll.setOnAction(e -> {
            moveMap.forEach((point,shape) -> {
                pane.getChildren().remove(point);
                shape.clear();
            });
            moveMap.clear();
            polyToPoints.clear();
            firstPoint = null;
        });

        clipBtn.setDisable(true);
        clipBtn.setOnAction(this::clipPolygon);

        canvas.setOnMouseClicked(this::canvasClick);
        saveButton.setOnAction(this::saveShape);
        loadButton.setOnAction(this::loadShape);
        loadPatternBtn.setOnAction(this::loadPattern);
    }

    private void loadPattern(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("image", Arrays.asList("*.jpeg", "*.jpg", "*.png"));
        fileChooser.getExtensionFilters().add(filter);
        File file = fileChooser.showOpenDialog(App.getMainStage());

        if(file != null) {
            fillingImage = new Image(file.toURI().toString());
            patternLabel.setText(file.getName());
        }
    }

    private void circleInit() {
        var circle = new MidpointCircle(firstPoint, secondPoint);
        circle.setColor(colorPicker.getValue());
        var c = new Circle(firstPoint.getX(), firstPoint.getY(), 10, new Color(0,0,0,0));
        c.setCursor(Cursor.CLOSED_HAND);
        c.setOnMouseReleased(this::circleOnReleased);
        c.setOnMousePressed(this::circleOnPressed);
        circle.draw();
        pane.getChildren().add(c);
        moveMap.put(c, circle);
    }

    private void lineInit() {
        var line = new Line(firstPoint, secondPoint);
        line.setColor(colorPicker.getValue());
        line.setThickness(thicknessChoice.getValue());
        line.draw();

        var p1 = new Circle(firstPoint.getX(), firstPoint.getY(), 10, new Color(0,0,0,0));
        var p2 = new Circle(secondPoint.getX(), secondPoint.getY(), 10, new Color(0,0,0,0));
        p1.setCursor(Cursor.CLOSED_HAND);
        p2.setCursor(Cursor.CLOSED_HAND);
        p1.setOnMousePressed(this::circleOnPressed);
        p1.setOnMouseReleased(this::lineOnReleased);
        p2.setOnMousePressed(this::circleOnPressed);
        p2.setOnMouseReleased(this::lineOnReleased);
        moveMap.put(p1, line);
        moveMap.put(p2, line);
        pane.getChildren().addAll(p1, p2);
    }

    private void rectangleInit() {
        var rectangle = new Rectangle(firstPoint, secondPoint);
        rectangle.setColor(colorPicker.getValue());
        rectangle.setThickness(thicknessChoice.getValue());
        rectangle.draw();

        List<Circle> points = rectangle.generatePoints();
        points.forEach(point -> {
            point.setOnMousePressed(this::rectangleOnPressed);
            point.setOnMouseReleased(this::polygonOnReleased);
            moveMap.put(point, rectangle);
            pane.getChildren().add(point);
        });
        polyToPoints.put(rectangle, points);
    }

    private void addToPolygon() {
        var line = new Line(firstPoint, secondPoint);
        line.setColor(colorPicker.getValue());
        line.setThickness(thicknessChoice.getValue());
        line.draw();
        polygon.getLines().add(line);

        var p1 = new Circle(firstPoint.getX(), firstPoint.getY(), 10, new Color(0,0,0,0));
        p1.setCursor(Cursor.CLOSED_HAND);
        p1.setOnMousePressed(this::polygonOnPressed);
        p1.setOnMouseReleased(this::polygonOnReleased);
        moveMap.put(p1, polygon);
        pane.getChildren().add(p1);
        polyToPoints.get(polygon).add(p1);
    }

    private void rectangleOnPressed(MouseEvent t) {
        previousPoint = (Circle)t.getSource();
        if(t.getButton().equals(MouseButton.SECONDARY)) {
            var rectangle = (Rectangle) moveMap.get(previousPoint);
            rectangle.clear();
            rectangle.getLines().forEach(e -> {
                var circle = new Circle(e.getX1(), e.getY1(), 10, new Color(0,0,0,0));
                moveMap.remove(circle);
                pane.getChildren().remove(circle);
            });
        }
    }

    private void circleOnPressed(MouseEvent t) {
        previousPoint = (Circle)t.getSource();
        if(t.getButton().equals(MouseButton.SECONDARY)) {
            pane.getChildren().remove(previousPoint);
            moveMap.get(previousPoint).clear();
            moveMap.remove(previousPoint);
        }
    }

    private void circleOnReleased(MouseEvent t) {
        if(t.getButton() != MouseButton.PRIMARY) return;

        if(t.isShiftDown()) {
            var circle = (MidpointCircle) moveMap.get(previousPoint);
            circle.clear();
            double a = Math.pow(previousPoint.getCenterX() - t.getX(), 2);
            double b = Math.pow(previousPoint.getCenterY() - t.getY(), 2);
            circle.setRadius((int)Math.sqrt(a + b));
            circle.draw();
        } else if(t.isControlDown()) {
            var circle = (MidpointCircle) moveMap.get(previousPoint);
            circle.clear();
            circle.setColor(colorPicker.getValue());
            circle.draw();
        } else {
            previousPoint.setCenterX(t.getX());
            previousPoint.setCenterY(t.getY());
            var circle = (MidpointCircle) moveMap.get(previousPoint);
            circle.clear();
            circle.setCenterX((int) t.getX());
            circle.setCenterY((int) t.getY());
            circle.draw();
        }
    }

    private void lineOnReleased(MouseEvent t) {
        if(t.getButton() != MouseButton.PRIMARY) return;

        var line = (Line) moveMap.get(previousPoint);
        line.clear();
        if(t.isShiftDown()) {
            line.setThickness(thicknessChoice.getValue());
        }
        else if(t.isControlDown()) {
            line.setColor(colorPicker.getValue());
        } else {
            if(line.getX1() == (int)previousPoint.getCenterX()
                    && line.getY1() == (int) previousPoint.getCenterY()) {

                line.setX1((int)t.getX());
                line.setY1((int)t.getY());
            } else {
                line.setX2((int)t.getX());
                line.setY2((int)t.getY());
            }
            previousPoint.setCenterY(t.getY());
            previousPoint.setCenterX(t.getX());
        }
        line.draw();
    }

    private void polygonOnPressed(MouseEvent t) {
        previousPoint = (Circle)t.getSource();
        if(t.getButton() == MouseButton.SECONDARY) {
            var polygon = (Polygon) moveMap.get(previousPoint);
            polygon.clear();
            polygon.getLines().forEach(e -> {
                var circle = new Circle(e.getX1(), e.getY1(), 10, new Color(0,0,0,0));
                moveMap.remove(circle);
                pane.getChildren().remove(circle);
            });
            polyToPoints.remove(polygon);
            polygon.getLines().clear();
        } else if(creatingPolygon) {
            secondPoint = new Point((int)previousPoint.getCenterX(), (int)previousPoint.getCenterY());
            addToPolygon();
            firstPoint = null;
            secondPoint = null;
        }
    }

    private void polygonOnReleased(MouseEvent t) {
        if(t.getButton() != MouseButton.PRIMARY) return;

        if(creatingPolygon) {
            creatingPolygon = false;
            return;
        }

        var poly = (Polygon) moveMap.get(previousPoint);

        polyToPoints.keySet().forEach(Polygon::clear);

        if (t.isControlDown() && t.isShiftDown()) {
            if (isPatternFilling.isSelected()) {
                poly.setPattern(fillingImage);
                poly.setHasPatternFilling(true);
            } else {
                poly.setHasPatternFilling(false);
                poly.setFilling(colorPicker.getValue());
            }
        } else if(t.isControlDown() && t.isAltDown()) {
            if(clippingPolygon == null) {
                if (poly.isConvex()) {
                    clippingPolygon = poly;
                    clipBtn.setDisable(false);
                }
            } else {
                polygon = poly;
            }
        } else if(t.isControlDown()) {
            poly.setColor(colorPicker.getValue());
        } else if(t.isShiftDown()) {
            poly.setThickness(thicknessChoice.getValue());
        } else if(t.isAltDown()) {
            int dx = (int) (previousPoint.getCenterX() - t.getX());
            int dy = (int) (previousPoint.getCenterY() - t.getY());

            poly.getLines().forEach(line -> {
                line.setX1(line.getX1() - dx);
                line.setY1(line.getY1() - dy);
                line.setX2(line.getX2() - dx);
                line.setY2(line.getY2() - dy);
            });
            polyToPoints.get(poly).forEach(e -> {
                e.setCenterX(e.getCenterX() - dx);
                e.setCenterY(e.getCenterY() - dy);
            });
        } else {
            if(poly instanceof Rectangle) {
                Rectangle rectangle = (Rectangle) poly;
                rectangle.update(previousPoint, new Circle(t.getX(), t.getY(), 10));

                polyToPoints.get(poly).forEach(p -> {
                    moveMap.remove(p);
                    pane.getChildren().remove(p);
                });
                polyToPoints.get(poly).clear();
                polyToPoints.get(poly).addAll(rectangle.generatePoints());

                polyToPoints.get(poly).forEach(point -> {
                    point.setOnMousePressed(this::rectangleOnPressed);
                    point.setOnMouseReleased(this::polygonOnReleased);
                    moveMap.put(point, rectangle);
                    pane.getChildren().add(point);
                });
            } else {
                poly.update(previousPoint, new Circle(t.getX(), t.getY(), 10));
                previousPoint.setCenterX((int)t.getX());
                previousPoint.setCenterY((int)t.getY());
            }
        }
        polyToPoints.keySet().forEach(Polygon::draw);
        poly.draw();
    }

    private void clipPolygon(ActionEvent e) {
        polygon.clear();
        var list = polyToPoints.get(polygon);
        for (Circle c : list) {
            moveMap.remove(c);
            pane.getChildren().remove(c);
        }
        polyToPoints.get(polygon).clear();
        polygon.clip(clippingPolygon);
        list = polygon.generatePoints();
        list.forEach(p -> {
            p.setOnMousePressed(this::polygonOnPressed);
            p.setOnMouseReleased(this::polygonOnReleased);
            moveMap.put(p, polygon);
            pane.getChildren().add(p);
            polyToPoints.get(polygon).add(p);
        });
        clippingPolygon = null;
        clipBtn.setDisable(true);
        polygon.draw();
    }

    private void canvasClick(MouseEvent e) {
        if(fillBtn.isSelected()) {
            boundaryFill((int)e.getX(), (int)e.getY(), fillPicker.getValue());
        }

        if(firstPoint == null) {
            firstPoint = new Point((int)e.getX(), (int)e.getY());
            if(selectAlgo.getValue().contentEquals("Polygon")) {
                polygon = new Polygon();
                polyToPoints.put(polygon, new ArrayList<>());
                creatingPolygon = true;
            }
            return;
        }

        if(selectAlgo.getValue().contentEquals("Sector") && secondPoint != null) {
            pointC = new Point((int) e.getX(), (int) e.getY());
            sector = new Sector(firstPoint, secondPoint, pointC);
            sector.draw();
            firstPoint = null;
            secondPoint = null;
            return;
        }

        secondPoint = new Point((int)e.getX(), (int)e.getY());

        switch(selectAlgo.getValue()) {
            case "Line":
                lineInit();
                break;
            case "Circle":
                circleInit();
                break;
            case "Rectangle":
                rectangleInit();
                break;
            case "Polygon":
                addToPolygon();
                firstPoint = secondPoint;
                return;
            case "Sector":
                return;
        }

        firstPoint = null;
        secondPoint = null;
    }

    private void saveShape(ActionEvent e) {
        var shape = moveMap.get(previousPoint);
        shape.save();
    }

    private void loadShape(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("json", "*.json");
        fileChooser.getExtensionFilters().add(filter);
        ObjectMapper mapper = new ObjectMapper();
        File file = fileChooser.showOpenDialog(App.getMainStage());

        if(file != null) {
            try {
                var shape = mapper.readValue(file, AbstractShape.class);
                shape.draw();
                var points = shape.generatePoints();

                switch(shape.getClass().getName()) {
                    case "top.shapes.Line":
                        points.forEach(p -> {
                            p.setOnMousePressed(this::circleOnPressed);
                            p.setOnMouseReleased(this::lineOnReleased);
                        });
                        break;
                    case "top.shapes.Polygon":
                        points.forEach(p -> {
                            p.setOnMousePressed(this::polygonOnPressed);
                            p.setOnMouseReleased(this::polygonOnReleased);
                        });
                        polyToPoints.put((Polygon)shape, points);
                        break;
                    case "top.shapes.MidpointCircle":
                        points.forEach(p -> {
                            p.setOnMousePressed(this::circleOnPressed);
                            p.setOnMouseReleased(this::circleOnReleased);
                        });
                        break;
                }
                points.forEach(p -> {
                    moveMap.put(p, shape);
                });
                pane.getChildren().addAll(points);
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    // TODO get a workaround to read pixel values from the canvas
    private void boundaryFill(int x, int y, Color boundary) {

        // Doesn't work
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);

        WritableImage writableImage = new WritableImage((int)canvas.getWidth(), (int)canvas.getHeight());
        canvas.snapshot(params, writableImage);

        var reader = writableImage.getPixelReader();
        //

        Color fillColor = colorPicker.getValue();
        Queue<Pixel> q = new LinkedList<>();
        var c = new Pixel(x, y, reader.getColor(x, y));

        if(c.getColor() == boundary) return;

        q.add(c);

        while(!q.isEmpty()) {
            c = q.remove();

            writer.setColor(c.getX(),c.getY(),fillColor);
            var d = new Color(c.getColor().getRed(), c.getColor().getGreen(), c.getColor().getBlue(), 0);

            if(!d.equals(boundary) && !c.getColor().equals(fillColor)) {

                if(y + 1 < canvas.getHeight())
                    q.add(new Pixel(x, y + 1, reader.getColor(x, y + 1)));
                if(y - 1 >= 0)
                    q.add(new Pixel(x, y - 1, reader.getColor(x, y - 1)));
                if(x + 1 < canvas.getWidth())
                    q.add(new Pixel(x + 1, y, reader.getColor(x + 1, y)));
                if(x - 1 >= 0)
                    q.add(new Pixel(x - 1, y, reader.getColor(x - 1, y)));
            }
        }
    }

    public static PixelWriter getWriter() {
        return writer;
    }
}
