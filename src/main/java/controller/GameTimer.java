package controller;

import game.Game;
import javafx.animation.AnimationTimer;
import view.View;

public class GameTimer extends AnimationTimer {
    private int frameCount;
    private Game game;
    private View view;

    public GameTimer(Game game, View view) {
        this.game = game;
        this.view = view;
        frameCount = 0;
    }
    @Override
    public void handle(long now) {
        view.display();
        /*
        game.executeCommands();
        if (! game.getPaused()) {
            if (game.getTurboMode()) {
                int turboFrames = 100;
                frameCount+=turboFrames;
                for (int i = 0; i < turboFrames; i++) game.evolve();
            } else {
                frameCount++;
                game.evolve();
            }
        }*/
    }
}
