package strategy;
import game.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import game.Game;

public class NNStrategy1output extends NNStrategy {
    private int scoreMethod;
    public NNStrategy1output(Game game, Agent controlledAgent){
        super(game, controlledAgent);
        this.controlledAgent = controlledAgent;
        numInputs = 2*(game.getAgentsNumber()+game.getKillingPointsNumber())+6;
        numOutputs = 1;
        epsilon = 0.8;
        epsilonMultiplier = 0.95;
        gamma = 0.7;
        maxHistoryDepth = 10;
        learningRate = 0.001;
        learningRateMultiplier = 0.99;
        nEpochs = 50;
        rewardIntensity = 1;
        punishmentIntensity = -1;
        intermediateLearn = false;
        scoreMethod = 0;
        Random random = new Random();
        nn = new NNdl4j(learningRate,random.nextInt(10000), numInputs, numOutputs);
    }
    private double calculateScore(){
        if (scoreMethod == 0) return nn.predict(calculateState())[0];
        return invertDistanceSumHeuristic(controlledAgent, false, 0);
    }
    @Override
    public double simulateMove(Agent agent, Position position, double dx, double dy) {
        agent.setPos(position);
        agent.move(dx,dy);
        return calculateScore();
    }
    public void decide(Agent agent){
        Random random = new Random();
        if (random.nextFloat() < epsilon) {
            scoreMethod = 1;
            goToBestPosition(agent);
            /*
            agent.setOrderX(random.nextInt(3)-1);
            agent.setOrderY(random.nextInt(3)-1);
            */
        } else {
            scoreMethod = 0;
            goToBestPosition(agent);
        }
    }
    @Override
    public void learn(double reward){
        int size = states.size();
        int firstState = 0;
        int lastState = Math.min(size-1, maxHistoryDepth);
        double[] statesFeatures = calculateFeatures(firstState, lastState);
        int featuresSize = lastState-firstState+1;
        double[] rewards = new double[featuresSize];
        for (int i = firstState; i < featuresSize; i++) {
            rewards[i] = reward;
            reward *= gamma;
        }
        nn.fit(statesFeatures, rewards, featuresSize, nEpochs);
        epsilon*=epsilonMultiplier;
        learningRate *= learningRateMultiplier;
        nn.setLearningRate(learningRate);
    }
}
