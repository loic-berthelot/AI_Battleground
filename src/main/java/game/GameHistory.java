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
    private int totalPoints;
    private int[] totalTeamPoints;
    public GameHistory(Game game, int scoresDepth, int ratiosDepth) {
        this.game = game;
        this.scoresDepth = scoresDepth;
        this.ratiosDepth = ratiosDepth;
        scores = new Vector<>();
        ratios = new Vector<>();
        totalPoints = 0;
        totalTeamPoints = new int[game.getTeamsNumber()];
    }
    public void registerRatio(int[] score) {
        scores.add(0,score);
        int teamsNumber = game.getTeamsNumber();
        double[] ratio = new double[game.getTeamsNumber()];
        for (int i = 0; i < teamsNumber; i++) {
            totalPoints += score[i];
            totalTeamPoints[i] += score[i];
        }
        if (scores.size() > scoresDepth && scoresDepth > 0) {
            int[] lastScore = scores.lastElement();
            for (int i = 0; i < teamsNumber; i++) {
                totalPoints -= lastScore[i];
                totalTeamPoints[i] -= lastScore[i];
            }
            scores.remove(lastScore);
        }
        for (int i = 0; i < teamsNumber; i++) {
            if(totalPoints > 0) ratio[i] = totalTeamPoints[i]/(double)totalPoints;
            else ratio[i] = 0;
        }
        ratios.add(0, ratio);
        if (ratios.size() > ratiosDepth) {
            ratios.remove(ratios.lastElement());
        }
    }
    public void draw(GraphicsContext graphicsContext) {
        synchronized (ratios) {
            final double width = 900;
            final double height = 600;
            final int border = 4;
            final double cornerX = game.getScreenPosX(1)+10+border;
            final double cornerY = game.getScreenPosY(-1)-height-border;
            graphicsContext.setFill(Color.BLACK);
            graphicsContext.fillRect(cornerX-border, cornerY-border, width+2*border, height+2*border);
            int teamsNumber = game.getTeamsNumber();
            int ratiosSize = ratios.size();
            double totalHeight;
            for (int j = 0; j < ratiosSize; j++) {
                totalHeight = 0;
                for (int i = teamsNumber-1; i >= 0; i--) {
                    graphicsContext.setFill(Game.getTeamColor(i));
                    graphicsContext.fillRect(cornerX+(ratiosDepth-j-1)*width/((double) ratiosDepth), cornerY + totalHeight, 1+width / ((double) ratiosDepth), height*ratios.get(j)[i]);
                    totalHeight += height*ratios.get(j)[i];
                }
            }
            graphicsContext.setStroke(new Color(1,1,1,0.7));
            graphicsContext.setLineWidth(1);
            final int divisionsNumber = 10;
            for (int i = 1; i < divisionsNumber; i++) {
                if (i==divisionsNumber/2) {
                    graphicsContext.setLineWidth(3);
                } else {
                    graphicsContext.setLineWidth(1.5);
                }
                graphicsContext.strokeLine(cornerX, cornerY+i/(double) divisionsNumber*height, cornerX+width, cornerY+i/(double) divisionsNumber*height);
            }
        }
    }
}
