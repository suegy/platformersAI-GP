package competition.venue.year.type.Gaudl.nn;

import org.encog.neural.networks.BasicNetwork;
import org.platformer.agents.Agent;
import org.platformer.benchmark.platform.environments.Environment;

import java.util.ArrayList;

public class Platformer_NNAgent implements Agent {

    String agentName;
    BasicNetwork network;
    int perceptSize = 0;
    boolean [] actions = new boolean[0];
    double [] input;


    public Platformer_NNAgent(BasicNetwork network) {
        this.network = network;
        perceptSize = (int)Math.sqrt(network.getInputCount());
        input = new double[network.getInputCount()];
    }

    @Override
    public boolean[] getAction() {
        double [] result = new double[network.getOutputCount()];
        boolean [] actions = new boolean[6];//NOTE: This is the standard length for the platformerAI control array
        network.compute(this.input,result);

        int top1 =  0;
        int top2 = 1;
        for (int i= 1;i<result.length;i++)
            if (result[i] > result[top1]) {
                top2 = top1;
                top1 = i;
            }
        for (int i = 2;i<result.length;i++)
            if (result[i] > result[top2])
                top2 = i;

        if (result[top1] > 0.5)
            actions[top1] = true;
        if (result[top2] > 0.5)
            actions[top2] = true;
       // for (int i=0;i<actions.length;i++)
       //     actions[i] = (result[i] < 0.5) ? false : true;

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

        this.input = vision;
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
