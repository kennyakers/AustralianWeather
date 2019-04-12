
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.io.IOException;
import java.util.ArrayList;

public class AustraliaWeather {

    private static double notAvailable() {
        return Double.NaN;
    }

    private static Weather.Date parseDate(String value) {
        return Weather.Date.valueOf(value);
    }

    private static double parseTemperature(String value) {
        if (value.equals("NA")) {
            return notAvailable();
        } else {
            double temperature = Double.parseDouble(value);
            if (temperature < -100.0 || temperature > 100.0) {
                throw new IllegalArgumentException("Bad temperature: " + value);
            } else {
                return temperature;
            }
        }
    }

    private static double parseHumidity(String value) {
        if (value.equals("NA")) {
            return notAvailable();
        } else {
            double humidity = Double.parseDouble(value);
            if (humidity < 0.0 || humidity > 100.0) {
                throw new IllegalArgumentException("Bad humidity: " + value);
            } else {
                return humidity;
            }
        }
    }

    private static double parsePressure(String value) {
        if (value.equals("NA")) {
            return notAvailable();
        } else {
            double pressure = Double.parseDouble(value);
            if (pressure <= 0.0) {
                throw new IllegalArgumentException("Bad pressure: " + value);
            } else {
                return pressure;
            }
        }
    }

    private static double parseWindSpeed(String value) {
        if (value.equals("NA")) {
            return notAvailable();
        } else {
            double speed = Double.parseDouble(value);
            if (speed < -40.0) {
                throw new IllegalArgumentException("Bad speed: " + value);
            } else {
                return speed;
            }
        }
    }

    private static Weather.Direction parseWindDirection(String value) {
        if (value.equals("NA")) {
            value = "MISSING";
        }
        return Weather.Direction.valueOf(value);
    }

    private static double parseHoursOfSunshine(String value) {
        if (value.equals("NA")) {
            return notAvailable();
        } else {
            double sunshine = Double.parseDouble(value);
            if (sunshine < 0.0 || sunshine > 24.0) {
                throw new IllegalArgumentException("Bad sunshine: " + value);
            } else {
                return sunshine;
            }
        }
    }

    private static int parseCloudCover(String value) {
        if (value.equals("NA")) {
            return -1;
        } else {
            int cloudCover = Integer.parseInt(value);
            if (cloudCover < 0 || cloudCover > 9) {
                throw new IllegalArgumentException("Bad cloud cover: " + value);
            } else {
                return cloudCover;
            }
        }
    }

    private static double parseRainfall(String value) {
        if (value.equals("NA")) {
            return notAvailable();
        } else {
            double rainfall = Double.parseDouble(value);
            if (rainfall < 0.0) {
                throw new IllegalArgumentException("Bad rainfall: " + value);
            } else {
                return rainfall;
            }
        }
    }

    private static double parseEvaporationRate(String value) {
        if (value.equals("NA")) {
            return notAvailable();
        } else {
            double evaporationRate = Double.parseDouble(value);
            if (evaporationRate < 0.0) {
                throw new IllegalArgumentException("Bad evaporation rate: " + value);
            } else {
                return evaporationRate;
            }
        }
    }

    private static Boolean parseBoolean(String value) {
        switch (value.toLowerCase()) {
            case "yes":
            case "true":
                return true;

            case "no":
            case "false":
                return false;

            case "na":
                return null;

            default:
                throw new IllegalArgumentException("Bad boolean value: " + value);
        }
    }

    private static Weather.DataPoint processLine(String line) {
        String[] fields = line.split(",");

        if (fields.length < 24) {
            throw new IllegalArgumentException("Too few fields: " + line);
        }

        Weather.DataPoint result = new Weather.DataPoint(
                parseDate(fields[0]), // Date of weather observations
                fields[1], // Location (city) of observation
                parseTemperature(fields[2]), // min temperature in °C
                parseTemperature(fields[3]), // max temperature in °C
                parseRainfall(fields[4]), // rainfall in mm
                parseEvaporationRate(fields[5]), // evaporation rate in mm/day
                parseHoursOfSunshine(fields[6]), // hours of bright sunshine
                parseWindSpeed(fields[8]), // maximum wind gust in km/hour
                parseWindDirection(fields[7]), // direction of maximum wind gust

                parseTemperature(fields[19]), // morning (9am) temperature in °C
                parseHumidity(fields[13]), // morning (9am) relative humidity as %
                parsePressure(fields[15]), // morning (9am) pressuare in hpa (hectapascals)
                parseCloudCover(fields[17]), // morning (9am) cloud cover in oktas (eighths)
                parseWindSpeed(fields[11]), // morning (9am) wind speed in km/hour
                parseWindDirection(fields[9]), // morning (9am) wind direction

                parseTemperature(fields[20]), // afternoon (3am) temperature in °C
                parseHumidity(fields[14]), // afternoon (3am) relative humidity as %
                parsePressure(fields[16]), // afternoon (3am) pressure in hpa (hectapascals)
                parseCloudCover(fields[18]), // afternoon (3am) cloud cover in oktas (eighths)
                parseWindSpeed(fields[12]), // afternoon (3am) wind speed in km/hour
                parseWindDirection(fields[10]), // afternoon (3am) wind direction

                parseBoolean(fields[21]), // Rained today
                parseBoolean(fields[23]), // Rained tomorrow (ANSWER)
                parseRainfall(fields[22]) // Tomorrow's rainfall in mm
        );

        return result;
    }

    public static int cullMissingData(ArrayList<Weather.DataPoint> dataset, String field) {
        // Remove data points that have missing values for a given field.
        int count = 0;
        for (int i = dataset.size() - 1; i >= 0; i--) {
            Weather.DataPoint item = dataset.get(i);
            if (item.isMissing(field)) {
                dataset.remove(i);
                count++;
            }
        }
        return count;
    }

    public static void readDataFile(Reader reader, ArrayList<Weather.DataPoint> dataset, int limit) throws IOException {
        BufferedReader input = new BufferedReader(reader);
        String line = input.readLine();
        if (limit <= 0) {
            limit = Integer.MAX_VALUE;
        }
        int i = 0;
        while (i++ < limit && line != null) {
            dataset.add(processLine(line));
            line = input.readLine();
        }
    }

    public static void readDataFile(String filename, ArrayList<Weather.DataPoint> dataset) throws IOException {
        readDataFile(new FileReader(filename));
    }

    public static ArrayList<Weather.DataPoint> readDataFile(Reader reader) throws IOException {
        ArrayList<Weather.DataPoint> result = new ArrayList<>();
        readDataFile(reader, result, 0);
        return result;
    }

    public static ArrayList<Weather.DataPoint> readDataFile(String filename) throws IOException {
        ArrayList<Weather.DataPoint> result = new ArrayList<>();
        FileReader reader = new FileReader(filename);
        readDataFile(reader, result, 0);
        return result;
    }

    public static void main(String[] args) {
        ArrayList<Weather.DataPoint> dataset = new ArrayList<>();
        // Read the data files.
        for (String arg : args) {
            if (arg.length() > 0 && arg.charAt(0) != '-') {
                try {
                    readDataFile(new FileReader(arg), dataset, 0);
                } catch (IOException e) {
                    System.err.println("Could not read file " + arg + ": " + e.getMessage());
                }
            }
        }

        // Cull records with missing data.
        for (String arg : args) {
            if (arg.length() > 0 && arg.charAt(0) == '-') {
                String field = arg.substring(1);
                int missing = cullMissingData(dataset, field);
                System.out.println(field + ": " + missing + " records removed");
            }
        }
        // Gather and print statistics.
        for (String arg : args) {
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;
            double sum = 0.0;
            int count = 0;

            if (arg.length() > 0 && arg.charAt(0) == '-') {
                String field = arg.substring(1);
                for (Weather.DataPoint item : dataset) {
                    double value = item.getNumber(field);
                    if (value < min) {
                        min = value;
                    }
                    if (value > max) {
                        max = value;
                    }
                    sum += value;
                    count++;
                }

                System.out.println("Field: " + field);
                System.out.println("  Minimum: " + min);
                System.out.println("  Maximum: " + max);
                System.out.println("  Average: " + (sum / count));
                System.out.println("  Values:  " + count);
                System.out.println();
            }
        }
    }
}
