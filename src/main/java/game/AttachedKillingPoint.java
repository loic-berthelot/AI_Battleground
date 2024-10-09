package game;

import javafx.scene.canvas.GraphicsContext;

public class AttachedKillingPoint extends KillingPoint {
    private Agent agent1;
    private Agent agent2;
    public AttachedKillingPoint(Agent agent1, Agent agent2) {
        super(agent1.getTeam());
        this.agent1 = agent1;
        this.agent2 = agent2;
    }
    @Override
    public void kill(Agent agent) {
        super.kill(agent);
        agent1.incrementKillCount();
        agent2.incrementKillCount();
    }

    public Agent getAgent1(){
        return agent1;
    }
    public Agent getAgent2(){
        return agent2;
    }
    public double getPosX(){
        return 0.5*(agent1.getPosX()+agent2.getPosX());
    }
    public double getPosY(){
        return 0.5*(agent1.getPosY()+agent2.getPosY());
    }
    @Override
    public void draw(GraphicsContext graphicsContext, Game game) {
        graphicsContext.setStroke(Environment.getTeamColor(team));
        graphicsContext.setLineWidth(0.8);
        double posX1 = game.getScreenPosX(agent1.getGraphicalPosition().getX());
        double posY1 = game.getScreenPosY(agent1.getGraphicalPosition().getY());
        double posX2 = game.getScreenPosX(agent2.getGraphicalPosition().getX());
        double posY2 = game.getScreenPosY(agent2.getGraphicalPosition().getY());
        graphicsContext.strokeLine(posX1, posY1, posX2, posY2);
        super.draw(graphicsContext, game);
    }
    public Position getGraphicalPosition(){
        return new Position(0.5*(agent1.getGraphicalPosition().getX()+agent2.getGraphicalPosition().getX()), 0.5*(agent1.getGraphicalPosition().getY()+agent2.getGraphicalPosition().getY()));
    }
}
