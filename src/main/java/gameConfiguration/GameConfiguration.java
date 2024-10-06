package gameConfiguration;

import game.Agent;
import game.AttachedKillingPoint;
import game.Game;
import game.KillingPoint;

import java.util.Vector;

public abstract class GameConfiguration {
    protected Game game;
    public GameConfiguration(Game game){
        this.game = game;
    }
    public abstract void initGame();
    public abstract void initRound();
    public abstract void checkEndRound();

    public void buildAgents(){
        Vector<Agent> agents = new Vector<>();
        final int teamsNumber = game.getTeamsNumber();
        final int teamSize = game.getTeamSize();
        for (int i = 0; i < teamsNumber; i++) {
            for (int j = 0; j < teamSize; j++) {
                Agent a = new Agent(game, i, 0);
                agents.add(a);
            }
        }
        game.setAgents(agents);
    }
    public void buildAttachedKillingPoints(){
        Agent agent1;
        Agent agent2;
        game.removeAttachedKp();
        final int agentsNumber = game.getAgentsNumber();
        for (int i = 0; i < agentsNumber; i++) {
            agent1 = game.getAgent(i);
            for (int j = i+1; j < agentsNumber; j++) {
                agent2 = game.getAgent(j);
                if (agent1.getTeam() == agent2.getTeam() && agent1.getGroup() == agent2.getGroup()) {
                    game.addKillingPoint(new AttachedKillingPoint(agent1, agent2));
                }
            }
        }
    }
}
