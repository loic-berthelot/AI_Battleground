package strategy;

import game.Agent;
import game.Game;
import game.Position;

import java.util.Random;

public class RuleBasedStrategy extends Strategy{
    private double epsilon;
    private double epsilonCoefficient;
    private int heuristic;
    private Agent controlledAgent;
    public RuleBasedStrategy(Game game, Agent controlledAgent, int heuristic) {
        super(game);
        this.controlledAgent = controlledAgent;
        this.heuristic = heuristic;
        epsilon = 0.5;
        epsilonCoefficient = 0.997;
    }
    public double heuristicResult(Agent agent) {
        switch(heuristic) {
            case 0 :
                return dangerPreyHeuristic(agent, 2.5);
            case 1 :
                return dangerPreyHeuristic(agent, 0.5);
            case 2 :
                return invertDistanceSumHeuristic(agent, false, -0.075);
            case 3 :
                return invertDistanceSumHeuristic(agent, false, -0.05);
            case 4 :
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
        if (random.nextDouble() <= epsilon) {
            agent.setOrderX(random.nextInt(3)-1);
            agent.setOrderY(random.nextInt(3)-1);
        } else {
            goToBestPosition(agent);
        }
    }
    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }
    @Override
    public void learn(boolean victory, int nEpochs){
        //epsilon *= epsilonCoefficient;
        //if (epsilon < 0.02) epsilon = 0.8;
        double winProportion = game.getGameHistory().getWinProportion(controlledAgent.getTeam());
        epsilon = 0.5*winProportion*winProportion;
    }
}
