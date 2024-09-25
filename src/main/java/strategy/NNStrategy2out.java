package strategy;

import game.Agent;
import game.Game;

import java.util.Random;
import java.util.Vector;

public class NNStrategy2out extends NNStrategy {
    final private Vector<double[]> choices;
    private int scoreMethod;
    private int recordingInterval;
    public NNStrategy2out(Game game, Agent controlledAgent) {
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
        choices = new Vector<double[]>();
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
    public void learn(double reward, int epochsNumber) {
        int size = states.size();
        double[] statesFeatures = calculateFeatures();
        double[] rewards = new double[size * 2];
        for (int i = 0; i < size; i++) {
            double[] choice = choices.get(i);
            rewards[2 * i] = reward * choice[0];
            rewards[2 * i + 1] = reward * choice[1];
            reward *= gamma;
        }
        learningHistory.add(new LearningBatch(statesFeatures, rewards, size, epochsNumber));
        if (learningHistory.size() > learningHistoryDepth) {
            for(LearningBatch batch : learningHistory){
                neuralNetwork.fit(batch);
            }
            learningHistory.clear();
        }
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
        super.recordState();
        choices.add(0, calculateChoice());
        if (choices.size() > maxHistoryDepth) choices.remove(choices.lastElement());
    }
}