package strategy;

import game.Agent;
import game.Game;
import game.KillingPoint;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

public abstract class NNStrategy extends Strategy {
    protected NNdl4j nn;
    protected int numInputs;
    protected int numOutputs;
    protected Agent controlledAgent;
    protected double epsilon;
    protected double epsilonMultiplier;
    protected double gamma;
    protected int maxHistoryDepth;
    protected double learningRate;
    protected double learningRateMultiplier;
    protected int nEpochs;
    protected double rewardIntensity;
    protected double punishmentIntensity;
    protected boolean intermediateLearn;
    protected ArrayList<double[]> states;
    public NNStrategy(Game game, Agent controlledAgent) {
        super(game);
        this.controlledAgent = controlledAgent;
    }
    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
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
    @Override
    public void recordState(){
        states.add(calculateState());
    }
    @Override
    public void discardStates(){
        states = new ArrayList<double[]>();
    }
}
