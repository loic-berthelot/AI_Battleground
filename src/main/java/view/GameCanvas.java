package view;

import game.*;
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
    public void displayArena(){
        Vector<Agent> agents = game.getAgents();
        Vector<KillingPoint> killingPoints = game.getKillingPoints();
        Vector<Light> lights = game.getLights();
        clearCanvas();
        game.getArena().draw(graphicsContext, game);
        Vector<AreaOfEffect> aoes = game.getAoes();
        for (AreaOfEffect aoe : aoes) {
            aoe.draw(graphicsContext, game);
        }
        synchronized(lights) {
            for (final Light light : lights) {
                light.draw(graphicsContext, game);
            }
        }
        for (final KillingPoint killingPoint : killingPoints) {
            killingPoint.draw(graphicsContext, game);
        }
        for (final Agent agent : agents) {
            agent.draw(graphicsContext, game);
        }
        for (final Ball ball : game.getBalls()) {
            ball.draw(graphicsContext, game);
        }
    }

    public void displayInterface(){
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
        font = Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 40);
        graphicsContext.setFont(font);
        final int margin = 7;
        final double cornerX = game.getScreenPosX(1) + 80;
        final double cornerY = game.getScreenPosY(-1)-game.getGameHistory().getHeight()-75;
        graphicsContext.setFill(new Color(1, 1, 1, 0.5));
        for (int i = 0; i < game.getTeamsNumber(); i++){
            if (game.isLastWinner(i)) {
                final double shiftX = 180.0 * (i % 4);
                final double shiftY = -55*(int)(i/4);
                Text text = new Text(Integer.toString(game.getScore(i)));
                text.setFont(font);
                graphicsContext.fillOval(cornerX + shiftX - margin, cornerY + shiftY - margin, text.getLayoutBounds().getWidth() + 2 * margin, text.getLayoutBounds().getHeight() + 2 * margin);
            }
        }
        for (int i = 0; i < game.getTeamsNumber(); i++){
            final double shiftX = 180.0 * (i % 4);
            final double shiftY = -55*(int)(i/4);
            if (game.isLastWinner(i)) {
                Text text = new Text(Integer.toString(game.getScore(i)));
                text.setFont(font);
                graphicsContext.setFill(new Color(1, 1, 1, 0.8));
                graphicsContext.fillOval(cornerX + shiftX - margin, cornerY + shiftY - margin, text.getLayoutBounds().getWidth() + 2 * margin, text.getLayoutBounds().getHeight() + 2 * margin);
            }
            graphicsContext.setFill(Environment.getTeamColor(i));
            graphicsContext.fillText(Integer.toString(game.getScore(i)), cornerX+shiftX, cornerY+40 + shiftY);
        }
        game.getGameHistory().draw(graphicsContext);
    }

    public void display() {
        displayArena();
        displayInterface();
    }
}
