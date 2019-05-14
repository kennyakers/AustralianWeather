
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public static boolean DEBUG = false;
    public static boolean DEBUG_WEIGHTS = false;
    private static int trainSize = 100;
    private static int epochs = 10;
    private static int pointToPredict = 0;
    private static double learningRate = 0.1;
    private static double regularizationPenalty = 0.8;
    private static LinearRegression.Regularization regularizationType = LinearRegression.Regularization.LASSO;

    public static void main(String[] args) {
        ArrayList<Weather.DataPoint> dataset = new ArrayList<>();
        String filename = "weather.csv";
        // Read the data files.
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.length() > 0) {
                switch (arg) {
                    case "-debug":
                        DEBUG = true;
                        break;
                    case "-debug-weights":
                        DEBUG_WEIGHTS = true;
                        break;
                    case "-ridge":
                        regularizationType = LinearRegression.Regularization.RIDGE;
                        break;
                    case "-train":
                        try {
                            trainSize = Integer.parseInt(args[++i]);
                        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                            System.out.println("No training set size specified. Defaulting to " + trainSize);
                        }
                        break;
                    case "-epochs":
                        try {
                            epochs = Integer.parseInt(args[++i]);
                        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                            System.out.println("Number of epochs not specified. Defaulting to " + epochs);
                        }
                        break;
                    case "-learn":
                        try {
                            learningRate = Double.parseDouble(args[++i]);
                        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                            System.out.println("Learning rate not specified. Defaulting to " + learningRate);
                        }
                        break;
                    case "-penalty":
                        try {
                            regularizationPenalty = Double.parseDouble(args[++i]);
                        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                            System.out.println("Regularization penalty not specified. Defaulting to " + regularizationPenalty);
                        }
                        break;
                    case "-predict":
                        try {
                            pointToPredict = Integer.parseInt(args[++i]);
                        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                            System.out.println("Point to predict not specified. Defaulting to the point at index " + pointToPredict);
                        }
                        break;
                    default:
                        try {
                            FileReader reader = new FileReader(arg); // See if valid file name is specified
                            filename = arg;
                        } catch (IOException e) {
                            System.err.println("Could not read file " + arg + ": " + e.getMessage());
                        }
                        break;
                }
            }
        }

        try {
            AustraliaWeather.readDataFile(new FileReader(filename), dataset, trainSize); // Just get first 15 examples

        } catch (IOException e) {
            System.err.println("Could not read file " + filename + ": " + e.getMessage());
        }

        if (DEBUG) {
            System.out.println("Training on " + dataset.size() + " datapoints for " + epochs + " epochs");
        }

        Weather.DataPoint toEvaluate = dataset.get(pointToPredict);
        System.out.println("Point to predict: " + toEvaluate + "\n");

        LinearRegression prediction = new LinearRegression(dataset, learningRate, epochs, regularizationType, regularizationPenalty);

        System.out.println("Prediction: " + (prediction.rainTomorrow(toEvaluate) ? "Rain" : "No rain") + "\tAnswer: " + (toEvaluate.rainTomorrow() ? "Rain" : "No rain" + "\n"));
    }

}
