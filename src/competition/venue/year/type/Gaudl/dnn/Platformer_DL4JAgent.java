package competition.venue.year.type.Gaudl.dnn;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.encog.neural.networks.BasicNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.platformer.agents.Agent;
import org.platformer.benchmark.platform.engine.sprites.Plumber;
import org.platformer.benchmark.platform.environments.Environment;

public class Platformer_DL4JAgent implements Agent {

    String agentName;
    MultiLayerNetwork network;
    int perceptSize = 0;
    boolean [] actions = new boolean[0];
    INDArray input;


    public Platformer_DL4JAgent(MultiLayerNetwork network) {
        this.network = network;
        perceptSize = 19;
        input = Nd4j.zeros(361);
    }

    int jumping =5;
    double lastJumpAct = 1;
    @Override
    public boolean[] getAction() {
        INDArray result = null;
        boolean [] actions = new boolean[6];//NOTE: This is the standard length for the platformerAI control array

        result = network.output(input);

        //network.compute(this.input,result);

        int top1 =  0;
        int top2 = 1;
        for (int i= 1;i<result.length();i++)
            if (result.getFloat(i) > result.getFloat(top1)) {
                top2 = top1;
                top1 = i;
            }
        for (int i = 2;i<result.length();i++)
            if (result.getFloat(i) > result.getFloat(top2))
                top2 = i;

       // if (result.getFloat(top1) > 0.5)
        //    actions[top1] = true;
       // if (result.getFloat(top2) > 0.5)
         //   actions[top2] = true;
        for (int i=0;i<actions.length;i++)
            actions[i] = (result.getFloat(i) > 0.2) ? true: false;

        if ((result.getFloat(Plumber.KEY_JUMP) > 0.2  && jumping > 0) || result.getFloat(Plumber.KEY_JUMP) > lastJumpAct) {
            actions[Plumber.KEY_JUMP] = true;
            lastJumpAct = result.getFloat(Plumber.KEY_JUMP);
            jumping--;
        } else {
            actions[Plumber.KEY_JUMP] = false;
            if (jumping < 0)
                jumping = 10;
            else
                jumping--;
        }
        return actions;
    }

    @Override
    public void integrateObservation(Environment environment) {
        byte[][] rawVision = environment.getMergedObservationZZ(1, 0);
        double[] vision = new double[this.perceptSize * this.perceptSize];
        //TODO: check for differences between perceptionsSize and vision array dimensions

        int counter = 0;
        for (int y = 0; y < perceptSize; y++)
            for (int x = 0; x < perceptSize; x++) {
                vision[counter] = new Double(rawVision[y][x]);
                counter++;
            }

        this.input = Nd4j.create(vision);
    }

    @Override
    public void giveIntermediateReward(float intermediateReward) {

    }

    @Override
    public void reset() {

    }

    @Override
    public void setObservationDetails(int rfWidth, int rfHeight, int egoRow, int egoCol) {

    }

    @Override
    public String getName() {
        return agentName;
    }

    @Override
    public void setName(String name) {
        agentName = name;

    }
}
