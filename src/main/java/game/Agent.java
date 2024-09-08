package game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import strategy.NullStrategy;
import strategy.Strategy;

import java.util.ArrayList;

public class Agent extends Particle {
    final static private double sqrtHalf = Math.sqrt(0.5);
    final private int group;
    static private double speed;
    static private double agentRadius;
    private Position position;
    private Position graphicalPosition;
    Strategy strategy;
    private double orderX;
    private double orderY;
    private boolean alive;
    private int killCount;
    static int globalId = 1;
    private int id;
    private ArrayList<Position> positionsHistory;
    private double orientation;
    private double targetOrientation;
    ArrayList<Eye> eyes;
    public Agent(int team, int group) {
        super(agentRadius);
        this.team = team;
        this.group = group;
        strategy = new NullStrategy();
        id = globalId++;
        eyes = new ArrayList<>();
        eyes.add(new Eye(this, -0.6));
        eyes.add(new Eye(this, 0.6));
        init();
    }
    public void init() {
        alive = true;
        killCount = 0;
        orderX = 0;
        orderY = 0;
        positionsHistory = new ArrayList<>();
    }
    public void init(Position position) {
        init();
        setPos(position);
        updateGraphicalPosition();
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
    public void updateGraphicalPosition() {
        graphicalPosition = new Position(position.getX(), position.getY());
    }
    public void move(double dx, double dy) {
        if (dx == 0 || dy == 0) {
            position.addX(speed*dx);
            position.addY(speed*dy);
        } else {
            position.addX(speed*dx*sqrtHalf);
            position.addY(speed*dy*sqrtHalf);
        }
        double dist = position.distanceToCenter();
        if (dist > 1-radius) {
            position.multiply((1-radius)/dist);
        }
        updateGraphicalPosition();
        positionsHistory.add(new Position(position));
        if (dx != 0 || dy != 0) {
            targetOrientation = (Math.atan2(dy, dx)+2*Math.PI)%(2*Math.PI);
        }
    }
    public void adjustOrientation(){
        double diff = (targetOrientation - orientation+2*Math.PI)%(2*Math.PI);
        final double rotationSpeed = 0.1;
        if (Math.abs(diff) <= rotationSpeed) {
            orientation = targetOrientation;
            for (Eye eye : eyes) eye.adjustOrientation(0);
        } else {
            if(diff > Math.PI || diff < 0){
                orientation -= rotationSpeed;
                for (Eye eye : eyes) eye.adjustOrientation(-1);
            } else {
                orientation += rotationSpeed;
                for (Eye eye : eyes) eye.adjustOrientation(1);
            }
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
    public Position getPosition(){
        return position;
    }
    public Position getGraphicalPosition(){
        return graphicalPosition;
    }
    public double getPosX() {
        return position.getX();
    }
    public double getPosY() {
        return position.getY();
    }
    public int getGroup(){
        return group;
    }
    public void setPos(Position position) {
        this.position = position;
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
        graphicsContext.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 200*agentRadius));
        graphicsContext.setFill(Color.BLACK);
        Text text = new Text(String.valueOf(id));
        double textWidth = text.getLayoutBounds().getWidth();
        double textHeight = text.getLayoutBounds().getHeight();
        graphicsContext.fillText(text.getText(), game.getScreenPosX(graphicalPosition.getX())-textWidth/2, game.getScreenPosY(graphicalPosition.getY())+textHeight/2);
        for (int i = 0; i < eyes.size(); i++) {
            eyes.get(i).draw(graphicsContext, game);
        }
    }
    public void recordState(){
        strategy.recordState();
    }
    public void discardStates(){
        strategy.discardStates();
    }
    public double getLastMovesX(int frames){
        int size = positionsHistory.size();
        if (frames > size) frames = size;
        if (frames == 0) return 0;
        return positionsHistory.get(size-1).getX()-positionsHistory.get(size-frames).getX();
    }
    public double getLastMovesY(int frames){
        int size = positionsHistory.size();
        if (frames > size) frames = size;
        if (frames == 0) return 0;
        return positionsHistory.get(size-1).getY()-positionsHistory.get(size-frames).getY();
    }
    public double getLastMoveX(){
        return getLastMovesX(1);
    }
    public double getLastMoveY(){
        return getLastMovesY(1);
    }
    public double getOrientation(){
        return orientation;
    }
    public double getTargetOrientation(){
        return targetOrientation;
    }
}
