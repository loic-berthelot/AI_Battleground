package arena;

import game.Agent;
import game.Game;
import game.Position;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class TorusArena extends Arena{
    final private double internalRadius;
    public TorusArena(double internalRadius) {
        this.internalRadius = internalRadius;
    }
    public void draw(GraphicsContext graphicsContext, Game game) {
        double radius = game.getArenaRadius();
        double screenInternalRadius = game.getScreenSize(internalRadius);
        graphicsContext.setFill(new Color(0,0,0,1));
        graphicsContext.fillOval(10,10, 2*radius,2*radius);
        graphicsContext.setFill(game.getBackgroundColor());
        graphicsContext.fillOval(game.getScreenPosX(0)-screenInternalRadius,game.getScreenPosY(0)-screenInternalRadius, 2*screenInternalRadius,2*screenInternalRadius);
        displayGrid(graphicsContext, game,2, 12);
    }
    public void displayGrid(GraphicsContext graphicsContext, Game game, int lines, int columns){
        graphicsContext.setStroke(new Color(1,1,1,0.2));
        graphicsContext.setLineWidth(1);
        double pos;
        for (double i = 1; i <= lines; i++) {
            pos = game.getScreenSize(internalRadius + i *(1-internalRadius) /  (lines+1));
            graphicsContext.strokeOval(game.getScreenPosX(0)-pos, game.getScreenPosY(0)-pos, 2*pos, 2*pos);
        }
        for (double i = 1; i <= columns; i++) {
            pos = 2*i * Math.PI/ columns;
            graphicsContext.strokeLine(game.getScreenPosX(internalRadius*Math.cos(pos)), game.getScreenPosY(internalRadius*Math.sin(pos)), game.getScreenPosX(Math.cos(pos)), game.getScreenPosY(Math.sin(pos)));
        }
        for (double i = 1; i <= 4; i++) {
            pos = i * Math.PI/ 2;
            graphicsContext.strokeLine(game.getScreenPosX(internalRadius*Math.cos(pos)), game.getScreenPosY(internalRadius*Math.sin(pos)), game.getScreenPosX(Math.cos(pos)), game.getScreenPosY(Math.sin(pos)));
        }
    }
    public void replaceAgent(Agent agent){
        Position position = agent.getPosition();
        double agentRadius = agent.getRadius();
        double dist = position.distanceToCenter();
        if (dist > 1-agentRadius) {
            position.multiply((1-agentRadius)/dist);
        } else if (dist < internalRadius+agentRadius){
            position.multiply((internalRadius+agentRadius)/dist);
        }
    }
    @Override
    public double getInternalRadius(){ return internalRadius;}
}
