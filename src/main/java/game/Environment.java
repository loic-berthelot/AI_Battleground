package game;

import javafx.scene.paint.Color;

import java.util.Vector;

public class Environment {
    final private static Vector<Color> teamColors;
    static {
        teamColors = new Vector<>();
        teamColors.add(Color.BLUE);
        teamColors.add(Color.RED);
        teamColors.add(Color.GREEN);
        teamColors.add(Color.YELLOW.deriveColor(0,1,0.9,1));
        teamColors.add(Color.OLIVE);
        teamColors.add(Color.PURPLE);
        teamColors.add(Color.AQUAMARINE);
        teamColors.add(Color.ORANGE);
    }
    public static Color getTeamColor(int team){
        return teamColors.get(team);
    }
}
