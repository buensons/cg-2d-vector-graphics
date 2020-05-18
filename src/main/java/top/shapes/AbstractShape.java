package top.shapes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import top.App;
import top.Controller;

import java.io.File;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Line.class, name = "Line"),
        @JsonSubTypes.Type(value = Polygon.class, name = "Polygon"),
        @JsonSubTypes.Type(value = MidpointCircle.class, name = "Circle")
})
public abstract class AbstractShape implements Shape {

    @JsonIgnore
    protected Color color = Color.WHITE;

    // for serialization purposes
    protected String stringColor = Color.WHITE.toString();

    protected void drawPixel(int x, int y) {
        Controller.getWriter().setColor(x, y, color);
    }

    @Override
    public void save() {
        ObjectMapper mapper = new ObjectMapper();
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilter
                = new FileChooser.ExtensionFilter("json", "*.json");

        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showSaveDialog(App.getMainStage());

        if(file != null) {
            try {
                mapper.writeValue(file, this);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void drawPixel(int x, int y, Color c) {
        Controller.getWriter().setColor(x, y, c);
    }

    protected void clearPixel(int x, int y) {
        Controller.getWriter().setColor(x, y, Color.BLACK);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
        stringColor = color.toString();
    }

    public String getStringColor() {
        return stringColor;
    }

    public void setStringColor(String stringColor) {
        this.stringColor = stringColor;
        color = Color.valueOf(stringColor);
    }
}
