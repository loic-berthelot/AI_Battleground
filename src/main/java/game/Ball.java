package game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


public class Ball extends Particle {
    Position position;
    public Ball(){
        super(0.045);
        position = new Position(0, 0);
        mass = 0.5;
    }

    @Override
    public Position getGraphicalPosition() {
        return position;
    }

    @Override
    public double getPosX() {
        return position.getX();
    }

    @Override
    public double getPosY() {
        return position.getY();
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public Color getColor(){
        return Color.WHITE;
    }
    public void draw(GraphicsContext graphicsContext, Game game) {
        super.draw(graphicsContext, game);
    }
}
