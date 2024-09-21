package strategy;
import game.*;

import java.util.Random;

import game.Game;

public class NNStrategy1output extends NNStrategy {
    private int scoreMethod;
    public NNStrategy1output(Game game, Agent controlledAgent){
        super(game, controlledAgent);
        this.controlledAgent = controlledAgent;
        numOutputs = 1;
        scoreMethod = 0;
        intermediateLearn = false;
        Random random = new Random();
        neuralNetwork = new NNdl4j(learningRate,random.nextInt(10000), numInputs, numOutputs);

        gamma = 0.98;
        maxHistoryDepth = 12;

        epsilon = 0.8;
        epsilonMultiplier = 0.97 ;
        symetricalLearning = false;
        learningRate = 0.05;
        learningRateMultiplier = 1;
        nEpochs = 4;
        rewardIntensity = 1;
        punishmentIntensity = -1;
    }
    private double calculateScore(){
        if (scoreMethod == 0) {
            return neuralNetwork.predict(calculateState())[0];
        }
        return invertDistanceSumHeuristic(controlledAgent, false, 0);
    }
    @Override
    public double simulateMove(Agent agent, Position position, double dx, double dy) {
        agent.setPos(position);
        agent.move(dx,dy);
        return calculateScore();
    }
    public void decide(Agent agent){
        final Random random = new Random();
        if (random.nextFloat() < epsilon) {
            scoreMethod = 1;
            //goToBestPosition(agent);
            agent.setOrderX(random.nextInt(3)-1);
            agent.setOrderY(random.nextInt(3)-1);
        } else {
            scoreMethod = 0;
            goToBestPosition(agent);
        }
    }
    @Override
    public void learn(double baseReward){
        int size = states.size();
        int firstState = 0;
        int lastState = Math.min(size-1, maxHistoryDepth);
        double[] statesFeatures = calculateFeatures(firstState, lastState);
        int featuresSize = lastState-firstState+1;
        int variationsNumber = (symetricalLearning ? 4 : 1);
        double[] rewards = new double[variationsNumber*featuresSize];
        for (int j = 0; j < variationsNumber; j++) {
            double reward = baseReward;
            for (int i = firstState; i < featuresSize; i++) {
                rewards[j*featuresSize+i] = reward;
                reward *= gamma;
            }
        }
        neuralNetwork.fit(statesFeatures, rewards, variationsNumber*featuresSize, nEpochs);
        epsilon*=epsilonMultiplier;
        learningRate *= learningRateMultiplier;
        neuralNetwork.setLearningRate(learningRate);
    }
}
