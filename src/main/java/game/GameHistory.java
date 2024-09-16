package game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Vector;

public class GameHistory {
    private Vector<int[]> scores;
    private Vector<double[]> ratios;
    private int scoresDepth;
    private int ratiosDepth;
    private Game game;
    public GameHistory(Game game, int scoresDepth, int ratiosDepth) {
        this.game = game;
        this.scoresDepth = scoresDepth;
        this.ratiosDepth = ratiosDepth;
        scores = new Vector<>();
        ratios = new Vector<>();
    }
    public int getTotalPoints(){
        int total = 0;
        int teamsNumber = game.getTeamsNumber();
        for (int[] score : scores) {
            for (int i = 0; i < teamsNumber; i++) {
                total += score[i];
            }
        }
        return total;
    }
    public void registerRatio(int[] score) {
        scores.add(0,score);
        if (scores.size() > scoresDepth) {
            scores.remove(scores.lastElement());
        }
        double teamPoints;
        double[] ratio = new double[game.getTeamsNumber()];
        double totalPoints = (double) getTotalPoints();
        for (int i = 0; i < game.getTeamsNumber(); i++) {
            teamPoints = 0;
            for (int[] s : scores) {
                teamPoints += s[i];
            }
            ratio[i] = teamPoints/totalPoints;
        }
        ratios.add(0, ratio);
        if (ratios.size() > ratiosDepth) {
            ratios.remove(ratios.lastElement());
        }
    }
    public void draw(GraphicsContext graphicsContext) {
        synchronized (ratios) {
            final double width = 600;
            final double height = 200;
            final int border = 4;
            final double cornerX = game.getScreenPosX(1)+10+border;
            final double cornerY = game.getScreenPosY(-1)-height-2*border;
            graphicsContext.setFill(Color.BLACK);
            graphicsContext.fillRect(cornerX-border, cornerY-border, width+2*border, height+2*border);
            int teamsNumber = game.getTeamsNumber();
            int ratiosSize = ratios.size();
            double totalHeight;
            for (int j = 0; j < ratiosSize; j++) {
                totalHeight = 0;
                for (int i = 0; i < teamsNumber; i++) {
                    graphicsContext.setFill(Game.getTeamColor(i));
                    graphicsContext.fillRect(cornerX+(ratiosDepth-j-1)*width/((double) ratiosDepth), cornerY + totalHeight, width / ((double) ratiosDepth), height*ratios.get(j)[i]);
                    totalHeight += height*ratios.get(j)[i];
                }
            }
        }
    }
}
