package strategy;

public class LearningBatch {
    private double[] statesFeatures;
    private double[] rewards;
    private int size;
    private int epochsNumber;

    public LearningBatch(double[] statesFeatures, double[] rewards, int size, int epochsNumber) {
        this.statesFeatures = statesFeatures;
        this.rewards = rewards;
        this.size = size;
        this.epochsNumber = epochsNumber;
    }

    public double[] getStatesFeatures() {
        return statesFeatures;
    }

    public double[] getRewards() {
        return rewards;
    }

    public int getSize() {
        return size;
    }

    public int getEpochsNumber() {
        return epochsNumber;
    }
}
