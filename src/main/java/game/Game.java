package game;

import controller.InputBuffer;
import controller.InputManager;
import it.unimi.dsi.fastutil.ints.IntHash;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import strategy.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

public class Game {
    private Vector<Agent> agents;
    private Vector<KillingPoint> killingPoints;
    private static Vector<Color> teamColors;
    private Vector<Integer> scores;
    private int teamsNumber;
    private int teamSize;
    private boolean mustReset;
    private boolean turboMode;
    private boolean paused;
    private InputBuffer turboInputBuffer;
    private InputBuffer pausedInputBuffer;
    private InputBuffer restartInputBuffer;
    private double speed;
    private int frameCount;
    private int roundCount;
    private int roundsLimit;
    ArrayList<NNStrategy> nnStrategies;
    private GameHistory gameHistory;
    static {
        teamColors = new Vector<>();
        teamColors.add(Color.BLUE);
        teamColors.add(Color.RED);
        teamColors.add(Color.GREEN);
        teamColors.add(Color.YELLOW);
        teamColors.add(Color.AZURE);
        teamColors.add(Color.PURPLE);
        teamColors.add(Color.DARKTURQUOISE);
        teamColors.add(Color.ORANGE);
    }
    public Game() {
        roundCount = 0;
        teamsNumber = 2;
        teamSize = 2;
        speed = 0.005;
        Agent.setAgentRadius(0.08);
        Agent.setSpeed(speed);
        roundsLimit = -1;
        nnStrategies = new ArrayList<>();
        init(true);
        scores = new Vector<>();
        for (int i = 0; i < teamsNumber; i++) {
            scores.add(0);
        }
        turboMode = false;
        turboInputBuffer = new InputBuffer("Turbo");
        pausedInputBuffer = new InputBuffer("Pause");
        restartInputBuffer = new InputBuffer("Restart");
    }
    private void buildKillingPoints(){
        killingPoints = new Vector<KillingPoint>();
        Agent agent1;
        Agent agent2;
        for (int i = 0; i < agents.size(); i++) {
            agent1 = agents.get(i);
            for (int j = i+1; j < agents.size(); j++) {
                agent2 = agents.get(j);
                if (agent1.getTeam() == agent2.getTeam()) killingPoints.add(new KillingPoint(agent1, agent2));
            }
        }
    }
    public void init(boolean firstInit){
        roundCount++;
        frameCount = 0;
        if (firstInit) agents = new Vector<>();
        Random random = new Random();
        double angleShift = random.nextDouble(2*Math.PI);
        double randomTeam = 0;
        int agentIndex = 0;
        for (int i = 0; i < teamsNumber; i++) {
            for (int j = 0; j < teamSize; j++) {
                double angle = 2*Math.PI*i/teamsNumber+angleShift;
                double dist = 0.4+0.4*(j/(double)teamSize);
                if (firstInit) {
                    Agent a = new Agent(dist * Math.cos(angle), dist * Math.sin(angle), i);
                    agents.add(a);
                    if ( i == randomTeam) {
                        a.setStrategy(new RandomStrategy(this, 0.2));
                    } else {
                        NNStrategy strat = new NNStrategy(this);
                        a.setStrategy(strat);
                        nnStrategies.add(strat);
                    }
                } else {
                    Agent a = agents.get(agentIndex);
                    a.setPos(dist * Math.cos(angle), dist * Math.sin(angle));
                    if (roundCount == 10 && i == 0) a.setStrategy(new NNStrategy(this));
                }
                agentIndex++;
            }
        }
        gameHistory = new GameHistory(this);
        buildKillingPoints();
        mustReset = false;
        addState();
    }
    public void addState(){
        int agentsSize = agents.size();
        int kpSize = killingPoints.size();
        double[] agentsPosX = new double[agentsSize];
        double[] agentsPosY = new double[agentsSize];
        double[] agentsTeams = new double[agentsSize];
        double[] kpPosX = new double[kpSize];
        double[] kpPosY = new double[kpSize];
        double[] kpTeams = new double[kpSize];
        for (int i = 0; i < agentsSize; i++) {
            Agent agent = agents.get(i);
            agentsPosX[i] = agent.getPosX();
            agentsPosY[i] = agent.getPosY();
            agentsTeams[i] = agent.getTeam();
        }
        for (int i = 0; i < kpSize; i++) {
            KillingPoint kp = killingPoints.get(i);
            kpPosX[i] = kp.getPosX();
            kpPosY[i] = kp.getPosY();
            kpTeams[i] = kp.getTeam();
        }
        GameState state = new GameState(agentsSize, agentsPosX, agentsPosY, agentsTeams);
        state.setKillingPointsCoordinates(kpSize, kpPosX, kpPosY, kpTeams);
        gameHistory.addState(state);
    }
    private double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
    }
    private double inGameReward(Agent agent) {
        double reward, rewardIntensity, punishmentIntensity, dist;
        KillingPoint killingPoint;
        reward = 0;
        Strategy strat = agent.getStrategy();
        rewardIntensity = strat.getRewardIntensity();
        punishmentIntensity = strat.getPunishmentIntensity();
        for (int i = 0; i < killingPoints.size(); i++){
            killingPoint = killingPoints.get(i);
            if (killingPoint.getTeam() == agent.getTeam()) {
                for (int j = 0; j < agents.size(); j++) {
                    dist = Math.max(distance(killingPoint.getPosX(),killingPoint.getPosY(),agents.get(j).getPosX(),agents.get(j).getPosY())-Agent.getAgentRadius(), 0.001);
                    reward += rewardIntensity/dist/dist;
                }
            } else {
                dist = Math.max(distance(killingPoint.getPosX(),killingPoint.getPosY(),agent.getPosX(),agent.getPosY())-Agent.getAgentRadius(), 0.001);
                reward += 0*punishmentIntensity/dist/dist;
            }
        }
        return reward;
    }
    public void evolve() {
        if (frameCount%3==0) {
            addState();
        }
        if (frameCount%((int)(2+20*nnStrategies.get(0).getEpsilon()))==0) {
            //agents.get(0).decide();
            for (int i = 0; i < agents.size(); i++) {
                agents.get(i).decide();
            }
        }
        if (frameCount%200==0 && roundCount <= 20){
            Agent agent;
            Strategy strat;
            for (int i = 0; i < agents.size(); i++) {
                agent = agents.get(i);
                strat = agent.getStrategy();
                if (strat.getIntermediateLearn()) {
                    strat.learn(gameHistory, 1*inGameReward(agent), i);
                }
            }
        }
        for (int i = 0; i < agents.size(); i++) {
            agents.get(i).evolve(this);
        }
        for (int i = 0; i < killingPoints.size(); i++) {
            killingPoints.get(i).evolve(this);
        }
        if (roundsLimit >= 0 && frameCount>=roundsLimit){
            for (int i = 0; i < agents.size(); i++) {
                agents.get(i).getStrategy().learn(gameHistory, -10, i);
            }
            init(false);
        }
        frameCount++;
    }
    public void nextRound(int indexWinner) {
        incrementScore(indexWinner);
        if (indexWinner >= 0) {
            double reward;
            for (int i = 0; i < agents.size(); i++) {
                Strategy strat = agents.get(i).getStrategy();
                reward = agents.get(i).getTeam() == indexWinner ? strat.getRewardIntensity() : strat.getPunishmentIntensity();
                strat.learn(gameHistory, (1+50000.0/frameCount)*reward, i);
            }
        }
        init(false);
    }
    public int getAgentsNumber(){
        return teamsNumber*teamSize;
    }
    public int getKillingPointsNumber(){
        return teamsNumber*teamSize*(teamSize-1)/2;
    }
    public Vector<Agent> getAgents(){
        return agents;
    }
    public Agent getAgent(int agentIndex){
        return agents.get(agentIndex);
    }
    public Vector<KillingPoint> getKillingPoints(){
        return killingPoints;
    }
    public void executeCommands() {
        if (turboInputBuffer.read()) turboMode = !turboMode;
        if (pausedInputBuffer.read()) paused = !paused;
        if (restartInputBuffer.read()) init(false);
    }
    public int getArenaRadius() {
        Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
        return (int) (0.5*screenSize.getHeight()-25);
    }
    public int getCenterArenaX(){
        return getArenaRadius()+10;
    }
    public int getCenterArenaY(){
        return getArenaRadius()+10;
    }
    public static Color getTeamColor(int team){
        return teamColors.get(team);
    }
    public int getTeamsNumber(){
        return teamsNumber;
    }
    public int getScore(int i){
        return scores.get(i);
    }
    public void incrementScore(int i){
        scores.set(i, scores.get(i)+1);
    }
    public void setMustReset(boolean mustReset) {
        this.mustReset = mustReset;
    }
    public boolean getTurboMode() {
        return turboMode;
    }
    public boolean getPaused() {
        return paused;
    }
    public double getSpeed(){
        return speed;
    }
    public double[] calculateLastFeatures(Agent agent) {
        int agentIndex = 0;
        for (int i = 0; i < agents.size(); i++) {
            if (agents.get(i) == agent) {
                agentIndex = i;
                break;
            }
        }
        return gameHistory.calculateFeatures(gameHistory.getSize()-1, gameHistory.getSize()-1, agentIndex);
    }
}
