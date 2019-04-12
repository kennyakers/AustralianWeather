
import java.util.ArrayList;


public class DecisionTree {
    
    private ArrayList<Weather.DataPoint> dataset;
    
    public DecisionTree(ArrayList<Weather.DataPoint> data) {
        this.dataset = data;
    }
    
    
    // Information gain = Entropy(parent) - WeightedAvgEntropy(children)
    // https://homes.cs.washington.edu/~shapiro/EE596/notes/InfoGain.pdf
    public double getEntropy(String field) {
        for (Weather.DataPoint dataPoint : this.dataset) {
            try {
                double value = dataPoint.getNumber(field);
            } catch (IllegalArgumentException e) {
                
            }
        }
        return 0;
    }
    
}
