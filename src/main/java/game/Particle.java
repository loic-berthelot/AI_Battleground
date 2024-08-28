package game;

import javafx.scene.canvas.GraphicsContext;

public class Particle {
    protected double posX;
    protected double posY;
    protected double radius;
    protected int team;
    Particle(double posX, double posY, double radius) {
        this.posX = posX;
        this.posY = posY;
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
        graphicsContext.fillOval(game.getCenterArenaX()+(posX-radius)*arenaRadius, game.getCenterArenaY()-(posY+radius)*arenaRadius, size, size);
    }
    public int getTeam() {
        return team;
    }
    public double getPosX() {
        return posX;
    }
    public double getPosY() {
        return posY;
    }
    public double getRadius() {
        return radius;
    }
    public void setPos(double posX, double posY) {
        this.posX = posX;
        this.posY = posY;
    }
}
