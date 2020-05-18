package top.shapes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import top.EdgeEntry;
import top.EdgeTable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Polygon extends AbstractShape {

    protected List<Line> lines;
    protected int thickness;
    private int minY;
    @JsonIgnore
    private Color filling;
    @JsonIgnore
    private Image pattern;
    private boolean hasPatternFilling;

    // for serialization purposes
    private String fillingString;
    private String imageName;

    public Polygon() {
        lines = new ArrayList<>();
        thickness = 1;
        minY = Integer.MAX_VALUE;
        filling = new Color(0,0,0,0);
        fillingString = filling.toString();
        hasPatternFilling = false;
    }

    public void update(Circle source, Circle destination) {
        var p1 = lines
                .stream()
                .filter(e -> e.getX1() == source.getCenterX() && e.getY1() == source.getCenterY())
                .findFirst()
                .get();

        var p2 = lines
                .stream()
                .filter(e -> e.getX2() == source.getCenterX() && e.getY2() == source.getCenterY())
                .findFirst()
                .get();

        p1.setX1((int) destination.getCenterX());
        p1.setY1((int) destination.getCenterY());
        p2.setX2((int) destination.getCenterX());
        p2.setY2((int) destination.getCenterY());
    }

    @Override
    public void draw() {
        if(!filling.equals(new Color(0,0,0,0)) || hasPatternFilling) {
            fill();
        }
        lines.forEach(Line::draw);
    }

    @Override
    public void clear() {
        lines.forEach(Line::clear);
        if(!filling.equals(new Color(0,0,0,0)) || hasPatternFilling) {
            var color = filling;
            var wasTrue = hasPatternFilling;
            hasPatternFilling = false;
            filling = new Color(0, 0, 0, 0);
            fill();
            filling = color;
            hasPatternFilling = wasTrue;
        }
    }

    @Override
    public List<Circle> generatePoints() {
        var list = new ArrayList<Circle>();
        lines.forEach(e -> {
            var circle = new Circle(e.getX1(), e.getY1(), 10, new Color(0,0,0,0));
            circle.setCursor(Cursor.CLOSED_HAND);
            list.add(circle);
        });
        return list;
    }

    private EdgeTable generateEdgeTable() {
        EdgeTable table = new EdgeTable();

        lines.forEach(line -> {
            if(line.getY1() == line.getY2()) return;

            int yMax = Math.max(line.getY1(), line.getY2());
            int yMin = Math.min(line.getY1(), line.getY2());
            int xMin = line.getX1();
            float slope =  (float)(line.getX2() - line.getX1()) / (float) (line.getY2() - line.getY1());

            if(line.getY1() > line.getY2()) {
                xMin = line.getX2();
            }

            table.add(yMin, new EdgeEntry(yMax, xMin, slope));

            if(yMin < minY) minY = yMin;
        });

        return table;
    }

    public void fill() {
        List<EdgeEntry> activeEdgeTable = new LinkedList<>();
        var edgeTable = generateEdgeTable();
        int y = minY;

        while(!activeEdgeTable.isEmpty() || !edgeTable.isEmpty()) {
            int parity = 0;

            if(edgeTable.containsKey(y)) {
                var entry = edgeTable.remove(y);
                activeEdgeTable.addAll(entry);
            }

            activeEdgeTable.sort(new Comparator<EdgeEntry>() {
                @Override
                public int compare(EdgeEntry o1, EdgeEntry o2) {
                    return (int)(o1.getxMin() - o2.getxMin());
                }
            });

            for(int i = 0; i < activeEdgeTable.size() - 1; i++) {
                if(parity++ % 2 == 0) {
                    fillTheLine(
                            Math.round(activeEdgeTable.get(i).getxMin()),
                            Math.round(activeEdgeTable.get(i+1).getxMin()),
                            y);
                }
            }
            ++y;

            int finalY = y;
            activeEdgeTable.removeIf(e -> e.getyMax() == finalY);
            activeEdgeTable.forEach(edge -> edge.setxMin(edge.getxMin() + edge.getInverseSlope()));
        }
    }

    private void fillTheLine(int start, int end, int height) {
        if(hasPatternFilling) {
            var reader = pattern.getPixelReader();
            int w = (int) pattern.getWidth();
            int h = (int) pattern.getHeight();

            for(int i = start; i < end; i++) {
                drawPixel(i, height, reader.getColor(i % w, height % h));
            }
        } else {
            for(int i = start; i < end; i++) {
                drawPixel(i, height, filling);
            }
        }
    }

    public void clip(Polygon polygon) {
        var list = sutherlandHodgman(polygon);
        var color = lines.get(0).getColor();
        lines.clear();

        for(int i = 0; i < list.size(); i++) {
            var index = i == list.size() - 1 ? 0 : i + 1;
            var line = new Line(list.get(i), list.get(index));
            line.setThickness(thickness);
            line.setColor(color);
            lines.add(new Line(list.get(i), list.get(index)));
        }
    }

    private List<Point> sutherlandHodgman(Polygon polygon) {

        var outPoly = lines
                .stream()
                .map(line -> new Point(line.getX1(), line.getY1()))
                .collect(Collectors.toList());

        var clipBoundary = polygon.getLines()
                .stream()
                .map(line -> new Point(line.getX1(), line.getY1()))
                .collect(Collectors.toList());

        var prevClip = clipBoundary.get(clipBoundary.size()-1);

        for (Point currClip : clipBoundary) {

            var input = outPoly;
            outPoly = new ArrayList<>();

            var prevInput = input.get(input.size() - 1);

            for (Point currInput : input) {

                if (isInside(prevClip, currClip, currInput)) {
                    if (!isInside(prevClip, currClip, prevInput)) {
                        outPoly.add(intersection(prevClip, currClip, prevInput, currInput));
                    }
                    outPoly.add(currInput);
                } else if (isInside(prevClip, currClip, prevInput)) {
                    outPoly.add(intersection(prevClip, currClip, prevInput, currInput));
                }

                prevInput = currInput;
            }
            prevClip = currClip;
        }
        return outPoly;
    }

    private boolean isInside(Point a, Point b, Point c) {
        return (b.getX() - a.getX()) * (c.getY() - a.getY()) <= (b.getY() - a.getY()) * (c.getX() - a.getX());
    }

    private Point intersection(Point a, Point b, Point p, Point q) {
        double A1 = b.getY() - a.getY();
        double B1 = a.getX() - b.getX();
        double C1 = A1 * a.getX() + B1 * a.getY();

        double A2 = q.getY() - p.getY();
        double B2 = p.getX() - q.getX();
        double C2 = A2 * p.getX() + B2 * p.getY();

        double det = A1 * B2 - A2 * B1;
        int x = (int)((B2 * C1 - B1 * C2) / det);
        int y = (int)((A1 * C2 - A2 * C1) / det);

        return new Point(x,y);
    }

    public boolean isConvex() {
        boolean wasNegative = false;
        boolean wasPositive = false;
        int b, c;

        var points = lines.stream().map(line -> new Point(line.getX1(), line.getY1())).collect(Collectors.toList());

        for (int a = 0; a < lines.size(); a++)
        {
            b = (a + 1) % lines.size();
            c = (b + 1) % lines.size();

            int cross_product = crossProductMagnitude(points.get(a), points.get(b), points.get(c));

            if (cross_product < 0) wasNegative = true;
            else if (cross_product > 0) wasPositive = true;

            if (wasNegative && wasPositive) return false;
        }

        return true;
    }

    private int crossProductMagnitude(Point a, Point b, Point c) {
        return ((a.getX() - b.getX()) * (c.getY() - b.getY()))
                - ((a.getY() - b.getY()) * (c.getX() - b.getX()));
    }

    public List<Line> getLines() {
        return lines;
    }

    public void setLines(List<Line> lines) {
        this.lines = lines;
    }

    public int getThickness() {
        return thickness;
    }

    public void setThickness(int thickness) {
        this.thickness = thickness;
        lines.forEach(line -> line.setThickness(thickness));
    }

    @Override
    public void setColor(Color color) {
        super.setColor(color);
        lines.forEach(line -> line.setColor(color));
    }

    public Color getFilling() {
        return filling;
    }

    public void setFilling(Color filling) {
        this.filling = filling;
        fillingString = filling.toString();
    }

    public Image getPattern() {
        return pattern;
    }

    public void setPattern(Image pattern) {
        this.pattern = pattern;
        imageName = pattern.getUrl();
    }

    public boolean isHasPatternFilling() {
        return hasPatternFilling;
    }

    public void setHasPatternFilling(boolean hasPatternFilling) {
        this.hasPatternFilling = hasPatternFilling;
    }

    public String getFillingString() {
        return fillingString;
    }

    public void setFillingString(String fillingString) {
        this.fillingString = fillingString;
        filling = Color.valueOf(fillingString);
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
        if(imageName != null) pattern = new Image(imageName);
    }
}
