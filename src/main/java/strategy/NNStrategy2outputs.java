package strategy;

import game.Agent;
import game.Game;
import game.KillingPoint;
import game.Position;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

public class NNStrategy2outputs extends NNStrategy {
    final private ArrayList<double[]> choices;
    private int scoreMethod;
    public NNStrategy2outputs(Game game, Agent controlledAgent) {
        super(game, controlledAgent);
        numInputs = 2 * (game.getAgentsNumber() + game.getTeamsNumber()) + 6;
        numOutputs = 2;
        epsilon = 0.8;
        epsilonMultiplier = 0.95;
        gamma = 1;
        maxHistoryDepth = 50;
        learningRate = 0.001;
        learningRateMultiplier = 0.99;
        nEpochs = 50;
        rewardIntensity = 1;
        punishmentIntensity = -0.3;
        intermediateLearn = false;
        states = new ArrayList<double[]>();
        choices = new ArrayList<double[]>();
        scoreMethod = 0;
        Random random = new Random();
        nn = new NNdl4j(learningRate, random.nextInt(10000), numInputs, numOutputs);
    }
    public void decide(Agent agent) {
        Random random = new Random();
        if (random.nextFloat() < epsilon) {
            scoreMethod = 1;
            agent.setOrderX(random.nextInt(3)-1);
            agent.setOrderY(random.nextInt(3)-1);
        } else {
            scoreMethod = 0;
            double[] outputs = nn.predict(calculateState());
            double dx = outputs[0];
            double dy = outputs[1];
            double threshold = 0.0;
            if (dx > threshold) agent.setOrderX(1);
            else if (dx < -threshold) agent.setOrderX(-1);
            if (dy > threshold) agent.setOrderY(1);
            else if (dy < -threshold) agent.setOrderY(-1);
        }
    }

    @Override
    public void learn(double reward) {
        int size = states.size();
        int firstState = Math.max(size - maxHistoryDepth, 0);
        int lastState = size - 1;
        double[] statesFeatures = calculateFeatures(firstState, lastState);
        int featuresSize = lastState - firstState + 1;
        double[] rewards = new double[featuresSize * 2];
        for (int i = firstState + 1; i <= size; i++) {
            double[] choice = choices.get(i);
            rewards[2 * (size - i)] = reward * choice[0];
            rewards[2 * (size - i) + 1] = reward * choice[1];
            reward *= gamma;
        }
        nn.fit(statesFeatures, rewards, featuresSize, nEpochs);
        epsilon *= epsilonMultiplier;
        learningRate *= learningRateMultiplier;
        nn.setLearningRate(learningRate);
    }

    public double[] calculateChoice() {
        double[] choice = new double[2];
        choice[0] = controlledAgent.getLastMovesX(10);
        choice[1] = controlledAgent.getLastMovesY(10);
        return choice;
    }

    @Override
    public void recordState() {
        states.add(calculateState());
        choices.add(calculateChoice());
    }
}