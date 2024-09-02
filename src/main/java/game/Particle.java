package game;

import javafx.scene.canvas.GraphicsContext;

public abstract class Particle {
    protected double radius;
    protected int team;
    Particle(double radius) {
        this.radius = radius;
        team = 0;
    }
    public void setTeam(int team) {
        this.team = team;
    }
    public void draw(GraphicsContext graphicsContext, Game game){
        int arenaRadius = game.getArenaRadius();
        graphicsContext.setFill(game.getTeamColor(team));
        double size = 2*radius*arenaRadius;
        graphicsContext.fillOval(game.getCenterArenaX()+(getPosX()-radius)*arenaRadius, game.getCenterArenaY()-(getPosY()+radius)*arenaRadius, size, size);
    }
    public int getTeam() {
        return team;
    }
    public abstract double getPosX();
    public abstract double getPosY();
    public double getRadius() {
        return radius;
    }
}
