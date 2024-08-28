package strategy;

import game.Agent;
import game.Game;
import game.GameHistory;

public abstract class Strategy {
    protected int orderX;
    protected int orderY;
    Game game;
    public Strategy(Game game){
        this.game = game;
    }
    public int getOrderX() {
        return orderX;
    }public int getOrderY() {
        return orderY;
    }
    public abstract void decide(Agent agent);
    public double getRewardIntensity() {
        return 0;
    }
    public double getPunishmentIntensity() {
        return 0;
    }
    public void learn(GameHistory gameHistory, double reward, int agentIndex){

    }
    public boolean getIntermediateLearn(){
        return false;
    }
}
