package competition.venue.year.type.Gaudl.nn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UniquePairs {

    private List<List<Integer>> distributionA;
    private List<List<Double>> distributionB;
    private List<Integer> occurrance;

    public UniquePairs(List<List<Integer>> distA, List<List<Double>> distB) {
        distributionA = new ArrayList<>();
        distributionB = new ArrayList<>();
        occurrance = new ArrayList<>();

        for (int i = 0; i < distA.size(); i++) {
            List<Integer> a = distA.get(i);
            List<Double> b = distB.get(i);
            boolean found = false;
            if (distributionA.contains(a)) {
                for (int k = distributionA.indexOf(a); k <= distributionA.lastIndexOf(a); k++) {
                    if (distributionA.get(k) == a && distributionB.get(k) == b) {
                        found = true;
                        occurrance.set(k, occurrance.get(k) + 1);
                        break;
                    }
                }
            }
            if (!found) {
                distributionA.add(a);
                distributionB.add(b);
                occurrance.add(1);
            }
        }
    }

    public List<Integer> getDistribution() {
        return this.occurrance;
    }

    public int size() {
        return distributionA.size();
    }

    public List<Double> getDataB(int pos) {
        return distributionB.get(pos);
    }

    public List<Integer> getDataA(int pos) {
        return distributionA.get(pos);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
