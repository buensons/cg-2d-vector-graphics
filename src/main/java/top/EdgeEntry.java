package top;

public class EdgeEntry {
    private int yMax;
    private float xMin;
    private float inverseSlope;

    public EdgeEntry(int y, float x, float inverseSlope) {
        yMax = y;
        xMin = x;
        this.inverseSlope = inverseSlope;
    }

    public int getyMax() {
        return yMax;
    }

    public void setyMax(int yMax) {
        this.yMax = yMax;
    }

    public float getxMin() {
        return xMin;
    }

    public void setxMin(float xMin) {
        this.xMin = xMin;
    }

    public float getInverseSlope() {
        return inverseSlope;
    }

    public void setInverseSlope(float inverseSlope) {
        this.inverseSlope = inverseSlope;
    }
}
