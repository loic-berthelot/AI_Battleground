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
    private double pupilMovementShift;
    final private double movementShiftLimit;
    public Eye(Agent agent, double angleShift){
        this.agent = agent;
        this.angleShift = angleShift;
        radius = 0.22*agent.getRadius();
        pupilRadius = 0.6*radius;
        forwardShift = 0.7*agent.getRadius();
        pupilForwardShift = 0.12 * forwardShift;
        movementShiftLimit = 0.4*Math.PI;
    }
    public double getOrientation(){
        return agent.getOrientation()+angleShift;
    }
    public void adjustOrientation(int direction){
        if (direction == 0) {
            pupilMovementShift *= 0.85;
            if (Math.abs(pupilMovementShift) < 0.15){
                pupilMovementShift = 0;
            }
        } else if(direction > 0){
            pupilMovementShift += 0.6;
            if (pupilMovementShift > movementShiftLimit) pupilMovementShift = movementShiftLimit;
        } else {
            pupilMovementShift -= 0.6;
            if (pupilMovementShift < -movementShiftLimit) pupilMovementShift = -movementShiftLimit;
        }
    }
    public double getPupilOrientation(){
        return getOrientation()+pupilMovementShift-1.2*angleShift;
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
        graphicsContext.setFill(Color.WHITE);
        double size = game.getScreenSize(2*radius);
        graphicsContext.fillOval(game.getScreenPosX(getPosX() - radius), game.getScreenPosY(getPosY() + radius), size, size);
        graphicsContext.setFill(Color.BLACK);
        size = game.getScreenSize(2*pupilRadius);
        graphicsContext.fillOval(game.getScreenPosX(getPupilPosX() - pupilRadius), game.getScreenPosY(getPupilPosY() + pupilRadius), size, size);
    }
}
