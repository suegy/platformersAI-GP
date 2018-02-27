package competition.venue.year.type.Gaudl.dnn;

import competition.venue.year.type.Gaudl.nn.UniquePairs;
import org.deeplearning4j.datasets.fetchers.BaseDataFetcher;
import org.deeplearning4j.datasets.iterator.BaseDatasetIterator;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.platformer.benchmark.tasks.GeneratorTask;

import java.util.*;

public class MarioDLDataGenerator extends BaseDatasetIterator {


    public MarioDLDataGenerator(int batch, int numExamples, BaseDataFetcher fetcher) {
        super(batch, numExamples, fetcher);
    }

    public MarioDLDataGenerator(int batch,int numExamples, int subsetStart,int subSetEnd) {
        this(batch,numExamples,new MarioANNDataFetcher(subsetStart,subSetEnd));
    }

}
