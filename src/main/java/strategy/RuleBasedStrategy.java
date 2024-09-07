package strategy;

import game.Agent;
import game.Game;
import game.Position;

import java.util.Random;

public class RuleBasedStrategy extends Strategy{
    private double epsilon;
    private double epsilonCoefficient;
    private int heuristic;
    public RuleBasedStrategy(Game game, int heuristic) {
        super(game);
        this.heuristic = heuristic;
        epsilon = 0.8;
        epsilonCoefficient = 0.98;
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
    public double simulateMove(Agent agent, Position position, double dx, double dy) {
        agent.setPos(position);
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
    @Override
    public void learn(double reward){
        epsilon *= epsilonCoefficient;
    }
}
