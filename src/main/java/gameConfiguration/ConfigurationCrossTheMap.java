package gameConfiguration;

import arena.SquareArena;
import game.*;

import java.util.Vector;

public class ConfigurationCrossTheMap extends GameConfiguration {
    public ConfigurationCrossTheMap(Game game){
        super(game);
    }

    @Override
    public void initGame() {
        game.setArena(new SquareArena());
        game.setTeamsNumber(2);
        game.setTeamSize(2);
        Vector<AreaOfEffect> aoes = new Vector<>();
        final double halfWidth = 0.03;
        for (int i = 0; i <= 1; i++) {
            aoes.add(new AreaOfEffect(new Position((halfWidth-1)*(i*2-1), 0), halfWidth, 1, i, Environment.getTeamColor(i).deriveColor(0, 1, 1, 0.5)));//
        }
        game.setAoes(aoes);
        buildAgents();
    }

    @Override
    public void initRound() {
        buildAttachedKillingPoints();
        int agentIndex = 0;
        for (int i = -1; i <= 1; i+=2) {
            for (int j = -1; j <= 1; j+=2) {
                game.getAgent(agentIndex).init(new Position(i*0.7, j*0.3));
                agentIndex++;
            }
        }
        orientAgentsTowardsCenter();
    }
    @Override
    public void checkEndRound() {
        final int teamsNumber = game.getTeamsNumber();
        final int agentsNumber = game.getAgentsNumber();
        int[] scoreIncrease = new int[teamsNumber];
        Vector<AreaOfEffect> aoes = game.getAoes();
        for (Agent agent : game.getAgents()) {
            scoreIncrease[agent.getTeam()] += agent.getKillCount();
            for (AreaOfEffect aoe : aoes) {
                if (aoe.getTeam() == agent.getTeam() && aoe.containsPart(agent)) {
                    scoreIncrease[agent.getTeam()] += 1;
                }
            }
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
