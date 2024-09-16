package strategy;

import game.Agent;
import game.Game;
import game.KillingPoint;

import java.util.Vector;

public abstract class NNStrategy extends Strategy {
    protected NNdl4j neuralNetwork;
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
    protected Vector<double[]> states;
    public NNStrategy(Game game, Agent controlledAgent) {
        super(game);
        numInputs = 2*(1+game.getKillingPointsNumber())+6;
        this.controlledAgent = controlledAgent;
        states = new Vector<double[]>();
    }
    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
    }
    public double invertValue(double val) {
        return 0.01/Math.max(val, 0.01);
    }
    public double[] calculateState(){
        Vector<Agent> agents = game.getAgents();
        Vector<KillingPoint> killingPoints = game.getKillingPoints();
        double[] state = new double[numInputs];
        int indexState = 0;
        double distDanger = 2;
        double distPrey = 2;
        double distAlly = 2;
        double dist = 0;

        state[indexState] = controlledAgent.getPosX();
        state[indexState+1] = controlledAgent.getPosY();
        indexState += 2;
        for (Agent agent : agents) {/*
            state[indexState] = agent.getPosX();
            state[indexState+1] = agent.getPosY();
            indexState += 2;*/
            if(agent != controlledAgent && agent.getTeam() == controlledAgent.getTeam()){
                dist = distance(agent.getPosX(), agent.getPosY(), controlledAgent.getPosX(), controlledAgent.getPosY());
                if (dist < distAlly) {
                    distAlly = dist;
                }
            }
        }
        for (KillingPoint kp : killingPoints) {
            state[indexState] = kp.getPosX();
            state[indexState+1] = kp.getPosY();
            indexState += 2;
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
        distDanger = Math.max(distDanger-Agent.getAgentRadius(), 0.001);
        distPrey = Math.max(distPrey-Agent.getAgentRadius(), 0.001);
        distAlly = Math.max(distAlly-Agent.getAgentRadius(), 0.001);
        state[indexState] = game.getArena().hasCorners() ? 1 : 0;
        state[indexState+1] = game.getArena().getInternalRadius();
        state[indexState+2] = invertValue(distDanger);
        state[indexState+3] = invertValue(distPrey);
        state[indexState+4] = invertValue(distAlly);
        state[indexState+5] = (game.getFrameLimit() - game.getFrameCount())/(double)game.getFrameLimit();
        return state;
    }

    public double[] calculateFeatures(int firstState, int lastState){
        int indexFeatures = 0;
        double[] state;
        double[] features = new double[(lastState-firstState+1)*numInputs];
        for (int i = firstState; i <= lastState; i++) {
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
        states.add(0, calculateState());
    }
    @Override
    public void discardStates(){
        states = new Vector<>();
    }
}
