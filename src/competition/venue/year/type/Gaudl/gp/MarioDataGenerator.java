package competition.venue.year.type.Gaudl.gp;

import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;

public class MarioDataGenerator {

    /**
     * 1 xor 0 = 1, 0 xor 0 = 0, 0 xor 1 = 1, 1 xor 1 = 0
     */
    public static final double[] SEQUENCE = { 1.0, 0.0, 1.0, 0.0, 0.0, 0.0,
            0.0, 1.0, 1.0, 1.0, 1.0, 0.0 };

    private double[][] input;
    private double[][] ideal;

    public MLDataSet generate(final int count) {
        this.input = new double[count][1];
        this.ideal = new double[count][1];

        for (int i = 0; i < this.input.length; i++) {
            this.input[i][0] = MarioDataGenerator.SEQUENCE[i
                    % MarioDataGenerator.SEQUENCE.length];
            this.ideal[i][0] = MarioDataGenerator.SEQUENCE[(i + 1)
                    % MarioDataGenerator.SEQUENCE.length];
        }

        return new BasicMLDataSet(this.input, this.ideal);
    }
}
