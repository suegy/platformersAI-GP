package competition.venue.year.type.Gaudl.dnn;

import competition.venue.year.type.Gaudl.nn.UniquePairs;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.platformer.benchmark.tasks.GeneratorTask;

import java.util.*;

public class MarioDLDataGenerator {

    public static String[] recordedGames = {"run0","run1","run2","run3","run4","run5"};

    private double[][] input; // [frame:row]
    private double[][] ideal;//  [frame:buttons]
    private Random rand = new Random();


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

        //Map<List<Integer>,Integer> idealDistribution = calculateDistribution(output);
        List<List<Double>> unfilteredInput = new ArrayList<>();
        List<List<Integer>> unfilderedIdeal = new ArrayList<>();

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

                unfilteredInput.add(visualLines);
                unfilderedIdeal.add(controlButtons);
            }
        this.input = new double[count][unfilteredInput.get(0).size()];
        this.ideal = new double[count][unfilderedIdeal.get(0).size()];

        /**
         * shuffle unfiltered dataset to reduce impact of sorting
         */
        shuffle(unfilteredInput,unfilderedIdeal);

        /**
         * balancing the data distribution to enhance dataset
         */
        int median = 0;
        UniquePairs datapairs = new UniquePairs(unfilderedIdeal,unfilteredInput);
        int min = datapairs.getDistribution().get(0);

        for (int i=0;i<datapairs.size();i++) {
            median += datapairs.getDistribution().get(i);
            if (datapairs.getDistribution().get(i) < min )
                min = datapairs.getDistribution().get(i);
        }
        median /= datapairs.size();
        //List<Integer> [] ideals = new ArrayList[1];
        //ideals  = idealDistribution.keySet().toArray(ideals);
        System.out.println("DataSet contains "+datapairs.size()+" unique pairs.");
       /* if (count < datapairs.size()) { // dataset not able to fully represent data

            for (int i=0;i<input.length;i++) { //selecting the most often occurring pairs first when filling the data set
                this.ideal[i] = convertIntToDouble(datapairs.getDataA(i));
                this.input[i] = convertDoubleToDouble(datapairs.getDataB(i));
            }

        } else if (count <= min*datapairs.size()) { // can represent dataset

            int max = 0;
            while (max < count) {
                for (int i=0;i<datapairs.size();i++) { //selecting the most often occurring pairs first when filling the data set
                    this.ideal[i] = convertIntToDouble(datapairs.getDataA(i));
                    this.input[i] = convertDoubleToDouble(datapairs.getDataB(i));
                    max ++;
                }
            }

        } else { */
            //FIXME: need to deepClone the lists
            //List<List<Double>>  _unfilteredInput = (List<List<Double>>)  unfilteredInput.clone();
            //List<List<Integer>> _unfilderedIdeal = (List<List<Integer>>) unfilderedIdeal.clone();
            int inSize = 0;

            while (inSize < count) {
                int max = 0;
                while (max < min) {
                    if (inSize+max >= count)
                        break;
                    for (int i = 0; i < Math.min(unfilderedIdeal.size(),count); i++) { //selecting the most often occurring pairs first when filling the data set
                     //   for (int i = 0; i < datapairs.size(); i++) { //selecting the most often occurring pairs first when filling the data set
                        this.ideal[i] = convertIntToDouble(datapairs.getDataA(i));
                        this.input[i] = convertDoubleToDouble(datapairs.getDataB(i));
                        //this.ideal[i] = convertIntToDouble(unfilderedIdeal.get(i));
                        //this.input[i] = convertDoubleToDouble(unfilteredInput.get(i));
                        //if (this.input[i].length < 1 ) {
                            /**
                             * now we need to add back pairs to draw from as the underrepresented ones are gone
                             * The simplest but not optimal way is to refill the entire set again;
                             *
                             * a better way
                             * would be to mix them up
                             */
                           // this.input[i] = getDataPoint(ideals[i], _unfilteredInput, _unfilderedIdeal, false);

                        //}
                        max++;
                    }
                }
                inSize += max;



            }
        //}

        /**
         * shuffle dataset
         */
        //shuffle(this.ideal,this.input);


        return new BasicMLDataSet(this.input, this.ideal);
    }
    public void shuffle(List<List<Double>> setA, List<List<Integer>> setB) {
        if (setA.size() != setB.size())
            return;
        for (int i=0;i<setA.size();i++) {
            int posA = this.rand.nextInt(setA.size());
            int posB = this.rand.nextInt(setA.size());

            List valA = setA.get(posA);
            List valB = setA.get(posB);
            setA.remove(posA);
            setA.add(posA, valB);
            setA.remove(posB);
            setA.add(posB, valA);

            valA = setB.get(posA);
            valB = setB.get(posB);
            setB.remove(posA);
            setB.add(posA, valB);
            setB.remove(posB);
            setB.add(posB, valA);
        }
    }

    public void shuffle(double [][] setA, double [][] setB) {
        if (setA.length != setB.length)
            return;
        for (int i=0;i<setA.length;i++) {
            int posA = this.rand.nextInt(setA.length);
            int posB = this.rand.nextInt(setA.length);

            double[] valA = setA[posA];
            double [] valB = setA[posB];
            setA[posA] = valB;
            setA[posB] = valA;

            valA = setB[posA];
            valB = setB[posB];
            setB[posA]=valB;
            setB[posB]=valA;
        }
    }

    private double [] getDataPoint(List<Integer> idealPoint,List<List<Double>>unfilteredInput, List<List<Integer>> unfilderedIdeal,boolean pop){
        double [] result = {};

        for (int i =0;i<unfilderedIdeal.size();i++) {
            List<Integer> point = unfilderedIdeal.get(i);
            boolean identical = true;
            if (idealPoint != point)
                identical = false;

            if (identical) {
                List<Double> res = unfilteredInput.get(i);
                if (pop) {
                    unfilteredInput.remove(i);
                    unfilderedIdeal.remove(i);
                }
                result = new double[res.size()];
                for (int j = 0; j < res.size(); j++)
                    result[j] = res.get(j);
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

    private double [] convertDoubleToDouble (List<Double> values) {
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
