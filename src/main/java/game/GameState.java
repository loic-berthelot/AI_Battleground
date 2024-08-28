package game;

public class GameState {
    private int agentsNumber;
    private int killingPointsNumber;
    private double[] agentsPosX;
    private double[] agentsPosY;
    private double[] agentsTeams;
    private double[] killingPointPosX;
    private double[] killingPointPosY;
    private double[] killingPointTeams;
    public GameState(int agentsNumber, double[] agentsPosX, double[] agentsPosY, double[] agentsTeams) {
        this.agentsNumber = agentsNumber;
        this.agentsPosX = agentsPosX;
        this.agentsPosY = agentsPosY;
        this.agentsTeams = agentsTeams;
    }
    public void setKillingPointsCoordinates(int killingPointsNumber, double[] killingPointPosX,double[] killingPointPosY,double[] killingPointTeams){
        this.killingPointsNumber = killingPointsNumber;
        this.killingPointPosX = killingPointPosX;
        this.killingPointPosY = killingPointPosY;
        this.killingPointTeams = killingPointTeams;
    }
    public int getAgentsNumber(){
        return agentsNumber;
    }
    public int getKillingPointsNumber(){
        return killingPointsNumber;
    }
    public double[] getAgentsPosX() {
        return agentsPosX;
    }
    public double[] getAgentsPosY() {
        return agentsPosY;
    }
    public double[] getAgentsTeams() {
        return agentsTeams;
    }
    public double[] getKillingPointPosX() {
        return killingPointPosX;
    }
    public double[] getKillingPointPosY() {
        return killingPointPosY;
    }
    public double[] getKillingPointTeams() {
        return killingPointTeams;
    }
    public double getAgentPosX(int agentIndex) {
        return agentsPosX[agentIndex];
    }
    public double getAgentPosY(int agentIndex) {
        return agentsPosY[agentIndex];
    }
    public double getAgentTeam(int agentIndex) {
        return agentsTeams[agentIndex];
    }
    public void setAgentsPosX(double[] agentsPosX) {
        this.agentsPosX = agentsPosX;
    }
    public void setAgentsPosY(double[] agentsPosY) {
        this.agentsPosY = agentsPosY;
    }
    public void setAgentsTeams(double[] agentsTeams) {
        this.agentsTeams = agentsTeams;
    }
    public void setKillingPointPosX(double[] killingPointPosX) {
        this.killingPointPosX = killingPointPosX;
    }
    public void setKillingPointPosY(double[] killingPointPosY) {
        this.killingPointPosY = killingPointPosY;
    }
    public void setKillingPointTeams(double[] killingPointTeams) {
        this.killingPointTeams = killingPointTeams;
    }
}
