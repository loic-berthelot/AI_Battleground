package strategy;
import game.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

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
    private ArrayList<double[]> states;
    private Agent controlledAgent;
    private int scoreMethod;
    public NNStrategy(Game game, Agent controlledAgent){
        super(game);
        this.controlledAgent = controlledAgent;
        numInputs = 2*(game.getAgentsNumber()+game.getTeamsNumber())+6;
        numOutputs = 1;
        epsilon = 0.8;
        epsilonMultiplier = 0.95;
        gamma = 0.7;
        maxHistoryDepth = 10;
        learningRate = 0.01;
        learningRateMultiplier = 0.99;
        nEpochs = 5;
        rewardIntensity = 20;
        punishmentIntensity = -1;
        intermediateLearn = false;
        states = new ArrayList<double[]>();
        scoreMethod = 0;
        nn = new NNdl4j(learningRate,0, numInputs, numOutputs);
    }
    private double calculateScore(){
        if (scoreMethod == 0) return nn.predict(calculateState());
        return invertDistanceSumHeuristic(controlledAgent, false, 0);
    }
    @Override
    public double simulateMove(Agent agent, double posX, double posY, double dx, double dy) {
        agent.setPos(posX, posY);
        agent.move(dx,dy);
        return calculateScore();
    }
    public void decide(Agent agent){
        Random random = new Random();
        if (random.nextFloat() < epsilon) {
            scoreMethod = 1;
            goToBestPosition(agent);
            /*
            agent.setOrderX(random.nextInt(3)-1);
            agent.setOrderY(random.nextInt(3)-1);
            */
        } else {
            scoreMethod = 0;
            goToBestPosition(agent);
        }
    }
    @Override
    public void learn(double reward){
        int size = states.size();
        int firstState = Math.max(size-maxHistoryDepth, 0);
        int lastState = size-1;
        double[] statesFeatures = calculateFeatures(firstState, lastState);
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
    public double[] calculateState(){
        int agentsSize = game.getAgentsNumber();
        int kpSize = game.getKillingPointsNumber();
        Vector<Agent> agents = game.getAgents();
        Vector<KillingPoint> killingPoints = game.getKillingPoints();
        double[] state = new double[numInputs];
        int indexState = 0;
        for (int i = 0; i < agentsSize; i++) {
            Agent agent = agents.get(i);
            state[indexState] = agent.getPosX();
            state[indexState+1] = agent.getPosY();
            indexState += 2;
        }
        double distDanger = 2;
        double distPrey = 2;
        double dist;
        for (int i = 0; i < kpSize; i++) {
            KillingPoint kp = killingPoints.get(i);
            state[indexState] = kp.getPosX();
            state[indexState+1] = kp.getPosY();
            indexState += 2;
            if (kp.getTeam() == controlledAgent.getTeam()) {
                for (Agent enemy : agents) {
                    dist = distance(kp.getPosX(), kp.getPosY(), enemy.getPosX(), enemy.getPosY());
                    if (dist < distDanger) {
                        distDanger = dist;
                    }
                }
            } else {
                dist = distance(kp.getPosX(), kp.getPosY(), controlledAgent.getPosX(), controlledAgent.getPosY());
                if (dist < distPrey) {
                    distPrey = dist;
                }
            }
        }
        distDanger = Math.max(distDanger-Agent.getAgentRadius(), 0.001);
        distPrey = Math.max(distPrey-Agent.getAgentRadius(), 0.001);
        state[indexState] = distDanger;
        state[indexState+1] = distPrey;
        state[indexState+2] = invertDistanceSumHeuristic(controlledAgent, false,0);
        Random random = new Random();
        state[indexState+3] = random.nextDouble(2)-1;
        state[indexState+4] = 0.01*game.getFrameCount();
        state[indexState+5] = 0.01*(game.getFrameCount()%200);
        return state;
    }
    @Override
    public void recordState(){
        states.add(calculateState());
    }
    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
    }
    public double[] calculateFeatures(int firstState, int lastState){
        int indexFeatures = 0;
        double[] state;
        double[] features = new double[(lastState-firstState+1)*numInputs];
        for (int i = firstState; i<=lastState; i++) {
            state = states.get(i);
            for (int j = 0; j < numInputs; j++) {
                features[indexFeatures] = state[j];
                indexFeatures++;
            }
        }
        return features;
    }
    @Override
    public void discardStates(){
        states = new ArrayList<double[]>();
    }
}
