package strategy;
import game.*;

import java.util.Random;
import java.util.Vector;

import game.Game;

public class NNStrategy1out extends NNStrategy {
    private int scoreMethod;
    public NNStrategy1out(Game game, Agent controlledAgent){
        super(game, controlledAgent);
        this.controlledAgent = controlledAgent;
        numOutputs = 1;
        numInputs = (game.getAgentsNumber()+game.getKillingPointsNumber())+5;
        scoreMethod = 0;
        intermediateLearn = false;
        Random random = new Random();

        gamma = 0.95;
        maxHistoryDepth = 10;

        epsilon = 0.8;
        epsilonMultiplier = 0.95;
        learningRate = 0.0002;
        learningRateMultiplier = 1;
        rewardIntensity = 1;
        punishmentIntensity = 0;
        learningHistoryDepth = 10;
        neuralNetwork = new NNdl4j(learningRate,random.nextInt(10000), numInputs, numOutputs);
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
        if (random.nextDouble() <= epsilon) {
            //scoreMethod = 1;
            //goToBestPosition(agent);
            agent.setOrderX(random.nextInt(3)-1);
            agent.setOrderY(random.nextInt(3)-1);
        } else {
            scoreMethod = 0;
            goToBestPosition(agent);
        }
    }
    @Override
    public void learn(boolean victory, int epochsNumber){
        int size = states.size();
        double[] statesFeatures = calculateFeatures();
        double[] rewards = new double[size];
        double reward = victory ? rewardIntensity : punishmentIntensity;
        for (int i = 0; i < size; i++) {
            rewards[i] = reward;
            reward = gamma*(reward-0.5)+0.5;
        }
        learningHistory.add(new LearningBatch(statesFeatures, rewards, size, epochsNumber));
        if (learningHistory.size() >= learningHistoryDepth) {
            for(LearningBatch batch : learningHistory){
                neuralNetwork.fit(batch);
            }
            learningHistory.clear();
        }
        epsilon*=epsilonMultiplier;
        if (learningRateMultiplier != 1) {
            learningRate = learningRateMultiplier*(learningRate-0.5)+0.5;
            neuralNetwork.setLearningRate(learningRate);
        }
    }

    @Override
    public double[] calculateState(){
        double[] state = new double[numInputs];
        Vector<Agent> agents = game.getAgents();
        Vector<KillingPoint> killingPoints = game.getKillingPoints();
        int indexState = 0;
        double distDanger = 2;
        double distPrey = 2;
        double distAlly = 2;
        double distEnemy = 2;
        double distCenter = 2;
        double distBorder = 2;
        double dist = 0;
        for (Agent agent : agents) {
            if (agent != controlledAgent) {
                state[indexState++] = distance(agent.getPosition(), controlledAgent.getPosition())-2*Agent.getAgentRadius();
                //state[indexState++] = 1-agent.getPosition().distanceToCenter()-Agent.getAgentRadius();
                dist = distance(agent.getPosition(), controlledAgent.getPosition());
                if(agent.getTeam() == controlledAgent.getTeam() && dist < distAlly) {
                    distAlly = dist;
                } else if (agent.getTeam() != controlledAgent.getTeam() && dist < distEnemy) {
                    distEnemy = dist;
                }
            }
        }
        for (KillingPoint kp : killingPoints) {
            state[indexState++] = distance(kp.getPosition(), controlledAgent.getPosition())-Agent.getAgentRadius();
            //state[indexState++] = 1-kp.getPosition().distanceToCenter();
            if (kp.getTeam() == controlledAgent.getTeam()) {
                for (Agent enemy : agents) {
                    if (enemy.getTeam() != controlledAgent.getTeam()) {
                        dist = distance(kp.getPosX(), kp.getPosY(), enemy.getPosX(), enemy.getPosY());
                        if (dist < distPrey) {
                            distPrey = dist;
                        }
                    }
                }
            } else {
                for (Agent ally : agents) {
                    if (ally.getTeam() == controlledAgent.getTeam()) {
                        dist = distance(kp.getPosX(), kp.getPosY(), ally.getPosX(), ally.getPosY());
                        if (dist < distDanger) {
                            distDanger = dist;
                        }
                    }
                }
            }
        }
        distCenter = controlledAgent.getPosition().distanceToCenter();
        distBorder = 1-distCenter;
        distDanger = Math.max(Math.pow(distDanger-Agent.getAgentRadius(), -1), 0.001);
        distPrey = Math.max(Math.pow(distPrey-Agent.getAgentRadius(), -1), 0.001);
        distAlly = Math.max(distAlly-2*Agent.getAgentRadius(), 0.001);
        distEnemy = Math.max(distEnemy-2*Agent.getAgentRadius(), 0.001);
        distCenter = Math.max(distCenter, 0.001);
        distBorder = Math.max(distBorder-Agent.getAgentRadius(), 0.001);
        state[indexState++] = (game.getFrameLimit() - game.getFrameCount())/(double)game.getFrameLimit();
        //state[indexState++] = game.getArena().hasCorners() ? 1 : 0;
        //state[indexState++] = game.getArena().getInternalRadius();
        state[indexState++] = distDanger;
        state[indexState++] = distPrey;
        state[indexState++] = distAlly;
        state[indexState++] = distEnemy;
        state[indexState++] = distBorder;
        /*
        state[indexState++] = distDanger*distDanger;
        state[indexState++] = distPrey*distPrey;
        state[indexState++] = distAlly*distAlly;
        */
        return state;
    }
}
