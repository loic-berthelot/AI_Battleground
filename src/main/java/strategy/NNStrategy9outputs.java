package strategy;

import game.Agent;
import game.Game;

import java.util.Random;
import java.util.Vector;

public class NNStrategy9outputs extends NNStrategy {
    final private Vector<Integer> choices;
    private int scoreMethod;
    private int recordingInterval;
    public NNStrategy9outputs(Game game, Agent controlledAgent) {
        super(game, controlledAgent);
        numInputs = 2 * (game.getAgentsNumber() + game.getTeamsNumber()) + 6;
        numOutputs = 9;
        epsilon = 0.8;
        epsilonMultiplier = 0.95;
        gamma = 0.7;
        maxHistoryDepth = 30;
        learningRate = 0.05;
        learningRateMultiplier = 0.99;
        rewardIntensity = 1;
        punishmentIntensity = -0.1;
        recordingInterval = game.getRecordingDelta();
        intermediateLearn = false;
        choices = new Vector<Integer>();
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
            int maxIndex = 4;
            double maxScore = outputs[4];
            double score;
            for (int i = 0; i < 9; i++) {
                score = outputs[i];
                if (score > maxScore) {
                    maxScore = score;
                    maxIndex = i;
                }
            }
            agent.setOrderX((maxIndex%3)-1);
            agent.setOrderY((int) (maxIndex/3)-1);
        }
    }

    @Override
    public void learn(double reward, int nEpochs) {
        int size = states.size();
        int firstState = 0;
        int lastState = Math.min(size - 1, maxHistoryDepth);
        double[] statesFeatures = calculateFeatures(firstState, lastState);
        int featuresSize = lastState + 1;
        double[] rewards = new double[featuresSize*9];
        for (int i = firstState; i < featuresSize; i++) {
            int choice = choices.get(i);
            rewards[9*i+choice] = reward;
        }
        neuralNetwork.fit(statesFeatures, rewards, featuresSize, nEpochs);
        epsilon *= epsilonMultiplier;
        learningRate *= learningRateMultiplier;
        neuralNetwork.setLearningRate(learningRate);
    }

    public int calculateChoice() {
        double dx = controlledAgent.getLastMovesX(recordingInterval);
        double dy = controlledAgent.getLastMovesY(recordingInterval);
        int idx = 1;
        int idy = 1;
        double threshold = 2*game.getSpeed();
        if (dx < -threshold) idx = 0;
        else if (dx > threshold) idx = 2;
        if (dy < -threshold) idy = 0;
        else if (dy > threshold) idy = 2;
        return 3*idy+idx;
    }

    @Override
    public void recordState() {
        states.add(0, calculateState());
        choices.add(0, calculateChoice());
    }
}