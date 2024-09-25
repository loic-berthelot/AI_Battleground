package strategy;

import game.Agent;
import game.Game;

import java.util.ArrayList;
import java.util.Random;

public class NNStrategy2outputs extends NNStrategy {
    final private ArrayList<double[]> choices;
    private int scoreMethod;
    private int recordingInterval;
    public NNStrategy2outputs(Game game, Agent controlledAgent) {
        super(game, controlledAgent);
        numOutputs = 2;
        epsilon = 0.8;
        epsilonMultiplier = 0.95;
        gamma = 0.7;
        maxHistoryDepth = 50;
        learningRate = 0.001;
        learningRateMultiplier = 0.99;
        rewardIntensity = 1;
        punishmentIntensity = -0.3;
        recordingInterval = 15;
        intermediateLearn = false;
        choices = new ArrayList<double[]>();
        scoreMethod = 0;
        Random random = new Random();
        neuralNetwork = new NNdl4j(learningRate, random.nextInt(10000), numInputs, numOutputs);
    }
    public void decide(Agent agent) {
        Random random = new Random();
        if (random.nextFloat() < epsilon) {
            scoreMethod = 1;
            agent.setOrderX(random.nextInt(3)-1);
            agent.setOrderY(random.nextInt(3)-1);
        } else {
            scoreMethod = 0;
            double[] outputs = neuralNetwork.predict(calculateState());
            double dx = outputs[0];
            double dy = outputs[1];
            double threshold =  2*game.getSpeed();
            if (dx > threshold) agent.setOrderX(1);
            else if (dx < -threshold) agent.setOrderX(-1);
            if (dy > threshold) agent.setOrderY(1);
            else if (dy < -threshold) agent.setOrderY(-1);
        }
    }

    @Override
    public void learn(double reward, int nEpochs) {
        int size = states.size();
        int firstState = 0;
        int lastState = Math.min(states.size() - 1, maxHistoryDepth);
        double[] statesFeatures = calculateFeatures(firstState, lastState);
        int featuresSize = lastState+ 1;
        double[] rewards = new double[featuresSize * 2];
        for (int i = firstState; i < featuresSize; i++) {
            double[] choice = choices.get(i);
            rewards[2 * i] = reward * choice[0];
            rewards[2 * i + 1] = reward * choice[1];
            reward *= gamma;
        }
        neuralNetwork.fit(statesFeatures, rewards, featuresSize, nEpochs);
        epsilon *= epsilonMultiplier;
        learningRate *= learningRateMultiplier;
        neuralNetwork.setLearningRate(learningRate);
    }

    public double[] calculateChoice() {
        double[] choice = new double[2];
        choice[0] = controlledAgent.getLastMovesX(recordingInterval);
        choice[1] = controlledAgent.getLastMovesY(recordingInterval);
        return choice;
    }

    @Override
    public void recordState() {
        states.add(calculateState());
        choices.add(calculateChoice());
    }
}