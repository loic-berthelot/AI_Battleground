package game;

import arena.Arena;
import arena.CircularArena;
import arena.SquareArena;
import arena.TorusArena;
import controller.InputBuffer;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import strategy.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

public class Game {
    private Vector<Agent> agents;
    private Vector<KillingPoint> killingPoints;
    private Vector<Light> lights;
    final private static Vector<Color> teamColors;
    private Vector<Integer> scores;
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
    private GameHistory gameHistory;
    private Arena arena;
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
        teamsNumber = 3;
        teamSize = 2;
        speed = 0.005;
        decisionDelta = 25;
        recordingDelta = 20;
        Agent.setAgentRadius(0.08);
        Agent.setSpeed(speed);
        frameLimit = 1800;
        scores = new Vector<>();
        arena = new TorusArena(0.25);
        gameHistory = new GameHistory(this, 100, 600);
        //arena = new SquareArena();
        buildAgents();
        buildKillingPoints();
        giveStrategies();
        init();
        for (int i = 0; i < teamsNumber; i++) {
            scores.add(0);
        }
        turboMode = false;
        turboInputBuffer = new InputBuffer("Turbo");
        pausedInputBuffer = new InputBuffer("Pause");
        restartInputBuffer = new InputBuffer("Restart");
    }

    public void giveStrategies(){
        int agentIndex = 0;
        Agent a;
        for (int i = 0; i < teamsNumber; i++) {
            for (int j = 0; j < teamSize; j++) {
                a = agents.get(agentIndex);
                if (i == 0) {
                    //if (j == 0) a.setStrategy(new KeyboardStrategy1(this));
                    //else if (j == 1) a.setStrategy(new KeyboardStrategy2(this));
                    //a.setStrategy(new NNStrategy9outputs(this, a));
                    a.setStrategy(new RuleBasedStrategy(this, 0));
                } else {
                    if (j==0) a.setStrategy(new NNStrategy1output(this, a));
                    else a.setStrategy(agents.get(agentIndex-1).getStrategy());
                    //a.setStrategy(new RuleBasedStrategy(this, 0));
                }
                agentIndex++;
            }
        }
    }
    public void init(){
        roundCount++;
        frameCount = 0;
        lights = new Vector<>();
        Random random = new Random();
        double angleShift = random.nextDouble(2*Math.PI);
        int agentIndex = 0;
        switch (random.nextInt(3)) {
            case 0:
                arena = new CircularArena();
                break;
            case 1:
                arena = new SquareArena();
                break;
            case 2:
                arena = new TorusArena(0.05+random.nextDouble(0.35));
                break;
        }
        for (int i = 0; i < teamsNumber; i++) {
            for (int j = 0; j < teamSize; j++) {
                //double angle = 2*Math.PI*i/teamsNumber+angleShift;
                //double dist = 0.4+0.4*(j/(double)teamSize);
                double angle = random.nextDouble(2*Math.PI);
                double dist = random.nextDouble(1);
                agents.get(agentIndex).init(new Position(dist * Math.cos(angle), dist * Math.sin(angle)));
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
        for (Agent agent : agents) {
            agent.evolve();
        }
        for (KillingPoint kp : killingPoints) {
            kp.evolve(this);
        }
        if (frameLimit >= 0 && frameCount>= frameLimit){
            for (int i = 0; i < agents.size(); i++) {
                agents.get(i).getStrategy().learn( 3*agents.get(i).getStrategy().getPunishmentIntensity());
            }
            init();
        }
        if (frameCount%10==0) {
            for (int i = 0; i < agents.size(); i++) {
                lights.add(new Light(new Position(agents.get(i).getPosition()), getTeamColor(agents.get(i).getTeam()), Agent.getAgentRadius()));
            }
        }
        synchronized (lights) {
            Iterator<Light> iter = lights.iterator();
            while (iter.hasNext()) {
                Light light = iter.next();
                if (light.evolve()) {
                    iter.remove();
                }
            }
        }
        checkEndRound();
        frameCount++;
    }
    public void checkEndRound() {
        int[] scoreIncrease = new int[teamsNumber];
        for (int i = 0; i < agents.size(); i++) {
            scoreIncrease[agents.get(i).getTeam()] += agents.get(i).getKillCount();
        }
        boolean mustReset = false;
        for (int i = 0; i < teamsNumber; i++) {
            if (scoreIncrease[i] > 0) {
                mustReset = true;
                scores.set(i, scores.get(i)+1);
            }
        }
        if (mustReset) {
            double reward;
            for (int i = 0; i < agents.size(); i++) {
                Strategy strat = agents.get(i).getStrategy();
                reward = scoreIncrease[agents.get(i).getTeam()] > 0 ? strat.getRewardIntensity() : strat.getPunishmentIntensity();
                strat.learn(reward);
                gameHistory.registerRatio(scoreIncrease);
            }
            init();
        }
    }
    public void buildAgents(){
        agents = new Vector<>();
        for (int i = 0; i < teamsNumber; i++) {
            for (int j = 0; j < teamSize; j++) {
                Agent a = new Agent(this, i, (j < 2)?0 :1);
                agents.add(a);
            }
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
                if (agent1.getTeam() == agent2.getTeam() && agent1.getGroup() == agent2.getGroup()) killingPoints.add(new KillingPoint(agent1, agent2));
            }
        }
    }
    public int getAgentsNumber(){
        return agents.size();
    }
    public int getKillingPointsNumber(){
        return killingPoints.size();
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
    public Vector<Light> getLights(){
        return lights;
    }
    public void executeCommands() {
        if (turboInputBuffer.read()) turboMode = !turboMode;
        if (pausedInputBuffer.read()) paused = !paused;
        if (restartInputBuffer.read()) init();
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
    public double getScreenPosX(double posX) {
        return getCenterArenaX()+posX*getArenaRadius();
    }
    public double getScreenPosY(double posY) {
        return getCenterArenaY()-posY*getArenaRadius();
    }
    public double getScreenSize(double size) {
        return getArenaRadius()*size;
    }
    public Arena getArena(){
        return arena;
    }
    public Color getBackgroundColor(){
        return new Color(0.7,0.7,0.7,1);
    }
    public int getRecordingDelta(){
        return recordingDelta;
    }
    public int getFrameLimit(){
        return frameLimit;
    }
    public GameHistory getGameHistory(){
        return gameHistory;
    }
}
