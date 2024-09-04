package controller;

import game.Game;
import javafx.application.Application;
import javafx.stage.Stage;
import view.View;

import java.io.IOException;

public class Main extends Application {
    private Game game;
    private View view;
    private GameTimer timer;
    private static Thread controllerThread;

    @Override
    public void start(Stage stage) throws IOException {
        game = new Game();
        controllerThread = new Thread(new GameController(game));
        controllerThread.start();
        view = new View(game, stage);
        timer = new GameTimer(game, view);
        timer.start();
        stage.setOnCloseRequest(event -> {
            controllerThread.stop();
        });
    }

    public static void main(String[] args) {
        launch();
    }
}