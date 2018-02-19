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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;
import com.owlike.genson.reflect.VisibilityFilter;
import competition.venue.year.type.Gaudl.nn.MarioDataGenerator;
import competition.venue.year.type.Gaudl.nn.NetworkConfiguration;
import competition.venue.year.type.Gaudl.nn.Platformer_NNAgent;
import org.apache.log4j.Logger;
import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.CalculateScore;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.ml.train.strategy.Greedy;
import org.encog.ml.train.strategy.HybridStrategy;
import org.encog.ml.train.strategy.StopTrainingStrategy;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.TrainingSetScore;
import org.encog.neural.networks.training.anneal.NeuralSimulatedAnnealing;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.neural.pattern.BoltzmannPattern;
import org.encog.neural.pattern.ElmanPattern;
import org.encog.neural.pattern.FeedForwardPattern;
import org.encog.neural.pattern.SVMPattern;
import org.platformer.agents.Agent;
import org.platformer.benchmark.platform.engine.Replayer;
import org.platformer.benchmark.tasks.BasicTask;
import org.platformer.benchmark.tasks.GamePlayTask;
import org.platformer.tools.PlatformerAIOptions;
import org.platformer.utils.Configuration;
import org.platformer.utils.ParameterContainer;

import java.io.*;
import java.lang.reflect.Modifier;
import java.util.*;

public final class ANNSystemStandAlone
{
    MarioDataGenerator temp ;
    MLDataSet trainingSet;
    private transient Logger LOGGER;
    private transient Genson jsonSerialiser;
    Map<BasicNetwork,Double []> networks;


public ANNSystemStandAlone() {
    networks = new HashMap<>();
    jsonSerialiser = new GensonBuilder()
            .useClassMetadata(true)
            .useMethods(false)
            .setSkipNull(true)
            .useFields(true, new VisibilityFilter(Modifier.TRANSIENT,Modifier.STATIC))
            .useClassMetadataWithStaticType(false)
            .create();
    BasicNetwork network = createElmanNetwork();
    network.setProperty("Networktype","Elman");
    networks.put(network,new Double[]{1.0,0.0}); // network:error:generations
    network = createFeedforwardNetwork();
    network.setProperty("Networktype","Feedforward");
    networks.put(network,new Double[]{1.0,0.0});// network:error:generations
}
 public void train() {
     temp = new MarioDataGenerator();
     trainingSet = temp.generate(800);

    Iterator<BasicNetwork> networkIterator = networks.keySet().iterator();
     BasicNetwork elman = networkIterator.next();
     BasicNetwork feedforward = networkIterator.next();

     double elmanError = trainNetwork("Elman", elman,
            trainingSet);
     NetworkConfiguration config = exportNetwork(elman,networks.get(elman)[0],
             networks.get(elman)[1].intValue(),"Elman");
     write("Elman.txt",config);
    double feedforwardError = trainNetwork("Feedforward",
            feedforward, trainingSet);
     config = exportNetwork(feedforward,networks.get(feedforward)[0],
             networks.get(feedforward)[1].intValue(),"Feedforward");
     write("FeedForward.txt",config);

 }

    private BasicNetwork createElmanNetwork() {
		// construct an Elman type network
		ElmanPattern pattern = new ElmanPattern();
		pattern.setActivationFunction(new ActivationSigmoid());
		pattern.setInputNeurons(361); // input is based on a 19x19 input vision array
		pattern.addHiddenLayer(60);
		pattern.setOutputNeurons(6); // output is in the form of 6 buttons on the controller
        return (BasicNetwork)pattern.generate();
	}

	private BasicNetwork createFeedforwardNetwork() {
		// construct a feedforward type network
		FeedForwardPattern pattern = new FeedForwardPattern();
		pattern.setActivationFunction(new ActivationSigmoid());
		pattern.setInputNeurons(361); // input is based on a 19x19 input vision array
		pattern.addHiddenLayer(60);
		pattern.setOutputNeurons(6); // output is in the form of 6 buttons on the controller
		return (BasicNetwork)pattern.generate();
	}

	public NetworkConfiguration exportNetwork(BasicNetwork network,double error,int epochCount,String name) {
        NetworkConfiguration config = new NetworkConfiguration();

        int layerCount = network.getLayerCount();
        ArrayList<Map<List<Integer>,Double>>  weights = new ArrayList<>();
        double [] biasedActivation = new double[layerCount];
        boolean [] biasedLayers = new boolean[layerCount];

        for (int i=0;i<layerCount-1;i++) {
            Map<List<Integer>,Double> layer = new HashMap<>();
            weights.add(i,layer);
            int bias = 0;
            if (network.isLayerBiased(i)) {
                bias = 1;
                biasedActivation[i] = network.getLayerBiasActivation(i);
                biasedLayers[i] = network.isLayerBiased(i);
            }

            for (int x=0;x<network.getLayerNeuronCount(i)+bias;x++) {
                for (int y = 0; y<network.getLayerNeuronCount(i+1);y++) {
                    List<Integer> key = new ArrayList<>();
                    key.add(0, x);
                    key.add(1,y);
                    String keyString  = key.toString();
                    if (network.isConnected(i,x,y) && !layer.containsKey(key)) {
                        layer.put(key,network.getWeight(i,x,y));
                    }
                }

            }
        }
        config.setWeights(weights);
        config.finalErrorRate = error;
        config.trainingEpochs = epochCount;
        config.layers = network.getLayerCount();
        config.networkType = network.getFactoryArchitecture();
        config.networkName = name;
        config.biasedLayers = biasedLayers;
        config.biasedActivation = biasedActivation;
        return config;
    }

    public BasicNetwork loadNetwork(NetworkConfiguration config) {
        BasicNetwork network = null;

        switch (config.networkName) {
            case "Elman":
                network = createElmanNetwork();
                break;
            case "Feedforward":
                network = createFeedforwardNetwork();
            default:
                break;
        }

        int layerCount = network.getLayerCount();
         if (layerCount != config.layers)
             return network;

        List<Map<List<Integer>,Double>> weights = config.getWeights();
        for (int i=0;i<layerCount-1;i++) {

            int bias = 0;
            if (config.biasedLayers[i]) {
                bias = 1;
                network.setLayerBiasActivation(i,config.biasedActivation[i]);
            }

            for (List<Integer> neuronpairs : weights.get(i).keySet()) {
                network.enableConnection(i,neuronpairs.get(0),neuronpairs.get(1),true);
                network.setWeight(i,neuronpairs.get(0),neuronpairs.get(1),weights.get(i).get(neuronpairs));
            }
        }

        return network;
    }

    public boolean playLevel(Agent agent, String baseLevel,int levelDelta, int difficultyDelta) {
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
            options.setLevelRandSeed(options.getLevelRandSeed()+levelDelta);
            options.setLevelDifficulty(options.getLevelDifficulty()+difficultyDelta);
            options.setFPS(fps);

            agent.reset();

            task = new BasicTask(options);
            task.runSingleEpisode(1);
            while (!task.isFinished())
                Thread.sleep(50);
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

    private NetworkConfiguration readNetworkFromFile(String fileName){
        String loc = "";
        NetworkConfiguration network = new NetworkConfiguration();
        try {
            loc = System.getProperty("user.dir")+File.separator+fileName;
            BufferedReader reader = new BufferedReader(new FileReader(loc));
            String json = "";
            while (reader.ready())
                json += reader.readLine()+"\n";
            network = jsonSerialiser.deserialize(json,NetworkConfiguration.class);


        } catch (IOException e) {
            System.err.println("Error: unable to read "+loc);
        }

        return  network;
    }

    public void write(String fileName,NetworkConfiguration networkDescr){
        try {
            Writer writer = new BufferedWriter(new FileWriter(System.getProperty("user.dir")+File.separator+fileName));
            String configuration = jsonSerialiser.serialize(networkDescr);
            writer.write(configuration);
            writer.flush();
            writer.close();

        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public double trainNetwork(final String what,
                                      final BasicNetwork network, final MLDataSet trainingSet) {
        // train the neural network
        CalculateScore score = new TrainingSetScore(trainingSet);
        final MLTrain trainAlt = new NeuralSimulatedAnnealing(
                network, score, 10, 2, 100);

        final MLTrain trainMain = new Backpropagation(network, trainingSet,0.000001, 0.0);

        final StopTrainingStrategy stop = new StopTrainingStrategy();
        trainMain.addStrategy(new Greedy());
        trainMain.addStrategy(new HybridStrategy(trainAlt));
        trainMain.addStrategy(stop);

        int epoch = 0;
        while (!stop.shouldStop()) {
            trainMain.iteration();
            System.out.println("Training " + what + ", Epoch #" + epoch
                    + " Error:" + trainMain.getError());
            epoch++;
        }
        networks.put(network,new Double[]{trainMain.getError(),new Double(epoch)});
        return trainMain.getError();
    }

    public void play(){
        NetworkConfiguration nwConfig = readNetworkFromFile("Elman.txt");
        BasicNetwork nw = loadNetwork(nwConfig);
        Platformer_NNAgent agent = new Platformer_NNAgent(nw);
        playLevel(agent,MarioDataGenerator.recordedGames[0],0,0);
        playLevel(agent,MarioDataGenerator.recordedGames[0],1,0);

    }

    public void readAgents(){
        try {
            int counter = 0;
            File folder = new File("solution");
            if (!folder.exists() || !folder.isDirectory()){
                System.err.println("JGAP folder for loading chromosomes not found");
                return;
            }
            String [] solutionFiles = folder.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (name.startsWith("solutions") && name.endsWith(".txt"))
                        return true;
                    return false;
                }
            });
            File last = new File(solutionFiles[solutionFiles.length-1]);

            BufferedReader reader = new BufferedReader(new FileReader(last));
            jsonSerialiser = new GensonBuilder()
                    .useClassMetadata(true)
                    .useMethods(false)
                    .setSkipNull(true)
                    .useFields(true, new VisibilityFilter(Modifier.TRANSIENT,Modifier.STATIC))
                    .useClassMetadataWithStaticType(false)
                    .create();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


	public static void main(final String args[]) {

		ANNSystemStandAlone system = new ANNSystemStandAlone();

      //  system.play();

        system.train();




		Encog.getInstance().shutdown();
		System.exit(0);
	}
}
