package game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class AreaOfEffect {

    final private Position position;
    final private double halfWidth;
    final private double halfHeight;
    final private int team;
    private Color color;
    public AreaOfEffect(Position position, double halfWidth, double halfHeight, int team, Color color) {
        this.position = position;
        this.team = team;
        this.halfWidth = halfWidth;
        this.halfHeight = halfHeight;
        this.color = color;
    }
    private double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x2 - x1) * (x2-x1) + (y2 - y1) * (y2 - y1));
    }
    public boolean containsPart(Agent agent) {
        final double aX = agent.getPosX();
        final double aY = agent.getPosY();
        final double pX = position.getX();
        final double pY = position.getY();
        double testPosX = aX;
        double testPosY = aY;
        if (testPosX < pX - halfWidth) testPosX = pX - halfWidth;
        else if (testPosX > pX + halfWidth) testPosX = pX + halfWidth;
        if (testPosY < pY - halfHeight) testPosY = pY - halfHeight;
        else if (testPosY > pY + halfHeight) testPosY = pY + halfHeight;
        return distance(testPosX, testPosY, aX, aY) < agent.getRadius();
    }
    public boolean containsCenter(Agent agent) {
        final double aX = agent.getPosX();
        final double aY = agent.getPosY();
        final double pX = position.getX();
        final double pY = position.getY();
        return aX <= pX + halfWidth && aX >= pX - halfWidth && aY <= pY + halfHeight && aY >= pY - halfHeight;
    }
    public boolean containsAll(Agent agent) {
        final double aX = agent.getPosX();
        final double aY = agent.getPosY();
        final double pX = position.getX();
        final double pY = position.getY();
        final double radius = agent.getRadius();
        return aX + radius <= pX + halfWidth && aX - radius >= pX - halfWidth && aY + radius <= pY + halfHeight && aY -radius >= pY - halfHeight;
    }
    public void draw(GraphicsContext graphicsContext, Game game){
        graphicsContext.setFill(color);
        graphicsContext.fillRect(game.getScreenPosX(position.getX()-halfWidth), game.getScreenPosY(position.getY()+halfHeight), game.getScreenSize(2*halfWidth), game.getScreenSize(2*halfHeight));
    }

    public Position getPosition() {
        return position;
    }

    public double getHalfWidth() {
        return halfWidth;
    }

    public double getHalfHeight() {
        return halfHeight;
    }

    public int getTeam() {
        return team;
    }
}
