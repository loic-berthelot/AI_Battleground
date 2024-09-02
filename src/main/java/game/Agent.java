package game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import strategy.NullStrategy;
import strategy.Strategy;

public class Agent extends Particle {
    static private double sqrtHalf = Math.sqrt(0.5);
    static private double speed;
    static private double agentRadius;
    private double posX;
    private double posY;
    Strategy strategy;
    private double orderX;
    private double orderY;
    private boolean alive;
    private int killCount;
    static int globalId = 0;
    private int id;
    public Agent(double posX, double posY, int team) {
        super(agentRadius);
        this.team = team;
        orderX = 0;
        orderY = 0;
        strategy = new NullStrategy();
        id = globalId++;
        init(posX, posY);
    }
    public void init() {
        alive = true;
        killCount = 0;
        orderX = 0;
        orderY = 0;
    }
    public void init(double posX, double posY){
        init();
        setPos(posX, posY);
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
    public double getPosX() {
        return posX;
    }
    public double getPosY() {
        return posY;
    }
    public void setPos(double posX, double posY) {
        this.posX = posX;
        this.posY = posY;
    }
    public void die() {
        alive = false;
    }
    public void revive(){
        alive = true;
    }
    public boolean isAlive() {
        return alive;
    }
    public int getKillCount(){
        return killCount;
    }
    public void incrementKillCount(){
        killCount++;
    }
    @Override
    public void draw(GraphicsContext graphicsContext, Game game) {
        super.draw(graphicsContext, game);
        if (strategy.isHuman()) {
            int arenaRadius = game.getArenaRadius();
            graphicsContext.setFill(Color.WHITE);
            double size = 0.7*radius * arenaRadius;
            graphicsContext.fillOval(game.getCenterArenaX() + getPosX()* arenaRadius - 0.5 * size , game.getCenterArenaY() - getPosY() * arenaRadius - 0.5 * size , size, size);
            graphicsContext.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12));
            graphicsContext.setFill(Color.BLACK);
            graphicsContext.fillText(String.valueOf(id), game.getCenterArenaX() + getPosX()* arenaRadius, game.getCenterArenaY() - getPosY() * arenaRadius);
        }
    }
    public void recordState(){
        strategy.recordState();
    }
    public void discardStates(){
        strategy.discardStates();
    }
}
