package gameConfiguration;

import arena.SquareArena;
import game.*;

import java.util.Vector;

public class ConfigurationKillTheKing extends GameConfiguration {
    public ConfigurationKillTheKing(Game game){
        super(game);
    }

    @Override
    public void initGame() {
        game.setArena(new SquareArena());
        final int teamsNumber = 2;
        final int teamSize = 4;
        game.setTeamsNumber(teamsNumber);
        game.setTeamSize(teamSize);
        buildAgents();
        for (int i = 0; i < 2; i++) {
            game.getAgent(i*teamSize).setType(AgentType.King);
            game.getAgent(i*teamSize).setGroup(2);
        }
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 6; j++) {
                game.addKillingPoint(new DetachedKillingPoint(i, new Position(0.5*(i*2-1), (0.95-0.15*j)*(i*2-1)), game));
            }
        }
    }

    @Override
    public void initRound() {
        buildAttachedKillingPoints();
        int agentIndex = 0;
        for (int i = -1; i <= 1; i+=2) {
            game.getAgent(agentIndex++).init(new Position(i*0.75, i*0.75));
            for (int j = 0; j <= 2; j++) {
                game.getAgent(agentIndex++).init(new Position(i*0.5, -i*j*0.4));
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
            if (agent.isKing() && ! agent.isAlive()) scoreIncrease[1-agent.getTeam()] += 1;
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
