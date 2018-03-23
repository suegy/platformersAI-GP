package competition.venue.year.type.Gaudl.dnn;


import competition.venue.year.type.Gaudl.nn.UniquePairs;
import org.apache.log4j.Logger;
import org.deeplearning4j.datasets.fetchers.BaseDataFetcher;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.platformer.benchmark.tasks.GeneratorTask;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MarioANNDataFetcher extends BaseDataFetcher {

    /**
     *
     */
    private static final long serialVersionUID = 4566329799221375262L;

    public static final String[] recordedGames = {"run0", "run1", "run2", "run3", "run4", "run5", "run6", "run7", "run8", "run9",
            "run10", "run11", "run12", "run13", "run14", "run15", "run16", "run17", "run18", "run19",
            "run20", "run21", "run22", "run23", "run24", "run25", "run26", "run27", "run28", "run29",
            "run30", "run31", "run32", "run33", "run34", "run35", "run36", "run37", "run38", "run39",
            "run40", "run41", "run42", "run43", "run44", "run45", "run46", "run47", "run48", "run49",
            "run50", "run51", "run52", "run53", "run54", "run55", "run56", "run57", "run58", "run59"
    };
    public static final String recordedPath = "rand-lvl-progression-ld0";
    private Random rand = new Random();
    private transient Logger LOGGER;

    private GeneratorTask replayTask = null;
    private List<List<Double>> unfilteredInput = new ArrayList<>();
    private List<List<Integer>> unfilderedIdeal = new ArrayList<>();
    private int playedLevelsFrom = 0;
    private int playedLevelsTo = 1;
    private int gameCounter = 0;

    public MarioANNDataFetcher(int subSetStart, int subSetEnd) {
        numOutcomes = 6;
        inputColumns = 361;
        playedLevelsTo = subSetEnd;
        playedLevelsFrom = subSetStart;
        LOGGER = Logger.getRootLogger();
        playLevels(playedLevelsFrom, playedLevelsFrom+1);
        gameCounter = playedLevelsFrom+1;

        totalExamples = unfilderedIdeal.size() * (playedLevelsTo-playedLevelsFrom+1);
    }

    private void playLevels(int from, int to) {
        LOGGER.info("training data generation for levels "+from+" -- "+to);
        int[][][] output = new int[to-from][][];
        int[][][][] input = new int[to-from][][][];

        for (int i = from; i < to; i++) {
            replayTask = new GeneratorTask();
            replayTask.reset( recordedGames[i % (recordedGames.length-1)]);
            replayTask.startReplay(200);
            input[i-from] = replayTask.getInputData();
            output[i-from] = replayTask.getOutputData();
            replayTask.delete();
        }


        for (int g = 0; g < input.length; g++)
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
    }

    @Override
    public void fetch(int numExamples) {
        int from = cursor;
        int to = cursor + numExamples;
        if (to > totalExamples)
            to = totalExamples;
        LOGGER.info("fetching data  cursor"+cursor+" -- "+to);
        initializeCurrFromList(generate(from, to));
        cursor += numExamples;
    }

    private List<DataSet> generate(int from, int to) {

        double[][] input = new double[to - from][inputColumns];
        double[][] ideal = new double[to - from][inputColumns];

        List<DataSet> list = new ArrayList<>();

        INDArray ret = Nd4j.ones(Math.abs(to - from), inputColumns);
        double[][] outcomes = new double[to-from][numOutcomes];

        //generate game data if required if more data is required than available loop data
        int counter = to;
        while (counter > unfilteredInput.size()) {
            playLevels(gameCounter, gameCounter+1);
            gameCounter += 1;
            if (gameCounter >= playedLevelsTo)
                gameCounter = playedLevelsFrom;
        }

        /**
         * balancing the data distribution to enhance dataset
         */
        int median = 0;
        UniquePairs datapairs = new UniquePairs(unfilderedIdeal, unfilteredInput);
        int min = datapairs.getDistribution().get(0);

        for (int i = 0; i < datapairs.size(); i++) {
            median += datapairs.getDistribution().get(i);
            if (datapairs.getDistribution().get(i) < min)
                min = datapairs.getDistribution().get(i);
        }
        median /= datapairs.size();

        //System.out.println("DataSet contains " + datapairs.size() + " unique pairs.");

        int putCount = 0;
        for (int i = from; i < to; i++) {

            double[] vector = new double[inputColumns];
            for (int j = 0; j < inputColumns; j++)
                vector[j] = unfilteredInput.get(i).get(j);

            ret.putRow(putCount++, Nd4j.create(vector));

            List<Integer> rawOutcome = unfilderedIdeal.get(i);
            double[] outcome = new double[rawOutcome.size()];
            for (int j = 0;j<rawOutcome.size();j++)
                outcome[j]=rawOutcome.get(j);
            outcomes[i-from] = outcome;
        }

        for (int i = 0; i < ret.rows(); i++) {
            DataSet add = new DataSet(ret.getRow(i), Nd4j.create(outcomes[i]));
            list.add(add);
        }

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
        return list;
    }

    public void shuffle(List<List<Double>> setA, List<List<Integer>> setB) {
        if (setA.size() != setB.size())
            return;
        for (int i = 0; i < setA.size(); i++) {
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

    public void shuffle(double[][] setA, double[][] setB) {
        if (setA.length != setB.length)
            return;
        for (int i = 0; i < setA.length; i++) {
            int posA = this.rand.nextInt(setA.length);
            int posB = this.rand.nextInt(setA.length);

            double[] valA = setA[posA];
            double[] valB = setA[posB];
            setA[posA] = valB;
            setA[posB] = valA;

            valA = setB[posA];
            valB = setB[posB];
            setB[posA] = valB;
            setB[posB] = valA;
        }
    }

    private double[] getDataPoint(List<Integer> idealPoint, List<List<Double>> unfilteredInput, List<List<Integer>> unfilderedIdeal, boolean pop) {
        double[] result = {};

        for (int i = 0; i < unfilderedIdeal.size(); i++) {
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

    private double[] convertIntToDouble(List<Integer> values) {
        double[] result = new double[values.size()];

        for (int i = 0; i < values.size(); i++)
            result[i] = values.get(i);

        return result;
    }

    private double[] convertDoubleToDouble(List<Double> values) {
        double[] result = new double[values.size()];

        for (int i = 0; i < values.size(); i++)
            result[i] = values.get(i);

        return result;
    }
}
