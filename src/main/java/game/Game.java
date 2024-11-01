package game;

import arena.Arena;
import controller.InputBuffer;
import gameConfiguration.*;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import strategy.*;

import java.util.Iterator;
import java.util.Vector;

public class Game {
    private Vector<Agent> agents;
    private Vector<KillingPoint> killingPoints;
    private Vector<Light> lights;
    private Vector<AreaOfEffect> aoes;
    private Vector<Integer> scores;
    private int teamsNumber;
    private int teamSize;
    private boolean turboMode;
    private boolean paused;
    private InputBuffer turboInputBuffer;
    private InputBuffer pausedInputBuffer;
    private InputBuffer restartRoundInputBuffer;
    private InputBuffer restartGameInputBuffer;
    private double speed;
    private int frameCount;
    private int roundCount;
    private int frameLimit;
    private int recordingDelta;
    private int decisionDelta;
    private GameHistory gameHistory;
    private Arena arena;
    private static int currentAgentId;
    private boolean[] lastWinners;
    private GameConfiguration gameConfiguration;
    private Vector<Ball> balls;
    public Game() {
        gameConfiguration = new ConfigurationSimple2vs2(this);
        initGame();
        turboMode = false;
        turboInputBuffer = new InputBuffer("Turbo");
        pausedInputBuffer = new InputBuffer("Pause");
        restartRoundInputBuffer = new InputBuffer("RestartRound");
        restartGameInputBuffer = new InputBuffer("RestartGame");
    }
    public void initGame(){
        currentAgentId = 1;
        roundCount = 0;
        teamsNumber = 2;
        teamSize = 2;
        speed = 0.005;
        decisionDelta = 10;
        recordingDelta = 20;
        Agent.setAgentRadius(0.08);
        Agent.setSpeed(speed);
        frameLimit = 2000;
        scores = new Vector<>();
        gameHistory = new GameHistory(this, 100, 300);
        aoes = new Vector<>();
        killingPoints = new Vector<>();
        balls = new Vector<>();
        gameConfiguration.initGame();
        initRound();
        giveStrategies();
        for (int i = 0; i < teamsNumber; i++) {
            scores.add(0);
        }
        lastWinners = new boolean[teamsNumber];
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
                    a.setStrategy(new NNStrategy2out(this, a));
                } else {
                    a.setStrategy(new RuleBasedStrategy(this, a, 0));
                    //a.setStrategy(new NullStrategy());
                }
                agentIndex++;
            }
        }
    }
    public void initRound(){
        gameConfiguration.initRound();
        roundCount++;
        frameCount = 0;
        lights = new Vector<>();
        for (Agent a : agents) {
            a.discardStates();
            a.recordState();
        }
    }
    public void evolve() {
        if (frameCount%recordingDelta==0) {
            for (Agent a : agents) a.recordState();
        }
        for (Agent a : agents) {
            if(a.getStrategy().isHuman()) a.decide();
            else if (frameCount%decisionDelta==0) {
                a.decide();
            }
        }
        if (frameCount%500==0){
            Agent agent;
            Strategy strat;
            for (int i = 0; i < agents.size(); i++) {
                agent = agents.get(i);
                strat = agent.getStrategy();
                if (strat.getIntermediateLearn()) {
                    strat.learn(false, 1);
                }
            }
        }
        for (Agent agent : agents) agent.evolve();
        manageCollisions();
        for (Agent a : agents) a.updateGraphicalPosition();
        for (KillingPoint kp : killingPoints) kp.evolve(this);
        if (frameCount%10==0) {
            for (Agent agent : agents) {
                if (agent.isAlive()) lights.add(new Light(new Position(agent.getPosition()), Environment.getTeamColor(agent.getTeam()), Agent.getAgentRadius()));
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
        synchronized (killingPoints) {
            Iterator<KillingPoint> iter = killingPoints.iterator();
            while (iter.hasNext()) {
                KillingPoint kp = iter.next();
                if (kp instanceof AttachedKillingPoint) {
                    AttachedKillingPoint ap = (AttachedKillingPoint) kp;
                    if (!ap.getAgent1().isAlive() || !ap.getAgent2().isAlive()) iter.remove();
                }
            }
        }
        gameConfiguration.checkEndRound();
        frameCount++;
    }
    public double distance(Position p1, Position p2) {
        double dx = p2.getX() - p1.getX();
        double dy = p2.getY() - p1.getY();
        return Math.sqrt(dx*dx+dy*dy);
    }
    public Vector<Particle> getSolidParticles(){
        Vector<Particle> particles = new Vector<>();
        for (Agent a : agents) {
            if (a.isAlive()) particles.add(a);
        }
        particles.addAll(balls);
        return particles;
    }
    public void manageCollisions(){
        double dist, angle;
        Particle p1;
        int iterations = 3;
        Vector<Particle> solidParticles = getSolidParticles();
        for (int j = 0; j < iterations; j++) {
            double[] shifts = new double[2*solidParticles.size()];
            for (int i = 0; i < solidParticles.size(); i++) {
                p1 = solidParticles.get(i);
                for (final Particle p2 : solidParticles) {
                    if (p1 != p2) {
                        dist = p2.getMass()/(p1.getMass()+p2.getMass())*Math.max(p1.getRadius()+p2.getRadius() - distance(p1.getPosition(), p2.getPosition()), 0);
                        angle = Math.atan2(p1.getPosY() - p2.getPosY(), p1.getPosX() - p2.getPosX());
                        shifts[2 * i] += dist * Math.cos(angle);
                        shifts[2 * i + 1] += dist * Math.sin(angle);
                    }
                }
            }
            Position pos;
            for (int i = 0; i < solidParticles.size(); i++) {
                pos = solidParticles.get(i).getPosition();
                pos.addX(shifts[2*i]);
                pos.addY(shifts[2*i+1]);
                arena.replaceParticle(solidParticles.get(i));
            }
        }
    }
    public boolean isLastWinner(int i){
        return lastWinners[i];
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
        if (restartRoundInputBuffer.read()) initRound();
        if (restartGameInputBuffer.read()) initGame();
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
    public int getTeamsNumber(){
        return teamsNumber;
    }
    public void setTeamsNumber(int teamsNumber) {
        this.teamsNumber = teamsNumber;
    }
    public void setTeamSize(int teamSize){
        this.teamSize = teamSize;
    }
    public int getTeamSize(){
        return teamSize;
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
    public Position getScreenPos(Position position) {
        return new Position(getScreenPosX(position.getX()), getScreenPosY(position.getY()));
    }
    public double getScreenSize(double size) {
        return getArenaRadius()*size;
    }
    public Arena getArena(){
        return arena;
    }
    public Color getBackgroundColor(){
        return new Color(0.8,0.8,0.8,1);
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
    public int getCurrentAgentId(){
        return currentAgentId++;
    }
    public void setArena(Arena arena) {
        this.arena = arena;
    }
    public void setAgents(Vector<Agent> agents) {
        this.agents = agents;
    }
    public void setKillingPoints(Vector<KillingPoint> killingPoints) {
        this.killingPoints = killingPoints;
    }
    public boolean isEndOfTime() {
        return frameLimit >= 0 && frameCount>= frameLimit;
    }
    public Vector<Integer> getScores(){
        return scores;
    }
    public void setLastWinner(int index, boolean value){
        lastWinners[index] = value;
    }
    public Vector<AreaOfEffect> getAoes() {
        return aoes;
    }
    public void setAoes(Vector<AreaOfEffect> aoes) {
        this.aoes = aoes;
    }
    public void addKillingPoint(KillingPoint kp) {
        killingPoints.add(kp);
    }
    public void removeAttachedKp(){
        synchronized (killingPoints) {
            Iterator<KillingPoint> iter = killingPoints.iterator();
            while (iter.hasNext()) {
                KillingPoint kp = iter.next();
                if (kp instanceof AttachedKillingPoint) iter.remove();
            }
        }
    }
    public void setBalls(Vector<Ball> balls) {
        this.balls = balls;
    }
    public Vector<Ball> getBalls(){
        return balls;
    }
    public void setFrameLimit(int frameLimit) {
        this.frameLimit = frameLimit;
    }
}
