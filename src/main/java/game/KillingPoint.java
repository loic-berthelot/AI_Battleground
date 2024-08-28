package game;

import javafx.scene.paint.Color;
import javafx.scene.canvas.GraphicsContext;

import java.util.Vector;

public class KillingPoint extends Particle {
    Agent agent1;
    Agent agent2;
    public void updatePosition(){
        posX = 0.5*(agent1.getPosX()+agent2.getPosX());
        posY = 0.5*(agent1.getPosY()+agent2.getPosY());
    }
    public KillingPoint(Agent agent1, Agent agent2) {
        super(0,0,0.01);
        this.agent1 = agent1;
        this.agent2 = agent2;
        updatePosition();
        team = agent1.getTeam();
    }
    public void evolve(Game game){
        updatePosition();
        Vector<Agent> agents = game.getAgents();
        Agent agent;
        for (int i = 0; i < agents.size(); i++) {
            agent = agents.get(i);
            double diffX = agent.getPosX() - posX;
            double diffY = agent.getPosY() - posY;
            if (team != agent.getTeam() && Math.sqrt(diffX*diffX+ diffY*diffY) < agent.getRadius()){
                game.nextRound(team);
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
        double posX1 = centerX+agent1.getPosX()*arenaRadius;
        double posY1 = centerY-agent1.getPosY()*arenaRadius;
        double posX2 = centerX+agent2.getPosX()*arenaRadius;
        double posY2 = centerY-agent2.getPosY()*arenaRadius;
        graphicsContext.strokeLine(posX1, posY1, posX2, posY2);
        super.draw(graphicsContext, game);
    }
    public Agent getAgent1(){
        return agent1;
    }
    public Agent getAgent2(){
        return agent1;
    }
}
