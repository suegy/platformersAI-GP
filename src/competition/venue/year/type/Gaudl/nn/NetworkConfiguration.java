package competition.venue.year.type.Gaudl.nn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkConfiguration {

    public String networkType;
    public String networkName;
    public int layers;
    private List<Map<String,Double>> weights;
    public int trainingEpochs;
    public double finalErrorRate;
    public boolean [] biasedLayers;
    public double [] biasedActivation;

    public NetworkConfiguration(){
        networkName = "";
        networkType = "";
        layers = 0;
        weights = null;
        trainingEpochs = 0;
        finalErrorRate = 1.0;
        biasedLayers = new boolean[0];
        biasedActivation = new double[0];
    }

    public void setWeights(List<Map<List<Integer>,Double>> weights) {
        List<Map<String,Double>> result = new ArrayList<>();

        for (Map<List<Integer>,Double> map : weights) {
            Map<String,Double> temp = new HashMap<>();
            for (List<Integer> key : map.keySet()){
                String list = "";
                for (int i = 0; i<key.size();i++)
                    list += ""+key.get(i)+" ";
                temp.put(list,map.get(key));
            }
            result.add(temp);
        }

        this.weights = result;

    }

    public List<Map<List<Integer>,Double>> getWeights() {
        List<Map<List<Integer>,Double>> result = new ArrayList<>();

        for (Map<String,Double> map : this.weights) {
            Map<List<Integer>,Double> temp = new HashMap<>();
            for (String key : map.keySet()){
                List<Integer> list = new ArrayList<>();
                String [] split = key.trim().split(" ");
                for (int i = 0; i<split.length;i++)
                    list.add(new Integer(split[i]));
                temp.put(list,map.get(key));
            }
            result.add(temp);
        }

        return result;
    }

}
