package controller;

import game.Game;

public class GameController implements Runnable {
    final private Game game;
    private int interval;
    private boolean stop;
    public GameController(Game game) {
        this.game = game;
        stop = false;
    }

    @Override
    public void run() {
        while (!stop){
            game.executeCommands();
            if (! game.getPaused()) {
                game.evolve();
                if (game.getTurboMode()) {
                    interval = 0;
                } else {
                    interval = 15;
                }
            }
            try {
                Thread.sleep(interval);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void stop(){
        stop = true;
    }
}