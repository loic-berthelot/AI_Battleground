package strategy.neuralNetwork;

import org.deeplearning4j.nn.api.NeuralNetwork;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

public abstract class NN {
    protected int numInputs;
    protected int numOutputs;
    protected MultiLayerNetwork model;
    public void setLearningRate(double learningRate) {
        model.setLearningRate(learningRate);
    }
}
