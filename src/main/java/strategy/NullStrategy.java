package strategy;

import game.Agent;
import game.Game;

public class NullStrategy extends Strategy {
    public NullStrategy() {
        super(null);
    }
    public void decide(Agent agent) {

    }
}
