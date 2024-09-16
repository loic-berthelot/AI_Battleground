package view;

import game.Agent;
import game.Game;
import game.KillingPoint;
import game.Light;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import java.util.Vector;

public class GameCanvas extends Canvas {
    private Game game;
    private GraphicsContext graphicsContext;
    private int width;
    private int height;

    public GameCanvas(int width, int height, Game game) {
        super(width, height);
        this.width = width;
        this.height = height;
        this.game = game;
        this.graphicsContext = getGraphicsContext2D();
    }
    public void clearCanvas(){
        //graphicsContext.clearRect(0, 0, width, height);
        graphicsContext.setFill(game.getBackgroundColor());
        graphicsContext.fillRect(0, 0, width, height);
    }
    public void display() {
        Vector<Agent> agents = game.getAgents();
        Vector<KillingPoint> killingPoints = game.getKillingPoints();
        Vector<Light> lights = game.getLights();
        clearCanvas();
        game.getArena().draw(graphicsContext, game);
        synchronized(lights) {
            for (Light light : lights) {
                light.draw(graphicsContext, game);
            }
        }
        for (int i = 0; i < killingPoints.size(); i++) {
            killingPoints.get(i).draw(graphicsContext, game);
        }
        for (int i = 0; i < agents.size(); i++) {
            agents.get(i).draw(graphicsContext, game);
        }
        graphicsContext.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 20));
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillText("Round "+Integer.toString(game.getRoundCount()), game.getScreenPosX(1)+10, game.getScreenPosY(-1)-250);
        String text = "Frame "+Integer.toString(game.getFrameCount());
        if (game.getFrameLimit() > 0) text += " / "+Integer.toString(game.getFrameLimit());
        graphicsContext.fillText(text, game.getScreenPosX(1)+10, game.getScreenPosY(-1)-230);
        graphicsContext.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 40));
        for (int i = 0; i < game.getTeamsNumber(); i++){
            graphicsContext.setFill(Game.getTeamColor(i));
            graphicsContext.fillText(Integer.toString(game.getScore(i)), game.getCenterArenaX()*2.1+i%4*140, 40+50*(int)(i/4));
        }
        game.getGameHistory().draw(graphicsContext);
    }
}
