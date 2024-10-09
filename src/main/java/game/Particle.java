package game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


public abstract class Particle {
    protected double radius;
    protected int team;
    protected double colorShift;
    protected double mass;
    Particle(double radius) {
        this.radius = radius;
        team = 0;
        colorShift = 1;
        mass = 1;
    }
    public void setTeam(int team) {
        this.team = team;
    }
    public Color getColor() {
        return Environment.getTeamColor(team);
    }
    public void draw(GraphicsContext graphicsContext, Game game){
        graphicsContext.setFill(getColor().deriveColor(0,1,colorShift, 1));
        double size = game.getScreenSize(2*radius);
        graphicsContext.fillOval(game.getScreenPosX(getGraphicalPosition().getX()-radius), game.getScreenPosY(getGraphicalPosition().getY()+radius), size, size);
    }
    public int getTeam() {
        return team;
    }
    public abstract Position getGraphicalPosition();
    public abstract double getPosX();
    public abstract double getPosY();
    public Position getPosition(){ return new Position(getPosX(), getPosY()); }
    public double getRadius() {
        return radius;
    }
    public double getMass(){
        return mass;
    }
}
