package game;

import javafx.scene.paint.Color;
import javafx.scene.canvas.GraphicsContext;

import java.util.Vector;

public class KillingPoint extends Particle {
    Agent agent1;
    Agent agent2;
    public KillingPoint(Agent agent1, Agent agent2) {
        super(0.01);
        this.agent1 = agent1;
        this.agent2 = agent2;
        super.setTeam(agent1.getTeam());
    }
    public void kill(Agent agent) {
        agent.die();
        agent1.incrementKillCount();
        agent2.incrementKillCount();
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
    }
    @Override
    public void draw(GraphicsContext graphicsContext, Game game){
        int arenaRadius = game.getArenaRadius();
        int centerX = game.getCenterArenaX();
        int centerY = game.getCenterArenaY();
        graphicsContext.setStroke(Color.WHITE);
        graphicsContext.setLineWidth(1);
        double posX1 = centerX+agent1.getGraphicalPosition().getX()*arenaRadius;
        double posY1 = centerY-agent1.getGraphicalPosition().getY()*arenaRadius;
        double posX2 = centerX+agent2.getGraphicalPosition().getX()*arenaRadius;
        double posY2 = centerY-agent2.getGraphicalPosition().getY()*arenaRadius;
        graphicsContext.strokeLine(posX1, posY1, posX2, posY2);
        super.draw(graphicsContext, game);
    }
    public Agent getAgent1(){
        return agent1;
    }
    public Agent getAgent2(){
        return agent1;
    }
    public double getPosX(){
        return 0.5*(agent1.getPosX()+agent2.getPosX());
    }
    public double getPosY(){
        return 0.5*(agent1.getPosY()+agent2.getPosY());
    }
}
