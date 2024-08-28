package game;

import strategy.NullStrategy;
import strategy.Strategy;

public class Agent extends Particle {
    static private double sqrtHalf = Math.sqrt(0.5);
    static private double speed;
    static private double agentRadius;
    Strategy strategy;
    private double orderX;
    private double orderY;
    public Agent(double posX, double posY, int team) {
        super(posX, posY, agentRadius);
        this.team = team;
        orderX = 0;
        orderY = 0;
        strategy = new NullStrategy();
    }
    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }
    public Strategy getStrategy() {
       return strategy;
    }
    public void decide() {
        strategy.decide(this);
    }
    public void move(double dx, double dy) {
        if (dx == 0 || dy == 0) {
            posX += speed*dx;
            posY += speed*dy;
        } else {
            posX += speed*dx*sqrtHalf;
            posY += speed*dy*sqrtHalf;
        }
        double dist = Math.sqrt(posX*posX+posY*posY);
        if (dist > 1-radius) {
            posX *= (1-radius)/dist;
            posY *= (1-radius)/dist;
        }
    }
    public void evolve(Game game){
        move(orderX, orderY);
    }
    public static void setAgentRadius(double r) {
        agentRadius = r;
    }
    public static double getAgentRadius(){
        return agentRadius;
    }
    public static void setSpeed(double s) {
        speed = s;
    }
    public void setOrderX(double orderX) {
        this.orderX = orderX;
    }
    public void setOrderY(double orderY) {
        this.orderY = orderY;
    }
}
