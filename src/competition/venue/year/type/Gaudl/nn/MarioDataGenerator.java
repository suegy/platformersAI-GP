package competition.venue.year.type.Gaudl.nn;

import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.platformer.benchmark.tasks.GeneratorTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MarioDataGenerator {

    /**
     * 1 xor 0 = 1, 0 xor 0 = 0, 0 xor 1 = 1, 1 xor 1 = 0
     */
    public static final double[] SEQUENCE = { 1.0, 0.0, 1.0, 0.0, 0.0, 0.0,
            0.0, 1.0, 1.0, 1.0, 1.0, 0.0 };
    public static String[] recordedGames = {"run0","run1","run2","run3","run4","run5","run6","run7","run8","run9"};

    private double[][] input; // [frame:row]
    private double[][] ideal;//  [frame:buttons]

    public MLDataSet generate(final int count) {
        GeneratorTask replayTask = new GeneratorTask();

        int [][][] output = new int[recordedGames.length][][];
        int [][][][] input = new int[recordedGames.length][][][];

        for (int i=0;i<recordedGames.length;i++) {
            replayTask.reset("nn-run/" + recordedGames[i]);
            replayTask.startReplay(200);
            input[i] = replayTask.getInputData();
            output[i] = replayTask.getOutputData();
        }

        Map<int[],Integer> idealDistribution = calculateDistribution(output);
        ArrayList<Double[]> unfilteredInput = new ArrayList<>();
        ArrayList<Integer[]> unfilderedIdeal = new ArrayList<>();

        for (int g= 0; g< input.length;g++)
            for (int f = 0; f < input[g].length; f++) {
                ArrayList<Double> visualLines = new ArrayList<>();
                ArrayList<Integer> controlButtons = new ArrayList<>();
                for (int y = 0; y < input[g][f].length; y++) { //concatinating the 2D visual field into a single line
                    for (int x = 0; x < input[g][f][y].length; x++) {
                        visualLines.add((double) input[g][f][y][x]);
                    }
                }
                for (int b = 0; b < output[g][f].length; b++) {
                    controlButtons.add(output[g][f][b]);
                }

                unfilteredInput.add(visualLines.toArray(new Double[1]));
                unfilderedIdeal.add(controlButtons.toArray(new Integer[1]));
            }
        this.input = new double[count][unfilteredInput.get(0).length];
        this.ideal = new double[count][unfilderedIdeal.get(0).length];

        /**
         * balancing the data distribution to enhance dataset
         */
        int median = 0;
        int min = idealDistribution.values().iterator().next();
        Integer [] idealCount = idealDistribution.values().toArray(new Integer[0]);
        for (int i=0;i<idealDistribution.size();i++) {
            median += idealCount[i];
            if (idealCount[i] < min )
                min = idealCount[i];
        }
        median /= idealCount.length;

        if (count < idealDistribution.size()) { // dataset not able to fully represent data


        } else if (count <= min*idealDistribution.size()) { // can represent dataset

        } else {

        }

        return new BasicMLDataSet(this.input, this.ideal);
    }

    private Map<int [],Integer> calculateDistribution(int [][][] data){
        HashMap<int [],Integer> count = new HashMap<>();

        for (int i=0;i<data.length;i++) // going over all data sets
            for (int j=0;j<data[i].length;j++) { // going over all frames
                if (count.containsKey(data[i][j])) {
                    count.put(data[i][j], count.get(data[i][j]) + 1);
                } else {
                    count.put(data[i][j], 1);
                }
            }

        return count;
    }
}
