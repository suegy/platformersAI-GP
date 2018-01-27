package competition.venue.year.type.Gaudl.nn;

import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.platformer.benchmark.tasks.GeneratorTask;

import java.util.*;

public class MarioDataGenerator {

    /**
     * 1 xor 0 = 1, 0 xor 0 = 0, 0 xor 1 = 1, 1 xor 1 = 0
     */
    public static final double[] SEQUENCE = { 1.0, 0.0, 1.0, 0.0, 0.0, 0.0,
            0.0, 1.0, 1.0, 1.0, 1.0, 0.0 };
    public static String[] recordedGames = {"run0","run1"};

    private double[][] input; // [frame:row]
    private double[][] ideal;//  [frame:buttons]

    public MLDataSet generate(final int count) {
        GeneratorTask replayTask = null;

        int [][][] output = new int[recordedGames.length][][];
        int [][][][] input = new int[recordedGames.length][][][];

        for (int i=0;i<recordedGames.length;i++) {
            replayTask = new GeneratorTask();
            replayTask.reset(recordedGames[i]);
            replayTask.startReplay(200);
            input[i] = replayTask.getInputData();
            output[i] = replayTask.getOutputData();
            replayTask.delete();
        }

        Map<List<Integer>,Integer> idealDistribution = calculateDistribution(output);
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
        List<Integer> [] ideals = new ArrayList[1];
        ideals  = idealDistribution.keySet().toArray(ideals);

        if (count < idealDistribution.size()) { // dataset not able to fully represent data

            for (int i=0;i<input.length;i++) { //selecting the most often occurring pairs first when filling the data set
                this.ideal[i] = convertIntToDouble(ideals[i]);
                this.input[i] = getDataPoint(ideals[i],unfilteredInput,unfilderedIdeal,true);
            }

        } else if (count <= min*idealDistribution.size()) { // can represent dataset

            int max = 0;
            while (max < count) {
                for (int i=0;i<idealDistribution.size();i++) { //selecting the most often occurring pairs first when filling the data set
                    this.ideal[i] = convertIntToDouble(ideals[i]);
                    this.input[i] = getDataPoint(ideals[i],unfilteredInput,unfilderedIdeal,true);
                    max ++;
                }
            }

        } else {

            ArrayList<Double[]>  _unfilteredInput = (ArrayList<Double[]>)  unfilteredInput.clone();
            ArrayList<Integer[]> _unfilderedIdeal = (ArrayList<Integer[]>) unfilderedIdeal.clone();
            int inSize = 0;
            Random rand = new Random();
            while (inSize < count) {
                int max = 0;
                while (max < min) {
                    if (inSize+max >= count)
                        break;
                    for (int i = 0; i < idealDistribution.size(); i++) { //selecting the most often occurring pairs first when filling the data set
                        this.ideal[i] = convertIntToDouble(ideals[i]);
                        this.input[i] = getDataPoint(ideals[i], unfilteredInput, unfilderedIdeal, true);

                        if (this.input[i].length < 1 ) {
                            /**
                             * now we need to add back pairs to draw from as the underrepresented ones are gone
                             * The simplest but not optimal way is to refill the entire set again;
                             *
                             * a better way
                             * would be to mix them up
                             */
                            this.input[i] = getDataPoint(ideals[i], _unfilteredInput, _unfilderedIdeal, false);

                        }
                        max++;
                    }
                }
                inSize += max;



            }
        }

        return new BasicMLDataSet(this.input, this.ideal);
    }

    private double [] getDataPoint(List<Integer> idealPoint,ArrayList<Double[]>unfilteredInput, ArrayList<Integer[]> unfilderedIdeal,boolean pop){
        double [] result = {};

        for (int i =0;i<unfilderedIdeal.size();i++) {
            Integer[] point = unfilderedIdeal.get(i);
            boolean identical = true;
            for (int j = 0; j < point.length; j++)
                if (point[j] != idealPoint.get(j)) {
                    identical = false;
                    break;
                }
            if (identical) {
                Double[] res = unfilteredInput.get(i);
                if (pop) {
                    unfilteredInput.remove(i);
                    unfilderedIdeal.remove(i);
                }
                result = new double[res.length];
                for (int j = 0; j < res.length; j++)
                    result[j] = res[j];
                break;
            }
        }
        return result;
    }

    private double [] convertIntToDouble (List<Integer> values) {
        double [] result = new double[values.size()];

        for (int i=0;i<values.size();i++)
            result[i] = values.get(i);

        return result;
    }

    public List<Integer> convertArrayToList(int [] data) {
        ArrayList<Integer> list = new ArrayList<>();

        for (int i=0;i<data.length;i++)
            list.add(data[i]);

        return list;
    }

    private Map<List<Integer>,Integer> calculateDistribution(int [][][] data){
        HashMap<List<Integer>,Integer> count = new HashMap<>();

        for (int i=0;i<data.length;i++) // going over all data sets
            for (int j=0;j<data[i].length;j++) { // going over all frames
                List<Integer> key = convertArrayToList(data[i][j]);
                if (count.containsKey(key)) {
                    count.put(key, count.get(key) + 1);
                } else {
                    count.put(key, 1);
                }
            }

        return count;
    }
}
