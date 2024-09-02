package strategy;

import game.Agent;
import game.Game;

import java.util.Random;

public class RuleBasedStrategy extends Strategy{
    private double epsilon;
    private double epsilonCoefficient;
    private int heuristic;
    public RuleBasedStrategy(Game game, int heuristic) {
        super(game);
        this.heuristic = heuristic;
        epsilon = 0;
        epsilonCoefficient = 0.99;
    }
    public double heuristicResult(Agent agent) {
        switch(heuristic) {
            case 0 :
                return invertDistanceSumHeuristic(agent, true, 0.012);
            default :
                return invertDistanceSumHeuristic(agent, false,-0.01);
        }
    }
    @Override
    public double simulateMove(Agent agent, double posX, double posY, double dx, double dy) {
        agent.setPos(posX, posY);
        agent.move(dx,dy);
        return heuristicResult(agent);
    }
    @Override
    public void decide(Agent agent) {
        Random random = new Random();
        if (random.nextFloat() < epsilon) {
            agent.setOrderX(random.nextInt(3)-1);
            agent.setOrderY(random.nextInt(3)-1);
        } else {
            goToBestPosition(agent);
        }
        epsilon *= epsilonCoefficient;
    }
    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }
}
