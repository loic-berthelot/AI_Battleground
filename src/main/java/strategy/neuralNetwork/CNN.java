package strategy.neuralNetwork;

import org.deeplearning4j.nn.api.NeuralNetwork;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.nd4j.linalg.api.ndarray.INDArray;
import strategy.LearningBatch;

public class CNN extends NN{
    /**
     * Crée un réseau CNN adapté pour l'agent contrôlé.
     * @param height hauteur de la carte
     * @param width largeur de la carte
     * @param numChannels nombre de canaux d'entrée (ex: 6)
     * @param numActions nombre d'actions (ex: 9)
     * @param learningRate taux d'apprentissage
     */
    private final int width;
    private final int height;
    private final int numChannels;
    public CNN(int width, int height, int numChannels, int numActions, double learningRate) {
        this.width = width;
        this.height = height;
        this.numChannels = numChannels;
        numInputs = height*width*numChannels;
        numOutputs = numActions;
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(1234)
                .weightInit(WeightInit.XAVIER)
                .updater(new Adam(learningRate))
                .l2(1e-4)
                .gradientNormalization(GradientNormalization.RenormalizeL2PerLayer)
                .list()
                .layer(0, new ConvolutionLayer.Builder(3,3)
                        .nIn(numChannels)
                        .nOut(16)
                        .stride(1,1)
                        .activation(Activation.RELU)
                        .build())
                .layer(1, new ConvolutionLayer.Builder(3,3)
                        .nOut(32)
                        .stride(1,1)
                        .activation(Activation.RELU)
                        .build())
                .layer(2, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                        .kernelSize(2,2)
                        .stride(2,2)
                        .build())
                .layer(3, new ConvolutionLayer.Builder(3,3)
                        .nOut(64)
                        .stride(1,1)
                        .activation(Activation.RELU)
                        .build())
                .layer(4, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                        .kernelSize(2,2)
                        .stride(2,2)
                        .build())
                .layer(5, new DenseLayer.Builder().nOut(256)
                        .activation(Activation.RELU)
                        .build())
                .layer(6, new DenseLayer.Builder().nOut(128)
                        .activation(Activation.RELU)
                        .build())
                .layer(7, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .activation(Activation.IDENTITY)
                        .nOut(numActions)
                        .build())
                .setInputType(InputType.convolutional(height, width, numChannels))
                //.backprop(true).pretrain(false)
                .build();

        model = new MultiLayerNetwork(conf);
        model.init();
    }

    public void fit(LearningBatch learningBatch)
    {
        INDArray indinputs = Nd4j.create(learningBatch.getStatesFeatures(), new int[]{learningBatch.getSize(), numChannels, width, height});
        INDArray indoutputs = Nd4j.create(learningBatch.getRewards(), new int[]{learningBatch.getSize(), numOutputs});
        DataSet dataset = new DataSet(indinputs, indoutputs);
        for(int i = 0; i<learningBatch.getEpochsNumber(); i++ ){
            model.fit(dataset);
        }
        //System.out.println("poids : "+network.getLayer(1).getParam("W").getDouble(0));
    }

    public double[] predict (double[] features) {
        INDArray input = Nd4j.create(features, new int[]{1,numChannels, width, height});
        INDArray out = model.output(input);
        return out.toDoubleVector();
    }

    public void trainWithIterator(org.nd4j.linalg.dataset.api.iterator.DataSetIterator iterator, int numEpochs) {
        for (int i = 0; i < numEpochs; i++) {
            model.fit(iterator);
        }
    }

    public NeuralNetwork getModel() {
        return model;
    }
}
