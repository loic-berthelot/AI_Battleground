package strategy;
import game.*;

import java.util.Random;

import game.Game;

public class NNStrategy extends Strategy {
    private NNdl4j nn;
    private int numInputs;
    private  int numOutputs;
    private double epsilon;
    private double epsilonMultiplier;
    private double gamma;
    private int maxHistoryDepth;
    private double learningRate;
    private double learningRateMultiplier;
    private int nEpochs;
    private double rewardIntensity;
    private double punishmentIntensity;
    private boolean intermediateLearn;
    public NNStrategy(Game game){
        super(game);
        numInputs = 2*(game.getAgentsNumber()+game.getKillingPointsNumber())+4;
        numOutputs = 1;
        epsilon = 0.8;
        epsilonMultiplier = 0.95;
        gamma = 0.9;
        maxHistoryDepth = 20;
        learningRate = 0.0001;
        learningRateMultiplier = 1;
        nEpochs = 5;
        rewardIntensity = 1;
        punishmentIntensity = -1;
        intermediateLearn = false;
        nn = new NNdl4j(learningRate,0, numInputs, numOutputs);
    }
    private double simulateMove(double features[], Agent agent, double posX, double posY, double dx, double dy) {
        agent.setPos(posX, posY);
        agent.move(dx,dy);
        double newPosX = agent.getPosX();
        double newPosY = agent.getPosY();
        features[0] = newPosX;
        features[1] = newPosY;
        features[10] = 0.5*(newPosX+features[2]);
        features[11] = 0.5*(newPosY+features[3]);
        features[12] = GameHistory.distance(features[0], features[1], features[10], features[11]);
        features[13] = GameHistory.distance(features[2], features[3], features[10], features[11]);
        features[14] = GameHistory.distance(features[4], features[5], features[8], features[9]);
        features[15] = GameHistory.distance(features[6], features[7], features[8], features[9]);
        return nn.predict(features);
    }
    public void decide(Agent agent){
        Random random = new Random();
        if (random.nextFloat() < epsilon) {
            agent.setOrderX(random.nextInt(3)-1);
            agent.setOrderY(random.nextInt(3)-1);
        } else {
            double posX = agent.getPosX();
            double posY = agent.getPosY();
            double[] features = game.calculateLastFeatures(agent);
            double shift = 5;
            agent.setOrderX(0);
            agent.setOrderY(0);
            double bestScore = simulateMove(features, agent, posX, posY, 0, 0);
            double score;
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (i != 0 || j != 0) {
                        score = simulateMove(features, agent, posX, posY, i*shift, j*shift);
                        if (score > bestScore) {
                            bestScore = score;
                            agent.setOrderX(i);
                            agent.setOrderY(j);
                        }
                    }
                }
            }
            agent.setPos(posX, posY);
        }
    }

    @Override
    public void learn(GameHistory gameHistory, double reward, int agentIndex){
        int size = gameHistory.getSize();
        int firstState = Math.max(size-maxHistoryDepth, 0);
        int lastState = size-1;
        double[] statesFeatures = gameHistory.calculateFeatures(firstState, lastState, agentIndex);
        int featuresSize = lastState-firstState+1;
        double[] rewards = new double[featuresSize];
        for (int i = firstState+1; i <= size; i++) {
            rewards[size-i] = reward;
            reward *= gamma;
        }
        nn.fit(statesFeatures, rewards, featuresSize, nEpochs);
        epsilon*=epsilonMultiplier;
        learningRate *= learningRateMultiplier;
        nn.setLearningRate(learningRate);
    }
    public double getEpsilon(){
        return epsilon;
    }
    @Override
    public double getRewardIntensity() {
        return rewardIntensity;
    }
    @Override
    public double getPunishmentIntensity() {
        return punishmentIntensity;
    }
    @Override
    public boolean getIntermediateLearn(){
        return intermediateLearn;
    }
}
