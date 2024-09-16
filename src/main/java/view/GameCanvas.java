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
import javafx.scene.text.Text;

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
        Font font = Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 25);
        graphicsContext.setFont(font);
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillText("Round :  "+Integer.toString(game.getRoundCount()), game.getScreenPosX(1)+10, game.getScreenPosY(1)+20);
        graphicsContext.fillText("Frame :  "+Integer.toString(game.getFrameCount()), game.getScreenPosX(1)+10, game.getScreenPosY(1)+45);
        if(game.getFrameLimit() > 0){
            Text text = new Text("Frame :  "+Integer.toString(game.getFrameLimit()));
            text.setFont(font);
            double shift = text.getLayoutBounds().getWidth();
            graphicsContext.fillText(" / "+Integer.toString(game.getFrameLimit()), game.getScreenPosX(1)+10+shift, game.getScreenPosY(1)+45);
        }
        graphicsContext.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 40));
        for (int i = 0; i < game.getTeamsNumber(); i++){
            graphicsContext.setFill(Game.getTeamColor(i));
            graphicsContext.fillText(Integer.toString(game.getScore(i)), game.getScreenPosX(1)+10+140.0*(i%4), game.getScreenPosY(-1)-330-50*(int)(i/4));
        }
        game.getGameHistory().draw(graphicsContext);
    }
}
