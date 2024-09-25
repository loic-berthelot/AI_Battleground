package strategy;

import game.Agent;
import game.Game;
import game.KillingPoint;
import game.Position;

import java.util.ArrayList;
import java.util.Random;
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
    protected double rewardIntensity;
    protected double punishmentIntensity;
    protected boolean intermediateLearn;
    protected Vector<double[]> states;
    protected ArrayList<LearningBatch> learningHistory;
    protected int learningHistoryDepth;
    public NNStrategy(Game game, Agent controlledAgent) {
        super(game);
        numInputs = 2*(game.getAgentsNumber()+game.getKillingPointsNumber())+1;
        this.controlledAgent = controlledAgent;
        states = new Vector<double[]>();
        learningHistory = new ArrayList<>();
        learningHistoryDepth = 10;
    }
    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
    }
    public static double distance(Position p1, Position p2) {
        return distance(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }
    public double invertValue(double val) {
        return 0.01/Math.max(val, 0.01);
    }

    public double[] calculateState(){
        double[] state = new double[numInputs];
        int indexState = 0;
        final Vector<Agent> agents = game.getAgents();
        final Vector<KillingPoint> killingPoints = game.getKillingPoints();
        for (Agent agent : agents){
            state[indexState] = agent.getPosX();
            state[indexState+1] = agent.getPosY();
            indexState+=2;
        }
        for (KillingPoint kp : killingPoints){
            state[indexState] = kp.getPosX();
            state[indexState+1] = kp.getPosY();
            indexState+=2;
        }
        state[indexState] = (game.getFrameLimit() - game.getFrameCount())/(double)game.getFrameLimit();
        return state;
    }
    public double[] invertState(double[] state, double xInversion, double yInversion) {
        double[] result = new double[state.length];
        for (int i = 0; i < 2*(game.getAgentsNumber()+game.getKillingPointsNumber()); i++) {
            result[i] = state[i] * (i%2==0 ? xInversion : yInversion);
        }
        for(int i = 2*(1+game.getKillingPointsNumber()); i < state.length; i++){
            result[i] = state[i];
        }
        return result;
    }
    public double[] calculateFeatures(){
        int indexFeatures = 0;
        double[] state;
        double[] features = new double[states.size() * numInputs];
        for (int i = 0; i < states.size(); i++) {
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
        if (states.size() > maxHistoryDepth) states.remove(states.lastElement());
    }
    @Override
    public void discardStates(){
        states = new Vector<>();
    }
}
