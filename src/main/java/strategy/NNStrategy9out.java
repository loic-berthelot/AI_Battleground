package strategy;

import game.Agent;
import game.Game;
import strategy.neuralNetwork.MLP;

import java.util.Random;
import java.util.Vector;

public class NNStrategy9out extends NNStrategy {
    final private Vector<Integer> choices;
    private int scoreMethod;
    private int recordingInterval;
    final private double threshold;
    public NNStrategy9out(Game game, Agent controlledAgent) {
        super(game, controlledAgent);
        numOutputs = 9;

        epsilon = 0.0;
        epsilonMultiplier = 0.95;
        gamma = 0.9;
        maxHistoryDepth = 4;

        learningRate = 0.001;
        learningRateMultiplier = 1;
        recordingInterval = game.getRecordingDelta();
        intermediateLearn = false;
        choices = new Vector<Integer>();
        scoreMethod = 0;
        threshold = 0.01;
        learningHistoryDepth = 25;
        Random random = new Random();
        neuralNetwork = new MLP(learningRate, random.nextInt(10000), numInputs, numOutputs);
    }
    public void decide(Agent agent) {
        Random random = new Random();
        if (random.nextFloat() < epsilon) {
            //scoreMethod = 1;
            agent.setOrderX(random.nextInt(3)-1);
            agent.setOrderY(random.nextInt(3)-1);
        } else {
            scoreMethod = 0;
            double[] outputs = neuralNetwork.predict(calculateState());
            int maxIndex = 4;
            double maxScore = outputs[4];
            double score;
            String result = "";
            for (int i = 0; i < 9; i++) {
                score = outputs[i];
                result += i + " : " + score + ", ";
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
    public void learn(boolean victory, int epochsNumber) {
        int size = states.size();
        double[] statesFeatures = calculateFeatures();
        double[] rewards = new double[size*9];
        double outputChoice = victory ? getMaxOutInterval() : getMinOutInterval();
        double outputOthers = !victory ? getMaxOutInterval() : getMinOutInterval();
        for (int i = 0; i < size; i++) {
            int choice = choices.get(i);
            for (int j = 0; j < 9; j++) {
                rewards[9*i+j] = (j==choice) ? outputChoice : outputOthers;
            }
        }
        learningHistory.add(new LearningBatch(statesFeatures, rewards, size, epochsNumber));
        if (learningHistory.size() >= learningHistoryDepth) {
            for(LearningBatch batch : learningHistory){
                neuralNetwork.fit(batch);
            }
            learningHistory.clear();
        }
        //epsilon *= epsilonMultiplier;
        epsilon = 50 / (50.0 + (game.getFrameCount() %500));
        learningRate *= learningRateMultiplier;
        neuralNetwork.setLearningRate(learningRate);
    }

    public int calculateChoice() {
        double dx = controlledAgent.getLastMovesX(recordingInterval);
        double dy = controlledAgent.getLastMovesY(recordingInterval);
        int idx = 1;
        int idy = 1;
        if (dx < -threshold) idx = 0;
        else if (dx > threshold) idx = 2;
        if (dy < -threshold) idy = 0;
        else if (dy > threshold) idy = 2;
        return 3*idy+idx;
    }

    @Override
    public void recordState() {
        super.recordState();
        choices.add(0, calculateChoice());
        if (choices.size() > maxHistoryDepth) choices.remove(choices.lastElement());
    }
}