
public class Weather {

    public static class Date {

        private int year;
        private int month;
        private int day;

        public Date(int year, int month, int day) {
            this.year = year;
            this.month = month;
            this.day = day;
        }

        public int year() {
            return this.year;
        }

        public int month() {
            return this.month;
        }

        public int day() {
            return this.day;
        }

        public int toNumber() {
            return this.day + this.month + this.year;
        }

        public boolean equals(Date other) {
            return other != null
                    && this.year == other.year
                    && this.month == other.month
                    && this.day == other.day;
        }

        @Override
        public boolean equals(Object other) {
            if (other != null && other instanceof Date) {
                return this.equals((Date) other);
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return this.day + 31 * this.month + (13 * 31) * this.year;
        }

        @Override
        public String toString() {
            return this.year + "-" + this.month + "-" + this.day;
        }

        public static Date valueOf(String date) {
            String[] fields = date.split("-");
            int year = Integer.parseInt(fields[0]);
            int month = Integer.parseInt(fields[1]);
            int day = Integer.parseInt(fields[2]);
            return new Date(year, month, day);
        }
    }

    // ----------------------------------------------------------------------
    public static enum Direction {
        N(0.0),
        NNE(22.5),
        NE(45.0),
        ENE(67.5),
        E(90.0),
        ESE(112.5),
        SE(135.0),
        SSE(157.5),
        S(180.0),
        SSW(202.5),
        SW(225.0),
        WSW(247.5),
        W(270.0),
        WNW(292.5),
        NW(315.0),
        NNW(337.5),
        MISSING(Double.NaN);

        public static Direction NORTH = Direction.N;
        public static Direction SOUTH = Direction.S;
        public static Direction EAST = Direction.E;
        public static Direction WEST = Direction.W;
        public static Direction NaN = Direction.MISSING;

        public static Direction NORTH_EAST = Direction.NE;
        public static Direction SOUTH_EAST = Direction.SE;
        public static Direction SOUTH_WEST = Direction.SW;
        public static Direction NORTH_WEST = Direction.NW;

        private double heading;

        private Direction(double heading) {
            this.heading = heading;
        }

        public double heading() {
            return this.heading;
        }
    }

    // ----------------------------------------------------------------------
    public static class DataPoint {

        private Date date;
        private String location;
        private double minTemperature;	  // °C
        private double maxTemperature;	  // °C
        private double rainfall;         // mm
        private double sunshine;         // hours
        private double evaporation;      // mm/day
        private double windGustSpeed;    // km/hour
        private Direction windGustDirection;

        // Morning values at 9am
        private double morningTemperature;   // °C
        private double morningHumidity;      // %
        private double morningPressure;      // hpa
        private int morningCloudCover;    // oktas
        private double morningWindSpeed;     // km/hour
        private Direction morningWindDirection;

        // Afternoon values at 3pm
        private double afternoonTemperature; // °V
        private double afternoonHumidity;    // %
        private double afternoonPressure;    // hpa
        private int afternoonCloudCover;  // octas
        private double afternoonWindSpeed;   // km/hour
        private Direction afternoonWindDirection;

        private Boolean rainToday;
        private Boolean rainTomorrow;         // Answer
        private double rainfallTomorrow;     // Answer

        // --------------------------------------------------------------------
        public DataPoint(Date date,
                String location,
                double minTemperature,
                double maxTemperature,
                double rainfall,
                double sunshine,
                double evaporation,
                double windGustSpeed,
                Direction windGustDirection,
                double morningTemperature,
                double morningHumidity,
                double morningPressure,
                int morningCloudCover,
                double morningWindSpeed,
                Direction morningWindDirection,
                double afternoonTemperature,
                double afternoonHumidity,
                double afternoonPressure,
                int afternoonCloudCover,
                double afternoonWindSpeed,
                Direction afternoonWindDirection,
                Boolean rainToday,
                Boolean rainTomorrow,
                double rainfallTomorrow) {

            this.date = date;
            this.location = location;
            this.minTemperature = minTemperature;
            this.maxTemperature = maxTemperature;
            this.rainfall = rainfall;
            this.sunshine = sunshine;
            this.evaporation = evaporation;
            this.windGustSpeed = windGustSpeed;
            this.windGustDirection = windGustDirection; // Added because it was missing before.

            this.morningTemperature = morningTemperature;
            this.morningHumidity = morningHumidity;
            this.morningPressure = morningPressure;
            this.morningCloudCover = morningCloudCover;
            this.morningWindSpeed = morningWindSpeed;
            this.morningWindDirection = morningWindDirection;

            this.afternoonTemperature = afternoonTemperature;
            this.afternoonHumidity = afternoonHumidity;
            this.afternoonPressure = afternoonPressure;
            this.afternoonCloudCover = afternoonCloudCover;
            this.afternoonWindSpeed = afternoonWindSpeed;
            this.afternoonWindDirection = morningWindDirection;

            this.rainToday = rainToday;
            this.rainTomorrow = rainTomorrow;
            this.rainfallTomorrow = rainfallTomorrow;
        }

        // ----------------------------------------------------------------------
        public Date date() {
            return this.date;
        }

        public String location() {
            return this.location;
        }

        public double minTemperature() {
            return this.minTemperature;
        }

        public double maxTemperature() {
            return this.maxTemperature;
        }

        public double rainfall() {
            return this.rainfall;
        }

        public double hoursOfSunshine() {
            return this.sunshine;
        }

        public double evaporationRate() {
            return this.evaporation;
        }

        public double windGustSpeed() {
            return this.windGustSpeed;
        }

        public Direction windGustDirection() {
            return this.windGustDirection;
        }

        public double morningTemperature() {
            return this.morningTemperature;
        }

        public double morningHumidity() {
            return this.morningHumidity;
        }

        public double morningPressure() {
            return this.morningPressure;
        }

        public int morningCloudCover() {
            return this.morningCloudCover;
        }

        public double morningWindSpeed() {
            return this.morningWindSpeed;
        }

        public Direction morningWindDirection() {
            return this.morningWindDirection;
        }

        public double afternoonTemperature() {
            return this.afternoonTemperature;
        }

        public double afternoonHumidity() {
            return this.afternoonHumidity;
        }

        public double afternoonPressure() {
            return this.afternoonPressure;
        }

        public int afternoonCloudCover() {
            return this.afternoonCloudCover;
        }

        public double afternoonWindSpeed() {
            return this.afternoonWindSpeed;
        }

        public Direction afternoonWindDirection() {
            return this.afternoonWindDirection;
        }

        public Boolean rainToday() {
            return this.rainToday;
        }

        public Boolean rainTomorrow() {
            return this.rainTomorrow;
        }

        // ----------------------------------------------------------------------        
        public double getNumber(String field) {
            switch (field.toLowerCase()) {
                case "mintemperature":
                    return minTemperature();

                case "maxtemperature":
                    return maxTemperature();

                case "rainfall":
                    return rainfall();

                case "hoursofsunshine":
                    return hoursOfSunshine();

                case "evaporationrate":
                    return evaporationRate();

                case "gustspeed":
                    return windGustSpeed();

                case "morningtemperature":
                    return morningTemperature();

                case "morninghumidity":
                    return morningHumidity();

                case "morningpressure":
                    return morningPressure();

                case "morningwindspeed":
                    return morningWindSpeed();

                case "morningcloudcover":
                    return morningCloudCover();

                case "afternoontemperature":
                    return afternoonTemperature();

                case "afternoonhumidity":
                    return afternoonHumidity();

                case "afternoonpressure":
                    return afternoonPressure();

                case "afternoonwindspeed":
                    return afternoonWindSpeed();

                case "afternooncloudcover":
                    return afternoonCloudCover();

                default:
                    throw new IllegalArgumentException("Invalid field name: " + field);
            }
        }

        public Direction getDirection(String field) {
            switch (field.toLowerCase()) {
                case "gustdirection":
                    return windGustDirection();

                case "morningwinddirection":
                    return morningWindDirection();

                case "afternoonwinddirection":
                    return afternoonWindDirection();

                default:
                    throw new IllegalArgumentException("Invalid field name: " + field);
            }
        }

        public Boolean getBooleanValue(String field) {
            switch (field.toLowerCase()) {
                case "raintoday":
                    return rainToday();

                case "raintomorrow":
                    return rainTomorrow();

                default:
                    throw new IllegalArgumentException("Invalid field name: " + field);
            }
        }

        public boolean isMissing(String field) {
            switch (field.toLowerCase()) {
                case "date":
                    return date() == null;

                case "location":
                    return location() == null;

                case "mintemperature":
                case "maxtemperature":
                case "morningtemperature":
                case "afternoontemperature":
                    return getNumber(field) < -1000.0;

                case "rainfall":
                case "gustspeed":
                case "hoursofsunshine":
                case "evaporationrate":
                case "morninghumidity":
                case "morningpressure":
                case "morningwindspeed":
                case "morningcloudcover":
                case "afternoonhumidity":
                case "afternoonpressure":
                case "afternoonwindspeed":
                case "afternooncloudcover":
                    return getNumber(field) < 0.0;

                case "gustdirection":
                case "morningwinddirection":
                case "afternoonwinddirection":
                    return getDirection(field) == null;

                case "raintoday":
                case "raintomorrow":
                    return getBooleanValue(field) == null;

                default:
                    throw new IllegalArgumentException("Invalid field name: " + field);
            }
        }

        @Override
        public String toString() {
            return this.date + " "
                    + this.location + " "
                    + this.minTemperature + " "
                    + this.maxTemperature + " "
                    + this.rainfall + " "
                    + this.sunshine + " "
                    + this.evaporation + " "
                    + this.windGustSpeed + " "
                    + this.windGustDirection.heading() + " "
                    + this.morningTemperature + " "
                    + this.morningHumidity + " "
                    + this.morningPressure + " "
                    + this.morningCloudCover + " "
                    + this.morningWindSpeed + " "
                    + this.morningWindDirection.heading() + " "
                    + this.afternoonTemperature + " "
                    + this.afternoonHumidity + " "
                    + this.afternoonPressure + " "
                    + this.afternoonCloudCover + " "
                    + this.afternoonWindSpeed + " "
                    + this.afternoonWindDirection.heading() + " "
                    + this.rainToday + " "
                    + this.rainTomorrow + " "
                    + this.rainfallTomorrow;
        }
    }

    public static final String[] fields = {
        "Dummy", // Always equal to 1.0
        "Date", //
        "Location", //
        "MinTemperature", //
        "MaxTemperature", //
        "Rainfall", //
        "Sunshine", //
        "Evaporation", //
        "WindGustSpeed", //
        "WindGustDirection", //
        "MorningTemperature", //
        "MorningHumidity", //
        "MorningPressure", //
        "MorningCloudCover", //
        "MorningWindSpeed", //
        "MorningWindDirection", //
        "AfternoonTemperature", //
        "AfternoonHumidity", //
        "AfternoonPressure", // 
        "AfternoonCloudCover", //
        "AfternoonWindSpeed", //
        "AfternoonWindDirection", //
        "RainToday", // XX Boolean - not used in place of Rainfall XX
        "RainTomorrow", // XX ANSWERS - exclude XX 
        "RainfallTomorrow" // XX ANSWERS - exclude XX
    };

    public static final String[] locations = {
        "Adelaide",
        "Albany",
        "Albury",
        "AliceSprings",
        "BadgerysCreek",
        "Ballarat",
        "Bendigo",
        "Brisbane",
        "Cairns",
        "Canberra",
        "Cobar",
        "CoffsHarbour",
        "Dartmoor",
        "Darwin",
        "GoldCoast",
        "Hobart",
        "Katherine",
        "Launceston",
        "Melbourne",
        "MelbourneAirport",
        "Mildura",
        "Moree",
        "MountGambier",
        "MountGinini",
        "Newcastle",
        "Nhil",
        "NorahHead",
        "NorfolkIsland",
        "Nuriootpa",
        "PearceRAAF",
        "Penrith",
        "Perth",
        "PerthAirport",
        "Portland",
        "RichmondSale",
        "SalmonGums",
        "Sydney",
        "SydneyAirport",
        "Townsville",
        "Tuggeranong",
        "Uluru",
        "WaggaWagga",
        "Walpole",
        "Watsonia",
        "Williamtown",
        "Witchcliffe",
        "Wollongong",
        "Woomera"
    };
}
