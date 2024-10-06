package gameConfiguration;

import arena.CircularArena;
import arena.SquareArena;
import arena.TorusArena;
import game.Game;
import game.Position;

import java.util.Random;
import java.util.Vector;

public class ConfigurationSimple2vs2 extends GameConfiguration{
    public ConfigurationSimple2vs2(Game game){
        super(game);
    }

    @Override
    public void initGame() {
        buildAgents();
        buildKillingPoints();
    }

    @Override
    public void initRound() {
        Random random = new Random();
        switch (random.nextInt(1)) {
            case 0:
                game.setArena(new CircularArena());
                break;
            case 1:
                game.setArena(new SquareArena());
                break;
            case 2:
                game.setArena(new TorusArena(0.05+random.nextDouble(0.35)));
                break;
        }
        double angleShift = random.nextDouble(2*Math.PI);
        int agentIndex = 0;
        final int teamsNumber = game.getTeamsNumber();
        final int teamSize = game.getTeamSize();
        for (int i = 0; i < teamsNumber; i++) {
            for (int j = 0; j < teamSize; j++) {
                double angle = 2*Math.PI*i/teamsNumber+angleShift;
                double dist = 0.4+0.4*(j/(double)teamSize);
                //double angle = random.nextDouble(2*Math.PI);
                //double dist = random.nextDouble(1);
                game.getAgent(agentIndex).init(new Position(dist * Math.cos(angle), dist * Math.sin(angle)));
                agentIndex++;
            }
        }
    }
    @Override
    public void checkEndRound() {
        final int teamsNumber = game.getTeamsNumber();
        final int agentsNumber = game.getAgentsNumber();
        int[] scoreIncrease = new int[teamsNumber];
        for (int i = 0; i < agentsNumber; i++) {
            scoreIncrease[game.getAgent(i).getTeam()] += game.getAgent(i).getKillCount();
        }
        boolean mustReset = false;
        Vector<Integer> scores = game.getScores();
        for (int i = 0; i < teamsNumber; i++) {
            if (scoreIncrease[i] > 0) {
                mustReset = true;
                scores.set(i, scores.get(i)+1);
            }
        }
        if (game.isEndOfTime()){
            mustReset = true;
        }
        if (mustReset) {
            for (int i = 0; i < agentsNumber; i++) {
                if (scoreIncrease[game.getAgent(i).getTeam()] > 0) {
                    game.getAgent(i).getStrategy().learn(true, 10);
                } else {
                    game.getAgent(i).getStrategy().learn(false, 10);
                }
            }
            for (int i = 0; i < teamsNumber; i++) {
                game.setLastWinner(i, scoreIncrease[i] > 0);
            }
            game.getGameHistory().registerRatio(scoreIncrease);
            game.initRound();
        }
    }
}
