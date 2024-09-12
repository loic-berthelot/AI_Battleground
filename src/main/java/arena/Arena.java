package arena;

import game.Agent;
import game.Game;
import game.Position;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class Arena {
    public abstract void draw(GraphicsContext graphicsContext, Game game);
    public abstract void displayGrid(GraphicsContext graphicsContext, Game game, int lines, int columns);
    public abstract void replaceAgent(Agent agent);
    public boolean hasCorners(){ return false; }
    public double getInternalRadius(){ return 0;}
}
