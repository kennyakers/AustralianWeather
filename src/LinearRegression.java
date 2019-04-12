
import java.io.Console;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class LinearRegression {

    private class FeatureStats {

        private final String field;
        private double min;
        private double max;
        private double mean;
        private double stdDev;
        private double numValues;
        private double sum;

        private double sumOfXMinusXBarSquared;

        public FeatureStats(String field) {
            this.field = field;
            this.min = Double.MAX_VALUE;
            this.max = Double.MIN_VALUE;
        }

        public void update(double newValue) {
            if (Double.isNaN(newValue)) {
                return;
            }
            this.numValues++;
            this.sum += newValue;
            if (newValue < this.min) {
                this.min = newValue;
            } else if (newValue > this.max) {
                this.max = newValue;
            }
            this.mean = this.sum / this.numValues;
            //System.out.println("newValue: " + newValue);
            //System.out.println("mean: " + this.mean);
            this.sumOfXMinusXBarSquared += Math.pow(newValue - this.mean, 2.0);
            this.stdDev = Math.sqrt(this.sumOfXMinusXBarSquared / this.numValues);
        }

        public double zScore(double x) {
            if (this.stdDev == 0) {
                return this.mean;
            }

            //System.out.println("stdDev: " + this.stdDev);
            double result = (x - this.mean) / this.stdDev;
            //System.out.println("zScore: " + result);
            return result;
        }

        public String getField() {
            return field;
        }

        public double getMin() {
            return min;
        }

        public double getMax() {
            return max;
        }

        public double getMean() {
            return mean;
        }

        public double getStdDev() {
            return stdDev;
        }

        public double getNumValues() {
            return numValues;
        }

        @Override
        public String toString() {
            return this.field + " (" + this.numValues + " values): µ = " + String.format("%.2f", this.mean) + " | σ = " + String.format("%.2f", this.stdDev);
        }

    }

    private Matrix dataMatrix;
    private double[] weights;
    private Vector outputs;

    private final double learningRate;

    private boolean skippedEpoch;
    private boolean skippedTraining;

    private FeatureStats[] featureStats;

    public LinearRegression(ArrayList<Weather.DataPoint> data, double learningRate, int epochs) {

        int NUM_FEATURES = 21;

        this.skippedEpoch = false;
        this.skippedTraining = false;
        this.featureStats = new FeatureStats[NUM_FEATURES + 1];
        for (int i = 0; i < this.featureStats.length; i++) {
            this.featureStats[i] = new FeatureStats(Weather.fields[i]);
        }

        this.outputs = new Vector();

        double[][] dataset = new double[data.size()][NUM_FEATURES + 1];
        for (int i = 0; i < data.size(); i++) {
            dataset[i] = this.processDataPoint(data.get(i));
        }

        this.weights = new double[NUM_FEATURES + 1]; // Weight vector to contain weights for all 21 used features + the dummy variable at w0.
        for (int i = 0; i < this.weights.length; i++) {
            this.weights[i] = 1.0;
        }

        this.dataMatrix = new Matrix(dataset);

        if (Main.DEBUG) {
            System.out.println("\nOriginal data matrix:");
            this.dataMatrix.printTruncated();
        }

        for (int i = 0; i < this.dataMatrix.numDataPoints(); i++) { // Replace NaNs with sampled values
            this.dataMatrix.getMatrix()[i] = this.replaceNaNs(this.dataMatrix.getDataPoint(i));
        }
        if (Main.DEBUG) {
            System.out.println("\nReplaced data matrix:");
            this.dataMatrix.printTruncated();
            System.out.println("\n");
        }

        this.normalize();

        if (Main.DEBUG) {
            System.out.println("\nNormalized and cleaned data matrix:");
            this.dataMatrix.printTruncated();
            for (FeatureStats feature : this.featureStats) {
                System.out.println(feature);
            }
        }

        this.learningRate = learningRate;

        if (Main.DEBUG) {
            System.out.println("Original weights: " + Arrays.toString(this.weights));
            System.out.println("Datamatrix num data points: " + this.dataMatrix.numDataPoints());
        }
        for (int i = 0; i < epochs; i++) {
            if (this.skippedTraining) {
                System.out.println("Skipping to end of training.");
            } else if (this.skippedEpoch) {
                System.out.println("Going to epoch #" + i);
                this.skippedEpoch = false;
            }
            this.updateWeights();
            if (Main.DEBUG_WEIGHTS) {
                System.out.println("Epoch #" + i + " : " + Arrays.toString(this.weights) + "\n");
            }
        }
        //if (Main.DEBUG) {
        System.out.println("New weights: ");
        System.out.println(Arrays.toString(this.weights));
        //}
    }

    public double[] replaceNaNs(Vector input) {
        return this.replaceNaNs(input.getVectorComponents());
    }

    private double[] replaceNaNs(double[] input) {
        for (int i = 0; i < input.length; i++) {
            if (Double.isNaN(input[i])) {
                if (Main.DEBUG) {
                    System.out.println(this.featureStats[i]);
                }
                double replacementVal = this.sample(this.featureStats[i].mean, this.featureStats[i].stdDev);
                input[i] = replacementVal; // Sample a value out of the probability distribution for that feature.
                if (Main.DEBUG) {
                    System.out.println("----------REPLACING " + this.dataMatrix.getAt(i, i) + " with " + replacementVal + "------------");
                }
            }
        }
        return input;
    }

    public double[] processDataPoint(Weather.DataPoint dataPoint) {
        double[] processed = {
            1.0, // Dummy variable xj0
            dataPoint.date().toNumber(),
            this.getLocationNumber(dataPoint.location()),
            dataPoint.minTemperature(),
            dataPoint.maxTemperature(),
            dataPoint.rainfall(),
            dataPoint.hoursOfSunshine(), //
            dataPoint.evaporationRate(), //
            dataPoint.windGustSpeed(),
            dataPoint.windGustDirection().heading(),
            dataPoint.morningTemperature(),
            dataPoint.morningHumidity(),
            dataPoint.morningPressure(),
            dataPoint.morningCloudCover(),
            dataPoint.morningWindSpeed(),
            dataPoint.morningWindDirection().heading(),
            dataPoint.afternoonTemperature(),
            dataPoint.afternoonHumidity(),
            dataPoint.afternoonPressure(),
            dataPoint.afternoonCloudCover(),
            dataPoint.afternoonWindSpeed(),
            dataPoint.afternoonWindDirection().heading()
        };

        for (int i = 0; i < processed.length; i++) {
            this.featureStats[i].update(processed[i]);
        }
        this.outputs.append(dataPoint.rainTomorrow() ? 1.0 : 0.0); // Add the answer to the outputs vector (1 for rain, 0 for no rain)
        return processed;
    }

    private void normalize() {
        for (int i = 0; i < this.dataMatrix.numDataPoints(); i++) {
            for (int j = 0; j < this.dataMatrix.numFeatures(); j++) { // Should be 0-19
                double zScore = this.featureStats[j].zScore(this.dataMatrix.getAt(i, j));
                this.dataMatrix.setAt(i, j, zScore);
            }
        }
    }

    private int getLocationNumber(String location) {
        return Arrays.asList(Weather.locations).indexOf(location);
    }

    // wi ← wi + α SUM(xj,i(yj − hw(xj))
    public void updateWeights() {
        Console console = System.console();
        double[] newWeights = new double[this.weights.length];
        for (int i = 0; i < this.weights.length; i++) { // For each feature's weight (skip w0)
            if (Main.DEBUG_WEIGHTS && !this.skippedEpoch && !this.skippedTraining) {
                System.out.println("Updating the weight for " + this.featureStats[i].getField());
            }
            double summation = 0;
            for (int j = 0; j < this.dataMatrix.numDataPoints(); j++) { // For each data point for that feature
                double hW = this.evaluateHyperplane(this.dataMatrix.getDataPoint(j));
                double yj = this.outputs.get(j);
                double xji = this.dataMatrix.getAt(j, i);
                if (Double.isNaN(hW) || Double.isNaN(yj) || Double.isNaN(xji)) {
                    throw new IllegalArgumentException("NaN while updating weights");
                }
                summation += (yj - hW) * xji;
                if (Main.DEBUG_WEIGHTS && !this.skippedEpoch && !this.skippedTraining) {
                    System.out.println("hW = " + hW);
                    System.out.println("yj = " + yj);
                    System.out.println("xji = " + xji);
                    System.out.println("(yj - hW) * xji = " + (yj - hW) * xji);
                    System.out.println("---------------");
                }
            }
            if (Main.DEBUG_WEIGHTS && !this.skippedEpoch && !this.skippedTraining) {
                System.out.println("Summation: " + summation);
                System.out.println("(" + this.featureStats[i].getField() + "): " + "Updating " + this.weights[i] + " to " + (this.weights[i] + (this.learningRate * summation)) + "\n\n");
            }
            newWeights[i] = this.weights[i] + (this.learningRate * summation);

            if (Main.DEBUG_WEIGHTS && !this.skippedEpoch && !this.skippedTraining) {
                String line = getLine(console);
                OUTER:
                while (line != null) {
                    String command = getCommand(line);
                    switch (command.toLowerCase()) {
                        case "y":
                            break OUTER;
                        case "quit":
                            System.exit(1);
                        case "next":
                            this.skippedEpoch = true;
                            System.out.println("Going to next epoch\n");
                            break OUTER;
                        case "end":
                            this.skippedTraining = true;
                            break OUTER;
                        case "help":
                            System.out.println("Debug Weights Commands:");
                            System.out.println("y\tGo to next feature in epoch.");
                            System.out.println("quit\tQuit the program.");
                            System.out.println("next\tGo to next epoch.");
                            System.out.println("end\tSkip the rest of training and go to results.");
                            break;
                        default:
                            System.out.println("Unrecognized command. Use \"help\" for command help.");
                    }
                    line = getLine(console);
                }
            }
            this.weights = newWeights;
        }
    }

    private static String getLine(Console console) {
        try {
            return console.readLine("Next weight?: ").trim();
        } catch (Exception e) {
            System.err.println(e);
            return null;
        }
    }

    private static String getArgument(String line, int index) {
        String[] words = line.split("\\s");
        return words.length > index ? words[index] : "";
    }

    private static String getCommand(String line) {
        return getArgument(line, 0);
    }

    private double evaluateHyperplane(Vector input) {
        return this.evaluateHyperplane(input.getVectorComponents());
    }

    private double evaluateHyperplane(double[] input) {
        double result = 0.0;
        for (int i = 1; i < input.length; i++) {
            if (Double.isNaN(input[i])) {
                System.out.println("Got a NaN");
                continue;
            }
            double inputAt = input[i];
            result += inputAt * this.weights[i];
        }
        return result;
    }

    // Samples values from any normal distribution with mean mu and variance sigma using the Box-Muller transform method.
    private double sample(double mean, double stdDev) {
        if (Double.isInfinite(mean) || Double.isNaN(mean)) {
            throw new IllegalArgumentException("Mean cannot be " + mean);
        } else if (Double.isInfinite(stdDev) || Double.isNaN(stdDev)) {
            throw new IllegalArgumentException("Standard deviation cannot be " + stdDev);
        }
        Random rand = new Random(); //reuse this if you are generating many
        double u1 = 1.0 - rand.nextDouble(); //uniform(0,1] random doubles
        double u2 = 1.0 - rand.nextDouble();
        double randStdNormal = Math.sqrt(-2.0 * Math.log(u1)) * Math.sin(2.0 * Math.PI * u2); //random normal (0,1)
        double randNormal = mean + stdDev * randStdNormal; //random normal (mean, stdDev^2)
        return randNormal;
    }

    public boolean rainTomorrow(Weather.DataPoint dataPoint) {
        return this.rainTomorrow(new Vector(this.processDataPoint(dataPoint)));
    }

    private boolean rainTomorrow(Vector dataPoint) {
        double[] replaced = this.replaceNaNs(dataPoint);
        if (Main.DEBUG) {
            System.out.println("Evaluating data point (length " + replaced.length + "): " + Arrays.toString(replaced) + "\n");
        }
        Vector knownPoint = this.dataMatrix.getDataPoint(0);
        boolean knownPointClass = this.outputs.get(0) == 1.0; // True if the known point was of class "rain"
        boolean testPointBiggerThanPlaneConstant = this.evaluateHyperplane(replaced) > this.weights[0];
        boolean knownPointBiggerThanPlaneConstant = this.evaluateHyperplane(knownPoint) > this.weights[0];
        return testPointBiggerThanPlaneConstant ^ knownPointBiggerThanPlaneConstant ? !knownPointClass : knownPointClass; // If on same side of hyperplane, return the class of that knownPoint.

        /*
        Vector normal = new Vector(weights); // Normal vector is given by the coefficients of the plane in normal form, which are just the entries in the weights array.
        Vector testPoint = new Vector(dataPoint);
        double[] point = new double[20];
        point[19] = this.planeConstant / normal.getAt(normal.getVectorComponents().length - 1);
        Vector pointOnPlane = new Vector(point); // Set all variables to 0 except for one and solve for the last
        double result = normal.dot(pointOnPlane.minus(testPoint));
        return Math.signum(result) > 0.0;
         */
    }

    // DEPRECATED
    // w∗=(XT X)^(−1) XT y
    // The first component of this weight vector is the plane constant D.
    private double[] calculateWeights() {
        Matrix transposed = dataMatrix.transpose();
        Matrix mat = transposed.times(dataMatrix);
        mat = mat.invert();
        mat = mat.times(transposed);
        double[] result = mat.times(outputs).getVectorComponents();
        double[] returnable = new double[result.length];
        for (int i = 0; i < result.length; i++) {
            //returnable[i] = result[i].doubleValue();
        }
        return result;
    }

}
