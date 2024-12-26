package strategy;

import game.Agent;
import game.Game;
import game.KillingPoint;
import game.Position;
import strategy.neuralNetwork.MLP;

import java.util.Vector;

public abstract class NNStrategy extends Strategy {
    protected MLP neuralNetwork;
    protected Agent controlledAgent;
    protected int maxHistoryDepth;
    protected boolean intermediateLearn;
    public NNStrategy(Game game, Agent controlledAgent) {
        super(game);
        numInputs = 4*(game.getAgentsNumber()+game.getKillingPointsNumber())+1;
        this.controlledAgent = controlledAgent;
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
            state[indexState+1] = agent.getPosX()*agent.getPosX();
            state[indexState+2] = agent.getPosY();
            state[indexState+3] = agent.getPosY()*agent.getPosY();
            indexState+=4;
        }
        for (KillingPoint kp : killingPoints){
            state[indexState] = kp.getPosX();
            state[indexState+1] = kp.getPosX()*kp.getPosX();
            state[indexState+2] = kp.getPosY();
            state[indexState+3] = kp.getPosY()*kp.getPosY();
            indexState+=4;
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
    public double getEpsilon(){
        return epsilon;
    }
    @Override
    public double getMaxOutInterval() {
        return neuralNetwork.getMaxOutInterval();
    }
    @Override
    public double getMinOutInterval() {
        return neuralNetwork.getMinOutInterval();
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
