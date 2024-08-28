package strategy;

import game.Agent;
import game.Game;

import java.util.Random;

public class RandomStrategy extends Strategy {
    double alpha;
    public RandomStrategy(Game game, double alpha){
        super(game);
        this.alpha = alpha;
    }
    public void decide(Agent agent) {
        Random random = new Random();
        if (random.nextFloat() < alpha) {
            agent.setOrderX(random.nextInt(3)-1);
            agent.setOrderY(random.nextInt(3)-1);
        }
    }
}