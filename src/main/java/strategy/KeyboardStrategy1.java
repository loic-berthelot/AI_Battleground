package strategy;

import controller.InputManager;
import game.Agent;
import game.Game;

public class KeyboardStrategy1 extends Strategy {
    public KeyboardStrategy1(Game game) {
        super(game);
    }
    public void decide(Agent agent) {
        boolean moveRight = InputManager.getInstance().getInput("MoveRight1");
        boolean moveLeft = InputManager.getInstance().getInput("MoveLeft1");
        boolean moveUp = InputManager.getInstance().getInput("MoveUp1");
        boolean moveDown = InputManager.getInstance().getInput("MoveDown1");
        if (moveRight && !moveLeft) agent.setOrderX(1);
        else if (!moveRight && moveLeft) agent.setOrderX(-1);
        else agent.setOrderX(0);
        if (moveUp && !moveDown) agent.setOrderY(1);
        else if (!moveUp && moveDown) agent.setOrderY(-1);
        else agent.setOrderY(0);
    }
}
