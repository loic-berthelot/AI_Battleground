package arena;

import game.Agent;
import game.Game;
import game.Position;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class CircularArena extends Arena{
    public void draw(GraphicsContext graphicsContext, Game game) {
        double radius = game.getArenaRadius();
        graphicsContext.setFill(new Color(0,0,0,1));
        graphicsContext.fillOval(10,10, 2*radius,2*radius);
        displayGrid(graphicsContext, game,5, 5);
    }
    public void displayGrid(GraphicsContext graphicsContext, Game game, int lines, int columns){
        graphicsContext.setStroke(new Color(1,1,1,0.2));
        graphicsContext.setLineWidth(1);
        double pos, extremity;
        for (double i = 1; i <= lines; i++) {
            pos = 2 * i / (double) (lines+1) - 1;
            extremity = Math.sqrt(1-pos*pos);
            pos = game.getScreenPosY(pos);
            graphicsContext.strokeLine(game.getScreenPosX(extremity), pos, game.getScreenPosX(-extremity), pos);
        }
        for (double i = 1; i <= columns; i++) {
            pos = 2 * i / (double) (columns+1) - 1;
            extremity = Math.sqrt(1-pos*pos);
            pos = game.getScreenPosX(pos);
            graphicsContext.strokeLine(pos, game.getScreenPosY(extremity), pos, game.getScreenPosY(-extremity));
        }
        graphicsContext.setStroke(new Color(1,1,1,0.15));
        graphicsContext.strokeLine(game.getScreenPosX(0), game.getScreenPosY(-1), game.getScreenPosX(0), game.getScreenPosY(1));
        graphicsContext.strokeLine(game.getScreenPosX(-1), game.getScreenPosY(0), game.getScreenPosX(1), game.getScreenPosY(0));
    }
    public void replaceAgent(Agent agent) {
        Position position = agent.getPosition();
        double radius = agent.getRadius();
        double dist = position.distanceToCenter();
        if (dist > 1-radius) {
            position.multiply((1-radius)/dist);
        }
    }
}
