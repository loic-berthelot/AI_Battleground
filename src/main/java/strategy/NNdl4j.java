package strategy;

import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.util.Random;

public class NNdl4j {
    private final MultiLayerNetwork network;
    private final int numInputs;
    private final int numOutputs;
    private double learningRate;
    public NNdl4j(double learningRate, int seed, int numInputs, int numOutputs){
        this.numInputs = numInputs;
        this.numOutputs = numOutputs;
        int numHidden = 16;
        network = new MultiLayerNetwork(new NeuralNetConfiguration.Builder()
                .seed(seed)
                .weightInit(WeightInit.XAVIER)
                .updater(new Adam(learningRate))
                .dropOut(0.15)
                .list()
                .layer(0, new DenseLayer.Builder().nIn(numInputs).nOut(numHidden)
                        .activation(Activation.SIGMOID)
                        .build())
                .layer(1, new DenseLayer.Builder().nIn(numHidden).nOut(numHidden)
                        .activation(Activation.SIGMOID)
                        .build())
                .layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .activation(Activation.SIGMOID)
                        .nIn(numHidden).nOut(numOutputs).build())
                .build()
        );
        network.init();
    }
    public void fit(double[] inputs, double[] outputs, int size, int nEpochs)
    {
        INDArray indinputs = Nd4j.create(inputs, new int[]{size, numInputs});
        INDArray indoutputs = Nd4j.create(outputs, new int[]{size, numOutputs});
        DataSet dataset = new DataSet(indinputs, indoutputs);
        for( int i=0; i<nEpochs; i++ ){
            network.fit(dataset);
        }
    }
    public double[] predict(double[] features){
        INDArray input = Nd4j.create(features, new int[]{1,numInputs});
        INDArray out = network.output(input);
        return out.toDoubleVector();
    }
    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
        network.setLearningRate(learningRate);
    }
}
