package game;

import javafx.scene.canvas.GraphicsContext;

public abstract class Particle {
    protected double radius;
    protected int team;
    protected double colorShift;
    Particle(double radius) {
        this.radius = radius;
        team = 0;
        colorShift = 1;
    }
    public void setTeam(int team) {
        this.team = team;
    }
    public void draw(GraphicsContext graphicsContext, Game game){
        graphicsContext.setFill(game.getTeamColor(team).deriveColor(0,1,colorShift, 1));
        double size = game.getScreenSize(2*radius);
        graphicsContext.fillOval(game.getScreenPosX(getGraphicalPosition().getX()-radius), game.getScreenPosY(getGraphicalPosition().getY()+radius), size, size);
    }
    public int getTeam() {
        return team;
    }
    public abstract Position getGraphicalPosition();
    public abstract double getPosX();
    public abstract double getPosY();
    public double getRadius() {
        return radius;
    }
}
