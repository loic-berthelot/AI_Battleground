package strategy;

import controller.InputManager;
import game.Agent;
import game.Game;

public class KeyboardStrategy2 extends Strategy {
    public KeyboardStrategy2(Game game) {
        super(game);
    }
    public void decide(Agent agent) {
        boolean moveRight = InputManager.getInstance().getInput("MoveRight2");
        boolean moveLeft = InputManager.getInstance().getInput("MoveLeft2");
        boolean moveUp = InputManager.getInstance().getInput("MoveUp2");
        boolean moveDown = InputManager.getInstance().getInput("MoveDown2");
        if (moveRight && !moveLeft) agent.setOrderX(1);
        else if (!moveRight && moveLeft) agent.setOrderX(-1);
        else agent.setOrderX(0);
        if (moveUp && !moveDown) agent.setOrderY(1);
        else if (!moveUp && moveDown) agent.setOrderY(-1);
        else agent.setOrderY(0);
    }
    @Override
    public boolean isHuman() {
        return true;
    }
}
