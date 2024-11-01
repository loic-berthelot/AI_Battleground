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

import static java.lang.Double.NaN;

public class NNdl4j {
    private final MultiLayerNetwork network;
    private final int numInputs;
    private final int numOutputs;
    private double learningRate;
    final private double minOutInterval;
    final private double maxOutInterval;
    final private int strechIntensity;
    final private int strechMode;
    public NNdl4j(double learningRate, int seed, int numInputs, int numOutputs){
        this.numInputs = numInputs;
        this.numOutputs = numOutputs;
        this.learningRate = learningRate;
        int numHidden = 8;
        minOutInterval = 0;
        maxOutInterval = 1;
        strechIntensity = 1500;
        strechMode = 1;
        network = new MultiLayerNetwork(new NeuralNetConfiguration.Builder()
                .seed(seed)
                .weightInit(WeightInit.XAVIER)
                .updater(new Adam(learningRate))
                .list()
                .layer(0, new DenseLayer.Builder().nIn(numInputs).nOut(numHidden)
                        .activation(Activation.SIGMOID)
                        .dropOut(0.3)
                        .l2(1e-4)
                        .build())
                .layer(1, new DenseLayer.Builder().nIn(numHidden).nOut(numHidden)
                        .activation(Activation.SIGMOID)
                        .dropOut(0.3)
                        .l2(1e-4)
                        .build())
                .layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .activation(Activation.SIGMOID)
                        .nIn(numHidden).nOut(numOutputs).build())
                .build()
        );
        network.init();
    }
    public void fit(LearningBatch learningBatch)
    {
        INDArray indinputs = Nd4j.create(learningBatch.getStatesFeatures(), new int[]{learningBatch.getSize(), numInputs});
        INDArray indoutputs = Nd4j.create(learningBatch.getRewards(), new int[]{learningBatch.getSize(), numOutputs});
        DataSet dataset = new DataSet(indinputs, indoutputs);
        for(int i = 0; i<learningBatch.getEpochsNumber(); i++ ){
            network.fit(dataset);
        }
        //System.out.println("poids : "+network.getLayer(1).getParam("W").getDouble(0));
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

    //y = 2*atan(power*x)/pi => x = tan(y*pi/2)/power
    public double stretch(double value, boolean extend){
        switch(strechMode){
            case 0 : {
                return Math.signum(value) * Math.pow(Math.abs(value), (extend ? 1/(double) strechIntensity : strechIntensity));
            }
            case 1:
            default:{
                if (extend){
                    return 2*Math.atan(strechIntensity*value)/Math.PI;
                } else {
                    return Math.tan(value*Math.PI/2)/strechIntensity;
                }
            }
        }
    }
//a;b  => c;d
//(x-a)*(d-c)/(b-a)+c
    //min;max => -1;1
//(x-min) * 2/(max-min)-1

//-1;1 => min;max
//(x+1) * (max-min)/2 + min
    public double outToArenaInterval(double value) {
        value = 2*(value-minOutInterval)/(maxOutInterval-minOutInterval)-1;
        value = stretch(value, true);
        return value;
    }
    public double arenaToOutInterval(double value) {
        value = stretch(value, false);
        value = (value+1)*(maxOutInterval-minOutInterval)/2 + minOutInterval;
        return value;
    }
}
