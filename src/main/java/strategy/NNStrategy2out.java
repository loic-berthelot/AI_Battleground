package strategy;

import game.Agent;
import game.Game;
import game.Position;
import strategy.neuralNetwork.MLP;

import java.util.Random;
import java.util.Vector;

public class NNStrategy2out extends NNStrategy {
    final private Vector<double[]> choices;
    private int scoreMethod;
    private int recordingInterval;
    private double movementThreshold;
    private final int variation;
    private double targetX;
    private double targetY;
    public NNStrategy2out(Game game, Agent controlledAgent) {
        super(game, controlledAgent);
        numOutputs = 2;
        epsilon = 0.8;
        epsilonMultiplier = 0.99;
        gamma = 0.97;
        maxHistoryDepth = 3;
        learningRate = 0.001;
        learningRateMultiplier = 1;
        recordingInterval = 25;
        intermediateLearn = false;
        movementThreshold = 0.01;
        choices = new Vector<double[]>();
        scoreMethod = 0;
        variation = 1;
        learningHistoryDepth = 40;
        Random random = new Random();
        neuralNetwork = new MLP(learningRate, random.nextInt(10000), numInputs, numOutputs);
    }

    @Override
    public double simulateMove(Agent agent, Position position, double dx, double dy) {
        agent.setPos(position);
        agent.move(dx,dy);
        return dangerPreyHeuristic(agent, 2.5);
    }

    public void decide(Agent agent) {
        Random random = new Random();
        if (random.nextFloat() < epsilon) {
            scoreMethod = 1;
            agent.setOrderX(random.nextInt(3)-1);
            agent.setOrderY(random.nextInt(3)-1);
            //goToBestPosition(agent);
        } else {
            scoreMethod = 0;
            double[] outputs = neuralNetwork.predict(calculateState());
            double dx = neuralNetwork.outToArenaInterval(outputs[0]);
            double dy = neuralNetwork.outToArenaInterval(outputs[1]);
            targetX = dx;
            targetY = dy;
            if (variation == 1) {
                if (dx > movementThreshold) agent.setOrderX(1);
                else if (dx < -movementThreshold) agent.setOrderX(-1);
                else agent.setOrderX(0);
                if (dy > movementThreshold) agent.setOrderY(1);
                else if (dy < -movementThreshold) agent.setOrderY(-1);
                else agent.setOrderY(0);
            } else if (variation == 2) {
                if (dx > agent.getPosX() + movementThreshold) agent.setOrderX(1);
                else if (dx < agent.getPosX() - movementThreshold) agent.setOrderX(-1);
                else agent.setOrderX(0);
                if (dy > agent.getPosY() + movementThreshold) agent.setOrderY(1);
                else if (dy < agent.getPosY() - movementThreshold) agent.setOrderY(-1);
                else agent.setOrderY(0);
            }
        }
    }

    @Override
    public void learn(boolean victory, int epochsNumber) {
        int size = states.size();
        double[] statesFeatures = calculateFeatures();
        double[] rewards = new double[size * 2];
        double multiplier = victory ? getMaxOutInterval() : getMinOutInterval();
        for (int i = 0; i < size; i++) {
            double[] choice = choices.get(i);
            if (variation == 1) {
                rewards[2 * i] = neuralNetwork.arenaToOutInterval(multiplier * choice[0]);
                rewards[2 * i + 1] = neuralNetwork.arenaToOutInterval(multiplier * choice[1]);
            } else if (variation == 2) {
                rewards[2 * i] = neuralNetwork.arenaToOutInterval(multiplier * (choice[0] - choice[2]) + choice[2]);
                rewards[2 * i + 1] = neuralNetwork.arenaToOutInterval(multiplier * (choice[1] - choice[3]) + choice[3]);
            }
            multiplier *= gamma;
        }
        learningHistory.add(new LearningBatch(statesFeatures, rewards, size, epochsNumber));
        if (learningHistory.size() >= learningHistoryDepth) {
            for(LearningBatch batch : learningHistory){
                neuralNetwork.fit(batch);
            }
            learningHistory.clear();
        }
        epsilon *= epsilonMultiplier;
        double winProportion = game.getGameHistory().getWinProportion(controlledAgent.getTeam());
        //epsilon = 1*(1-winProportion)*(1-winProportion)*(1-winProportion);
        learningRate *= learningRateMultiplier;
        neuralNetwork.setLearningRate(learningRate);
    }

    public double[] calculateChoice() {
        double[] choice= new double[4];
        if (variation == 1) {
            choice[0] = controlledAgent.getLastMovesX(recordingInterval);
            choice[1] = controlledAgent.getLastMovesY(recordingInterval);
        } else if (variation == 2) {
            //choice[0] = controlledAgent.getPosX();
            //choice[1] = controlledAgent.getPosY();
            choice[0] = targetX;
            choice[1] = targetY;
            choice[2] = controlledAgent.getPosX();
            choice[3] = controlledAgent.getPosY();
        }
        //choice[0] = 0.5*(choice[0]+1);
        //choice[1] = 0.5*(choice[1]+1);
        return choice;
    }

    @Override
    public void recordState() {
        super.recordState();
        choices.add(0, calculateChoice());
        if (choices.size() > maxHistoryDepth) choices.remove(choices.lastElement());
    }
}

