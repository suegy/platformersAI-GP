package competition.venue.year.type.Gaudl.nn;

import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.platformer.benchmark.tasks.GeneratorTask;

import java.util.HashMap;

public class MarioDataGenerator {

    /**
     * 1 xor 0 = 1, 0 xor 0 = 0, 0 xor 1 = 1, 1 xor 1 = 0
     */
    public static final double[] SEQUENCE = { 1.0, 0.0, 1.0, 0.0, 0.0, 0.0,
            0.0, 1.0, 1.0, 1.0, 1.0, 0.0 };
    public static String[] recordedGames = {"run0","run1","run2","run3","run4","run5","run6","run7","run8","run9"};

    private double[][][] input;
    private double[][] ideal;

    public MLDataSet generate(final int count) {
        GeneratorTask replayTask = new GeneratorTask();

        Integer [][][] output = new Integer[recordedGames.length][][];
        Integer [][][][] input = new Integer[recordedGames.length][][][];
        for (int i=0;i<recordedGames.length;i++) {
            replayTask.reset("nn-run/" + recordedGames[i]);
            replayTask.startReplay(200);
            input[i] = replayTask.getInputData();
            output[i] = replayTask.getOutputData();
        }
        int minOccurence = calculateDistribution(output);
        this.input = new double[count][1][];
        this.ideal = new double[count][1];

        for (int i = 0; i < this.input.length; i++) {
            this.input[i][0] = MarioDataGenerator.SEQUENCE[i
                    % MarioDataGenerator.SEQUENCE.length];
            this.ideal[i][0] = MarioDataGenerator.SEQUENCE[(i + 1)
                    % MarioDataGenerator.SEQUENCE.length];
        }

        return new BasicMLDataSet(this.input, this.ideal);
    }

    private int calculateDistribution(Integer [][][] data){
        int occurence = 0;
        HashMap<Integer [],Integer> count = new HashMap<>();

        for (int i=0;i<data.length;i++) // going over all data sets
            for (int j=0;j<data[i].length;j++) { // going over all frames
                if (count.containsKey(data[i][j])) {
                    count.put(data[i][j], count.get(data[i][j]) + 1);
                } else {
                    count.put(data[i][j], 1);
                }
            }
        count.values().
        return occurence;
    }
}
