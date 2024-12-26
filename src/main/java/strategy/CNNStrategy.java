package strategy;

import game.Agent;
import game.Game;
import game.KillingPoint;
import game.Position;
import strategy.neuralNetwork.CNN;

import java.util.Vector;

public class CNNStrategy extends Strategy {
    private CNN cnndl4j;
    private final int sideSize = 16;
    public CNNStrategy(Game game) {
        super(game);
        cnndl4j = new CNN(sideSize,sideSize,5, 9, 1e-3);
    }
    @Override
    public void decide(Agent agent) {
        double[] input = calculateState(agent);
        cnndl4j.predict(input);
    }

    public int getIndexFromPosition(Position pos) {
        final int normalizedX = (int) ((sideSize*(pos.getX()+1))/2);
        final int normalizedY = (int) ((sideSize*(pos.getY()+1))/2);
        return normalizedY*sideSize+normalizedX;
    }
    public double[] calculateState(Agent agent){
        double[] features = new double[sideSize*sideSize*5];
        features[getIndexFromPosition(agent.getPosition())] = 1;
        Vector<Agent> agents = game.getAgents();
        for (Agent ag : agents) {
            if (ag != agent && ag.getTeam() == agent.getTeam()) {
                features[sideSize*sideSize+getIndexFromPosition(ag.getPosition())] = 1;
            } else if (ag.getTeam() != agent.getTeam()) {
                features[2*sideSize*sideSize+getIndexFromPosition(ag.getPosition())] = 1;
            }
        }
        Vector<KillingPoint> killingPoints = game.getKillingPoints();
        for (KillingPoint kp : killingPoints) {
            if (kp.getTeam() == agent.getTeam()) {
                features[3*sideSize*sideSize+getIndexFromPosition(kp.getPosition())] = 1;
            } else {
                features[4*sideSize*sideSize+getIndexFromPosition(kp.getPosition())] = 1;
            }
        }
        return features;
    }

    @Override
    public void learn(boolean victory, int epochsNumber){
        int size = states.size();
        double[] statesFeatures = calculateFeatures();
        double[] rewards = new double[size];
        double reward = victory ? getMaxOutInterval() : getMinOutInterval();
        for (int i = 0; i < size; i++) {
            rewards[i] = reward;
            reward = gamma*(reward-0.5)+0.5;
        }
        learningHistory.add(new LearningBatch(statesFeatures, rewards, size, epochsNumber));
        if (learningHistory.size() >= learningHistoryDepth) {
            for(LearningBatch batch : learningHistory){
                cnndl4j.fit(batch);
            }
            learningHistory.clear();
        }
        epsilon*=epsilonMultiplier;
        if (learningRateMultiplier != 1) {
            learningRate = learningRateMultiplier*(learningRate-0.5)+0.5;
            cnndl4j.setLearningRate(learningRate);
        }
    }
}
