package view;

import game.Agent;
import game.Game;
import game.KillingPoint;
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
        graphicsContext.setFill(new Color(0.7,0.7,0.7,1));
        graphicsContext.fillRect(0, 0, width, height);
    }
    public void display() {
        Vector<Agent> agents = game.getAgents();
        Vector<KillingPoint> killingPoints = game.getKillingPoints();
        clearCanvas();
        double radius = game.getArenaRadius();
        graphicsContext.setFill(new Color(0,0,0,1));
        graphicsContext.fillOval(10,10, 2*radius,2*radius);
        for (int i = 0; i < killingPoints.size(); i++) {
            killingPoints.get(i).draw(graphicsContext, game);
        }
        for (int i = 0; i < agents.size(); i++) {
            agents.get(i).draw(graphicsContext, game);
        }
        graphicsContext.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 20));
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillText("Round "+Integer.toString(game.getRoundCount()), 10, 20);
        graphicsContext.fillText("Frame "+Integer.toString(game.getFrameCount()), 10, 40);
        graphicsContext.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 40));
        for (int i = 0; i < game.getTeamsNumber(); i++){
            graphicsContext.setFill(Game.getTeamColor(i));
            graphicsContext.fillText(Integer.toString(game.getScore(i)), game.getCenterArenaX()*2.1+i%4*140, 40+50*(int)(i/4));
        }
    }
}
