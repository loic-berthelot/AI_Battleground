package game;

public class Position {
    private double x;
    private double y;
    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public Position(Position pos) {
        x = pos.x;
        y = pos.y;
    }
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setCoordinates(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void addX(double x) {
        this.x += x;
    }

    public void addY(double y) {
        this.y += y;
    }

    public double distanceToCenter() {
        return Math.sqrt(x*x + y*y);
    }

    public void multiply(double coefficient) {
        x *= coefficient;
        y *= coefficient;
    }

    public double getOppositeDirection() {
        return Math.atan2(-y, -x);
    }
}
