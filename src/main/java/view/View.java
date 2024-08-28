package view;

import controller.InputManager;
import game.Game;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;


public class View {
    private Game game;
    private Stage stage;
    private GameCanvas gameCanvas;
    private Scene scene;
    private Rectangle2D screenSize;
    public View(Game game, Stage stage) {
        this.game = game;
        this.stage = stage;
        Pane root = new Pane();
        stage.setMaximized(true);
        stage.setTitle("AI Battleground");
        screenSize = Screen.getPrimary().getVisualBounds();
        scene = new Scene(root, screenSize.getWidth(), screenSize.getHeight());
        gameCanvas = new GameCanvas((int) screenSize.getWidth(), (int) screenSize.getHeight(), game);
        root.getChildren().add(gameCanvas);
        stage.setScene(scene);
        stage.show();
        scene.setOnKeyPressed(event -> { InputManager.getInstance().setInput(event.getCode(), true); });
        scene.setOnKeyReleased(event -> { InputManager.getInstance().setInput(event.getCode(), false); });
    }

    public void display() {
        Canvas canvas = new Canvas((int)screenSize.getWidth(),(int)screenSize.getHeight());
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gameCanvas.display();
    }
}
