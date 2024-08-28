package game;

import java.util.ArrayList;

public class GameHistory {
    ArrayList<GameState> states;
    private Game game;
    public GameHistory(Game game){
        this.game = game;
        states = new ArrayList<GameState>();
    }
    public int getSize(){
        return states.size();
    }
    public ArrayList<GameState> getStates(){
        return states;
    }
    public void addState(GameState state) {
        states.add(state);
    }
    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
    }
    public double[] calculateFeatures(int firstState, int lastState, int agentIndex){
        int indexFeatures = 0;
        GameState state = states.get(firstState);
        int agentsNumber = state.getAgentsNumber();
        int kpNumber = state.getKillingPointsNumber();
        double[] features = new double[(lastState-firstState+1)*((agentsNumber+kpNumber)*2+4)];
        for (int j = firstState; j<=lastState; j++) {
            state = states.get(j);
            Agent agent = game.getAgent(agentIndex);
            int team = agent.getTeam();
            double[] agentsPosX = state.getAgentsPosX();
            double[] agentsPosY = state.getAgentsPosY();
            double[] agentsTeams = state.getAgentsTeams();
            double[] kpPosX = state.getKillingPointPosX();
            double[] kpPosY = state.getKillingPointPosY();
            double[] kpTeams = state.getKillingPointTeams();
            features[indexFeatures] = agentsPosX[agentIndex];
            features[indexFeatures+1] = agentsPosY[agentIndex];
            indexFeatures+=2;
            for (int i = 0; i < agentsNumber; i++) {
                if (agentsTeams[i] == team && i != agentIndex) {
                    features[indexFeatures] = agentsPosX[i];
                    features[indexFeatures+1] = agentsPosY[i];
                    indexFeatures+=2;
                }
            }
            for (int i = 0; i < agentsNumber; i++) {
                if (agentsTeams[i] != team) {
                    features[indexFeatures] = agentsPosX[i];
                    features[indexFeatures+1] = agentsPosY[i];
                    indexFeatures+=2;
                }
            }
            for (int i = 0; i < kpNumber; i++) {
                if (kpTeams[i] == team) {
                    features[indexFeatures] = kpPosX[i];
                    features[indexFeatures+1] = kpPosY[i];
                    indexFeatures+=2;
                }
            }
            for (int i = 0; i < kpNumber; i++) {
                if (kpTeams[i] != team) {
                    features[indexFeatures] = kpPosX[i];
                    features[indexFeatures+1] = kpPosY[i];
                    indexFeatures+=2;
                }
            }
            features[indexFeatures] = distance(features[0], features[1], features[10], features[11]);
            features[indexFeatures+1] = distance(features[2], features[3], features[10], features[11]);
            features[indexFeatures+2] = distance(features[4], features[5], features[8], features[9]);
            features[indexFeatures+3] = distance(features[6], features[7], features[8], features[9]);
            indexFeatures+=4;
        }
        return features;
    }
    public GameState getState(int stateIndex) {
        return states.get(stateIndex);
    }
    public GameState getLastState() {
        return states.get(states.size()-1);
    }
}
