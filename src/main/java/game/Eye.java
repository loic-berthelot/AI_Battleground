package game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Eye {
    private Agent agent;
    private Position position;
    private double angleShift;
    final private double radius;
    final private double pupilRadius;
    final private double forwardShift;
    final private double pupilForwardShift;
    public Eye(Agent agent, double angleShift){
        this.agent = agent;
        this.angleShift = angleShift;
        radius = 0.45*agent.getRadius();
        pupilRadius = 0.6*radius;
        forwardShift = 0.7*agent.getRadius();
        pupilForwardShift = 0.12 * forwardShift;
    }
    public double getOrientation(){
        return agent.getOrientation()+angleShift;
    }
    public double getPupilOrientation(){
        return getOrientation()-1.2*angleShift;
    }
    public double getPosX(){
        return agent.getGraphicalPosition().getX()+forwardShift*Math.cos(getOrientation());
    }
    public double getPosY(){
        return agent.getGraphicalPosition().getY()+forwardShift*Math.sin(getOrientation());
    }
    public double getPupilPosX(){
        return getPosX()+pupilForwardShift*Math.cos(getPupilOrientation());
    }
    public double getPupilPosY(){
        return getPosY()+pupilForwardShift*Math.sin(getPupilOrientation());
    }
    public void draw(GraphicsContext graphicsContext, Game game) {
        int arenaRadius = game.getArenaRadius();
        graphicsContext.setFill(Color.WHITE);
        double size = radius * arenaRadius;
        graphicsContext.fillOval(game.getCenterArenaX() + getPosX() * arenaRadius - 0.5 * size, game.getCenterArenaY() - getPosY() * arenaRadius - 0.5 * size, size, size);
        graphicsContext.setFill(Color.BLACK);
        size = pupilRadius * arenaRadius;
        graphicsContext.fillOval(game.getCenterArenaX() + getPupilPosX() * arenaRadius - 0.5 * size, game.getCenterArenaY() - getPupilPosY() * arenaRadius - 0.5 * size, size, size);

    }
}
