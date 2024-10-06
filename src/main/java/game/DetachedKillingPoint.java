package game;

import javafx.scene.canvas.GraphicsContext;

public class DetachedKillingPoint extends KillingPoint {
    private Position position;
    private Game game;
    public DetachedKillingPoint(int team, Position position, Game game) {
        super(team);
        this.position = position;
        this.game = game;
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
    public Position getGraphicalPosition() {
        return position;
    }
    @Override
    public void draw(GraphicsContext graphicsContext, Game game) {
        super.draw(graphicsContext, game);
    }
}
