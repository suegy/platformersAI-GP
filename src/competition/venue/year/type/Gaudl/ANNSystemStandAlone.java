/*
 * Copyright (c) 2009-2010, Sergey Karakovskiy and Julian Togelius
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Mario AI nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package competition.venue.year.type.Gaudl;

import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;
import com.owlike.genson.reflect.VisibilityFilter;
import competition.venue.year.type.Gaudl.dnn.MarioDLDataGenerator;
import competition.venue.year.type.Gaudl.dnn.Platformer_DL4JAgent;
import competition.venue.year.type.Gaudl.nn.MarioDataGenerator;
import org.apache.log4j.*;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.GravesLSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.platformer.agents.Agent;
import org.platformer.benchmark.platform.engine.Replayer;
import org.platformer.benchmark.tasks.BasicTask;
import org.platformer.tools.PlatformerAIOptions;


import java.io.*;
import java.lang.reflect.Modifier;
import java.util.*;

public final class ANNSystemStandAlone {

    private transient Logger LOGGER;
    private transient Genson jsonSerialiser;
    Map<MultiLayerNetwork, Double[]> networks;
    // RNN dimensions
    private static final int HIDDEN_LAYER_WIDTH = 50;
    private static final int HIDDEN_LAYER_CONT = 2;
    private static final Random r = new Random(7894);
    NeuralNetConfiguration.Builder builder;
    NeuralNetConfiguration.ListBuilder listBuilder;


    public ANNSystemStandAlone() {
        networks = new HashMap<>();
        LOGGER = Logger.getRootLogger();
        try {
            LOGGER.setLevel(Level.INFO);
            PatternLayout layout = new PatternLayout();
            layout.setConversionPattern("%d %p - %m%n");
            RollingFileAppender logger = new RollingFileAppender(layout, "mario-dnn.log");
            logger.setMaxBackupIndex(10);
            logger.setMaxFileSize("100MB");
            LOGGER.addAppender(logger);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        jsonSerialiser = new GensonBuilder()
                .useClassMetadata(true)
                .useMethods(false)
                .setSkipNull(true)
                .useFields(true, new VisibilityFilter(Modifier.TRANSIENT, Modifier.STATIC))
                .useClassMetadataWithStaticType(false)
                .create();

        // some common parameters
        builder = new NeuralNetConfiguration.Builder();
        builder.iterations(10);
        builder.learningRate(0.001);
        builder.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT);
        builder.seed(123);
        builder.biasInit(0);
        builder.miniBatch(false);
        builder.updater(Updater.RMSPROP);
        builder.weightInit(WeightInit.XAVIER);

        listBuilder = builder.list();

    }

    private MultiLayerNetwork createDeepNetwork() {

        for (int i = 0; i < HIDDEN_LAYER_CONT; i++) {
            GravesLSTM.Builder hiddenLayerBuilder = new GravesLSTM.Builder();
            hiddenLayerBuilder.nIn(i == 0 ? 361 : HIDDEN_LAYER_WIDTH); //361 is the size of the 19x19 input array from the platformersAI
            hiddenLayerBuilder.nOut(HIDDEN_LAYER_WIDTH);
            // adopted activation function from GravesLSTMCharModellingExample
            // seems to work well with RNNs
            hiddenLayerBuilder.activation(Activation.TANH);
            listBuilder.layer(i, hiddenLayerBuilder.build());
        }

        // we need to use RnnOutputLayer for our RNN
        RnnOutputLayer.Builder outputLayerBuilder = new RnnOutputLayer.Builder(LossFunctions.LossFunction.MCXENT);
        // softmax normalizes the output neurons, the sum of all outputs is 1
        // this is required for our sampleFromDistribution-function
        outputLayerBuilder.activation(Activation.SOFTMAX);
        outputLayerBuilder.nIn(HIDDEN_LAYER_WIDTH);
        outputLayerBuilder.nOut(6); //6 is the size of the out array from the platformersAI
        listBuilder.layer(HIDDEN_LAYER_CONT, outputLayerBuilder.build());

        // finish builder
        listBuilder.pretrain(false);
        listBuilder.backprop(true);

        // create network
        MultiLayerConfiguration conf = listBuilder.build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        net.setListeners(new ScoreIterationListener(1));
        LOGGER.info("finished building network: "+net.conf().toJson());
        return net;
    }


    public void train() {
        DataSetIterator train = new MarioDLDataGenerator(12,15000,0,10);
        DataSetIterator test = new MarioDLDataGenerator(12,15000,10,19);

        MultiLayerNetwork net  = createDeepNetwork();
        networks.put(net,new Double[]{1d,0d});

        double error = trainNetwork("Convolutional2", net,
                train, test, 2);
        write("Convolutional2", net);

    }

    public double trainNetwork(final String what,
                               final MultiLayerNetwork network, final DataSetIterator train,
                               final DataSetIterator test, int nEpochs) {

        double error = 1d;
        for (int i = 0; i < nEpochs; i++) {
            LOGGER.info("====================");
            LOGGER.info("Start training Epoch "+ i);
            network.fit(train);
            LOGGER.info("Completed training Epoch "+ i);
            Evaluation eval = network.evaluate(test);
            LOGGER.info(eval.stats());
            error = eval.accuracy();
            LOGGER.info("Network Evaluation accuracy: "+error);
            LOGGER.info("====================");
            train.reset();
            test.reset();
        }
        networks.put(network, new Double[]{error, new Double(nEpochs)});
        return error;
    }

    public boolean playLevel(Agent agent, String baseLevel, int levelDelta, int difficultyDelta,int repetition) {
        PlatformerAIOptions options = new PlatformerAIOptions();
        BasicTask task = null;
        int fps = 25;
        if (baseLevel.length() < 1) { //TODO: need to set parameters for loading a level plat task
            return false;
        }

        Replayer replayer = new Replayer(baseLevel);
        try {
            replayer.openNextReplayFile();
            replayer.openFile("options");
            String strOptions = (String) replayer.readObject();
            //options.setArgs(strOptions);
            //TODO: reset; resetAndSetArgs;
            options.setVisualization(true);
            options.setRecordFile("off");
            options.setAgent(agent);
            options.setLevelRandSeed(options.getLevelRandSeed() + levelDelta);
            options.setLevelDifficulty(options.getLevelDifficulty() + difficultyDelta);
            options.setFPS(fps);

            agent.reset();

            task = new BasicTask(options);
            task.runSingleEpisode(repetition);
            replayer.closeReplayFile();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (task != null)
            return task.isFinished();
        else
            return false;
    }

    private MultiLayerNetwork readNetworkFromFile(String fileName) {
        String loc = "";
        MultiLayerNetwork network = null;
        try {
            String locationToSave = System.getProperty("user.dir") + File.separator + fileName+".zip";

            //Load the model
            network = ModelSerializer.restoreMultiLayerNetwork(locationToSave);

        } catch (IOException e) {
            System.err.println("Error: unable to read " + loc);
        }

        return network;
    }

    public void write(String fileName, MultiLayerNetwork net) {
        try {
            String locationToSave = System.getProperty("user.dir") + File.separator + fileName+".zip";

            boolean saveUpdater = true; //Updater: i.e., the state for Momentum, RMSProp, Adagrad etc. Save this if you want to train your network more in the future
            ModelSerializer.writeModel(net, locationToSave, saveUpdater);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }



    public void play() {
        MultiLayerNetwork playNet;
        if (networks.size() < 1)
            playNet = readNetworkFromFile("Convolutional2");
        else
            playNet = networks.keySet().iterator().next();

        Platformer_DL4JAgent agent = new Platformer_DL4JAgent(playNet);
        playLevel(agent, MarioDataGenerator.recordedGames[0], 1, 0,5);
        playLevel(agent, MarioDataGenerator.recordedGames[0], 1, 1,5);

    }

    public void readAgents() {
        try {
            int counter = 0;
            File folder = new File("solution");
            if (!folder.exists() || !folder.isDirectory()) {
                System.err.println("JGAP folder for loading chromosomes not found");
                return;
            }
            String[] solutionFiles = folder.list((dir, name) -> {
                if (name.startsWith("solutions") && name.endsWith(".txt"))
                    return true;
                return false;
            });
            File last = new File(solutionFiles[solutionFiles.length - 1]);

            BufferedReader reader = new BufferedReader(new FileReader(last));
            //jsonSerialiser.deserialize(reader,);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public static void main(final String args[]) {

        ANNSystemStandAlone system = new ANNSystemStandAlone();

        //system.play();

        system.train();



        System.exit(0);
    }
}
