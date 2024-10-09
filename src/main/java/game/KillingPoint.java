package game;

import javafx.scene.paint.Color;
import javafx.scene.canvas.GraphicsContext;

import java.util.Vector;

public abstract class KillingPoint extends Particle {
    public KillingPoint(int team) {
        super(0.012);
        super.setTeam(team);
    }
    public void kill(Agent agent) {
        agent.die();
    }
    public void evolve(Game game){
        Vector<Agent> agents = game.getAgents();
        Agent agent;
        double posX = getPosX();
        double posY = getPosY();
        for (int i = 0; i < agents.size(); i++) {
            agent = agents.get(i);
            double diffX = agent.getPosX() - posX;
            double diffY = agent.getPosY() - posY;
            if (team != agent.getTeam() && Math.sqrt(diffX*diffX+ diffY*diffY) < agent.getRadius()){
                kill(agent);
            }
        }
        colorShift=0.55+0.45*Math.sin((double)(0.2*game.getFrameCount()));
    }
    @Override
    public void draw(GraphicsContext graphicsContext, Game game){
        graphicsContext.setFill(Color.WHITE.deriveColor(0,1,colorShift, 0.5*colorShift));
        double size = game.getScreenSize((6*colorShift)*radius);
        graphicsContext.fillOval(game.getScreenPosX(getGraphicalPosition().getX())-0.5*size, game.getScreenPosY(getGraphicalPosition().getY())-0.5*size, size, size);
        super.draw(graphicsContext, game);
    }

    public abstract double getPosX();
    public abstract double getPosY();
    public abstract Position getGraphicalPosition();
}
