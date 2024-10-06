package game;

import javafx.scene.paint.Color;
import javafx.scene.canvas.GraphicsContext;

import java.util.Vector;

public class KillingPoint extends Particle {
    Agent agent1;
    Agent agent2;
    public KillingPoint(Agent agent1, Agent agent2) {
        super(0.008);
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
                //agent.getStrategy().learn(false, 20);
            }
        }
        colorShift=0.55+0.45*Math.sin((double)(0.2*game.getFrameCount()));
    }
    @Override
    public void draw(GraphicsContext graphicsContext, Game game){
        graphicsContext.setStroke(game.getTeamColor(team));
        graphicsContext.setLineWidth(0.8);
        double posX1 = game.getScreenPosX(agent1.getGraphicalPosition().getX());
        double posY1 = game.getScreenPosY(agent1.getGraphicalPosition().getY());
        double posX2 = game.getScreenPosX(agent2.getGraphicalPosition().getX());
        double posY2 = game.getScreenPosY(agent2.getGraphicalPosition().getY());
        graphicsContext.strokeLine(posX1, posY1, posX2, posY2);

        graphicsContext.setFill(Color.WHITE.deriveColor(0,1,colorShift, 0.5*colorShift));
        double size = game.getScreenSize((6*colorShift)*radius);
        graphicsContext.fillOval(game.getScreenPosX(getPosX())-0.5*size, game.getScreenPosY(getPosY())-0.5*size, size, size);
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
    public Position getPosition(){
        return new Position(getPosX(), getPosY());
    }
    public Position getGraphicalPosition(){
        return new Position(0.5*(agent1.getGraphicalPosition().getX()+agent2.getGraphicalPosition().getX()), 0.5*(agent1.getGraphicalPosition().getY()+agent2.getGraphicalPosition().getY()));
    }
}
