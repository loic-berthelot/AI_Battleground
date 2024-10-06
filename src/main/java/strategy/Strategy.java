package strategy;

import game.Agent;
import game.Game;
import game.KillingPoint;
import game.Position;

import java.util.Random;
import java.util.Vector;

public abstract class Strategy {
    protected int orderX;
    protected int orderY;
    Game game;
    public Strategy(Game game){
        this.game = game;
    }
    public abstract void decide(Agent agent);
    public double getRewardIntensity() {
        return 1;
    }
    public double getPunishmentIntensity() {
        return 0;
    }
    public void learn(boolean victory, int epochsNumber){

    }
    public boolean getIntermediateLearn(){
        return false;
    }
    public boolean isHuman() {
        return false;
    }
    public void goToBestPosition(Agent agent){
        Position position = agent.getPosition();
        agent.setOrderX(0);
        agent.setOrderY(0);
        double shift = 1;
        double bestScore = simulateMove(agent, position, 0, 0);
        double score;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i != 0 || j != 0) {
                    score = simulateMove(agent, new Position(position), i*shift, j*shift);
                    if (score > bestScore) {
                        bestScore = score;
                        agent.setOrderX(i);
                        agent.setOrderY(j);
                    }
                }
            }
        }
        agent.setPos(position);
    }
    public double simulateMove(Agent agent, Position position, double dx, double dy){
        return 0;
    }
    private double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
    }
    public double dangerPreyHeuristic(Agent agent, double agressivity) {
        double dist;
        double distDanger = 10;
        double distPrey = 10;
        Vector<KillingPoint> killingPoints = game.getKillingPoints();
        Vector<Agent> agents = game.getAgents();
        for (KillingPoint kp : killingPoints) {
            for (Agent ag : agents) {
                if (ag.getTeam() != kp.getTeam()) {
                    dist = distance(kp.getPosX(), kp.getPosY(), ag.getPosX(), ag.getPosY()) - Agent.getAgentRadius();
                    if (ag.getTeam() == agent.getTeam()) {
                        if (dist < distDanger) distDanger = dist;
                    } else {
                        if (dist < distPrey) distPrey = dist;
                    }
                }
            }
        }
        return -1/ Math.max(distDanger, 0.001) + agressivity / Math.max(distPrey, 0.001);
    }
    public double invertDistanceSumHeuristic(Agent agent, boolean cubeDist, double alliesCoefficient) {
        double reward, dist;
        KillingPoint killingPoint;
        reward = 0;
        Vector<KillingPoint> killingPoints = game.getKillingPoints();
        Vector<Agent> agents = game.getAgents();
        for (int i = 0; i < killingPoints.size(); i++){
            killingPoint = killingPoints.get(i);
            if (killingPoint.getTeam() == agent.getTeam()) {
                for (int j = 0; j < agents.size(); j++) {
                    dist = Math.max(distance(killingPoint.getPosX(),killingPoint.getPosY(),agents.get(j).getPosX(),agents.get(j).getPosY())-Agent.getAgentRadius(), 0.001);
                    if (cubeDist) dist = dist*dist*dist;
                    else dist = dist*dist;
                    reward += 0.015*(agents.get(j).getTeam() == killingPoint.getTeam() ? alliesCoefficient : 1)/dist;
                }
            } else {
                dist = Math.max(distance(killingPoint.getPosX(),killingPoint.getPosY(),agent.getPosX(),agent.getPosY())-Agent.getAgentRadius(), 0.001);
                if (cubeDist) dist = dist*dist*dist;
                else dist = dist*dist;
                reward += -0.01/dist;
            }
        }
        Random random = new Random();
        reward += 0.00001*random.nextDouble(1);
        return reward;
    }
    public void recordState(){

    }
    public void discardStates(){

    }
}
