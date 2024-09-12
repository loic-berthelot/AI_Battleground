package arena;

import game.Agent;
import game.Game;
import game.Position;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class SquareArena extends Arena{
    public void draw(GraphicsContext graphicsContext, Game game){
        double radius = game.getArenaRadius();
        graphicsContext.setFill(new Color(0,0,0,1));
        graphicsContext.fillRect(10,10, 2*radius,2*radius);
        displayGrid(graphicsContext, game,5, 5);
    }
    public void displayGrid(GraphicsContext graphicsContext, Game game, int lines, int columns){
        graphicsContext.setStroke(new Color(1,1,1,0.2));
        graphicsContext.setLineWidth(1);
        double pos;
        for (double i = 1; i <= lines; i++) {
            pos = 2 * i / (double) (lines+1) - 1;
            pos = game.getScreenPosY(pos);
            graphicsContext.strokeLine(game.getScreenPosX(1), pos, game.getScreenPosX(-1), pos);
        }
        for (double i = 1; i <= columns; i++) {
            pos = 2 * i / (double) (columns+1) - 1;
            pos = game.getScreenPosX(pos);
            graphicsContext.strokeLine(pos, game.getScreenPosY(1), pos, game.getScreenPosY(-1));
        }
        graphicsContext.setStroke(new Color(1,1,1,0.15));
        graphicsContext.strokeLine(game.getScreenPosX(0), game.getScreenPosY(-1), game.getScreenPosX(0), game.getScreenPosY(1));
        graphicsContext.strokeLine(game.getScreenPosX(-1), game.getScreenPosY(0), game.getScreenPosX(1), game.getScreenPosY(0));
    }
    public void replaceAgent(Agent agent){
        Position position = agent.getPosition();
        double x = position.getX();
        double y = position.getY();
        double radius = agent.getRadius();
        if (x < radius-1) position.setX(radius-1);
        if (y < radius-1) position.setY(radius-1);
        if (x > 1-radius) position.setX(1-radius);
        if (y > 1-radius) position.setY(1-radius);
    }

    @Override
    public boolean hasCorners(){ return true; }
}
