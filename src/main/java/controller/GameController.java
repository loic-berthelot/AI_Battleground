package controller;

import game.Game;

public class GameController implements Runnable {
    final private Game game;
    public GameController(Game game) {
        this.game = game;
    }

    @Override
    public void run() {
        boolean stop = false;
        while (!stop){
            game.evolve();
            try {
                Thread.sleep(15);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}