package game;

import controller.InputBuffer;
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
    final private static Vector<Color> teamColors;
    final private Vector<Integer> scores;
    final private int teamsNumber;
    final private int teamSize;
    private boolean turboMode;
    private boolean paused;
    final private InputBuffer turboInputBuffer;
    final private InputBuffer pausedInputBuffer;
    final private InputBuffer restartInputBuffer;
    final private double speed;
    private int frameCount;
    private int roundCount;
    final private int frameLimit;
    final private int recordingDelta;
    final private int decisionDelta;
    final private ArrayList<NNStrategy1output> nnStrategies;
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
        decisionDelta = 5;
        recordingDelta = 15;
        Agent.setAgentRadius(0.08);
        Agent.setSpeed(speed);
        frameLimit = 2500;
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
    public void init(boolean firstInit){
        roundCount++;
        frameCount = 0;
        if (firstInit) agents = new Vector<>();
        Random random = new Random();
        double angleShift = random.nextDouble(2*Math.PI);
        int agentIndex = 0;
        for (int i = 0; i < teamsNumber; i++) {
            for (int j = 0; j < teamSize; j++) {
                double angle = 2*Math.PI*i/teamsNumber+angleShift;
                double dist = 0.4+0.4*(j/(double)teamSize);
                if (firstInit) {
                    Agent a = new Agent(new Position(dist * Math.cos(angle), dist * Math.sin(angle)), i);
                    agents.add(a);
                    if ( i ==0) {
                        //if (j == 0) a.setStrategy(new KeyboardStrategy1(this));
                        //else if (j == 1) a.setStrategy(new KeyboardStrategy2(this));
                        //a.setStrategy(new NNStrategy1output(this, a));
                        a.setStrategy(new NNStrategy9outputs(this, a));
                    } else {
                        a.setStrategy(new NNStrategy2outputs(this, a));
                        //a.setStrategy(new RuleBasedStrategy(this, 1));
                        //a.setStrategy(new RandomStrategy(this, 0.2));
                    }
                    buildKillingPoints();
                } else {
                    Agent a = agents.get(agentIndex);
                    a.init(new Position(dist * Math.cos(angle), dist * Math.sin(angle)));
                    //if (roundCount%6 == 0 && i == 0)  a.setStrategy(new RandomStrategy(this, 0.2));
                    //else if ( i == 0)  a.setStrategy(new RuleBasedStrategy(this));
                }
                agentIndex++;
            }
        }
        for (Agent a : agents) {
            a.discardStates();
            a.recordState();
        }
    }
    public void evolve() {
        if (frameCount%recordingDelta==0) {
            for (Agent a : agents) {
                a.discardStates();
                a.recordState();
            };
        }
        for (Agent a : agents) {
            if(a.getStrategy().isHuman()) a.decide();
            else if (frameCount%decisionDelta==0) a.decide();
        }
        if (frameCount%500==0){
            Agent agent;
            Strategy strat;
            for (int i = 0; i < agents.size(); i++) {
                agent = agents.get(i);
                strat = agent.getStrategy();
                if (strat.getIntermediateLearn()) {
                    strat.learn(0.2*strat.getPunishmentIntensity());
                }
            }
        }
        for (int i = 0; i < agents.size(); i++) {
            agents.get(i).evolve(this);
            agents.get(i).adjustOrientation();
        }
        for (int i = 0; i < killingPoints.size(); i++) {
            killingPoints.get(i).evolve(this);
        }
        if (frameLimit >= 0 && frameCount>= frameLimit){
            for (int i = 0; i < agents.size(); i++) {
                agents.get(i).getStrategy().learn( -10);
            }
            init(false);
        }
        checkEndRound();
        frameCount++;
    }
    public void checkEndRound() {
        int[] scoreIncrease = new int[teamsNumber];
        for (int i = 0; i < agents.size(); i++) {
            scoreIncrease[agents.get(i).getTeam()] = agents.get(i).getKillCount();
        }
        boolean mustReset = false;
        for (int i = 0; i < teamsNumber; i++) {
            if (scoreIncrease[i] > 0) {
                mustReset = true;
                scores.set(i, scores.get(i)+scoreIncrease[i]);
            }
        }
        if (mustReset) {
            double reward;
            for (int i = 0; i < agents.size(); i++) {
                Strategy strat = agents.get(i).getStrategy();
                reward = scoreIncrease[agents.get(i).getTeam()] > 0 ? strat.getRewardIntensity() : strat.getPunishmentIntensity();
                strat.learn(reward);
            }
            init(false);
        }
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
    public boolean getTurboMode() {
        return turboMode;
    }
    public boolean getPaused() {
        return paused;
    }
    public double getSpeed(){
        return speed;
    }
    public int getFrameCount(){
        return frameCount;
    }
    public int getRoundCount(){
        return roundCount;
    }
}
