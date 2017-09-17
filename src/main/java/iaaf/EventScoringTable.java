package iaaf;

import functions.Function;
import functions.PolynomialRegression;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.TreeBidiMap;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created on 12-11-16.
 */
public class EventScoringTable {
    private static final Logger logger = Logger.getLogger(EventScoringTable.class.getName());
    private Gender gender;
    private Event event;
    private BidiMap<Double,Integer> performancePoints;
    private Function functie;
    private PolynomialRegression regression;

    public EventScoringTable(Gender gender, Event event) {
        this.gender = gender;
        this.event = event;
        performancePoints = new TreeBidiMap<>();
    }

    public EventScoringTable(String genderName, String eventName) {
        this(Gender.fromString(genderName), Event.fromString(eventName));
    }

    public void addScore(double performance, int point) {
        // Exit and print warnings if either performance or points were already added before.
        if ( performancePoints.containsValue(point) ) {
            logger.warning(String.format("'%d' points was already added to '%s'.%n", point, event));
            return;
        }

        if ( performancePoints.containsKey(performance) ) {
            logger.warning(String.format("Performance of '%.2f' was already added to '%s'.%n", performance, event));
            return;
        }
        performancePoints.put(performance, point);
    }

    /**
     * Looks up points in the table for a given performance.
     * If performance not in table, get closest worse performance that is in the table.
     * @param performance
     * @return number of points or null if not found
     * // TODO: tests
     */
    public Integer lookupPoints(double performance) {
        int i = 0;
        while (true) {
            if (this.performancePoints.containsKey(performance)) {
                return this.performancePoints.get(performance);
            }

            // Prevent floating point error by rounding to appropriate decimals
            switch (this.event.getPerformanceType()) {
                case TIME:
                    performance += 0.01d;
                    performance = Math.round(performance * 100d)/100d;
                    break;
                case DISTANCE:
                    performance -= 0.01d;
                    performance = Math.round(performance * 100d)/100d;
                    break;
                case MULTI:
                    performance--;
                    performance = Math.round(performance);
                    break;
            }

            // If nothing found after 10 cycles, abort
            if (i > 10) {
                logger.warning("Could not find points for the given performance");
                return null;
            }
            i++;
        }
    }

    /**
     * Looks up the performance belonging to the given points.
     * If points not in table, get closest worse performance
     * @param points
     * @return performance or null if no match
     * // TODO: tests
     */
    public Double lookupPerformance(int points) {
        int i = 0;
        while (true) {
            if (this.performancePoints.containsValue(points)) {
                return this.performancePoints.getKey(points);
            }

            points--;

            // If nothing found after 10 cycles, abort
            if (i > 10) {
                logger.warning("Could not find points for the given performance");
                return null;
            }
            i++;
        }
    }

    public double[] getPointsAsDouble() {
        return performancePoints
                .values()
                .stream()
                .mapToDouble(Integer::doubleValue)
                .toArray();
    }

    public double[] getScoresAsDouble() {
        return performancePoints
                .keySet()
                .stream()
                .mapToDouble(Double::doubleValue)
                .toArray();
    }

    public Map<Double, Integer> getPerformancePoints() {
        return performancePoints;
    }

    public Event getEvent() {
        return event;
    }

    public Gender getGender() {
        return gender;
    }

    public Function getFunctie() {
        return functie;
    }

    public void setFunctie(Function functie) {
        this.functie = functie;
    }

    public int getCount() {
        return performancePoints.size();
    }

    public void doRegression() {
        System.out.println("Starting Polynomial Regression for: " + gender + event);
        regression = new PolynomialRegression(this.getScoresAsDouble(), this.getPointsAsDouble(), 2);
    }

    public void printRegression() {
        if (regression == null) {
            this.doRegression();
        }

        BigDecimal a = new BigDecimal(regression.beta(2));
        BigDecimal b = new BigDecimal(regression.beta(1));
        BigDecimal c = new BigDecimal(regression.beta(0));

        if (a.equals(BigDecimal.ZERO)) {
            // simple bx + c
            System.out.println(String.format("%-15s- %6s: %9.6f x %s %12.6f",
                    event,
                    gender,
                    b,
                    c.compareTo(BigDecimal.ZERO) > 0 ? '+' : '-',
                    c.abs()
            ));
            return;
        };

        BigDecimal two  = new BigDecimal(2);
        BigDecimal four = new BigDecimal(4);

        // Reformatting to get constants from ax^2 + bx + c to ai(bi-x)^2 + ci
        BigDecimal ai = a;
        // bi = b/2a
        BigDecimal bi = b.divide(a.multiply(two),BigDecimal.ROUND_HALF_EVEN).negate();
        // c = c - b^2/4a
        BigDecimal ci = c.subtract(b.pow(2).divide((a.multiply(four)),BigDecimal.ROUND_HALF_EVEN));


        if (this.event.getPerformanceType().equals(PerformanceType.TIME)) {
            System.out.println(String.format("%-15s- %6s: %9.6f (%12.6f - t)^2 %s %12.6f",
                    event,
                    gender,
                    ai,
                    bi,
                    ci.compareTo(BigDecimal.ZERO) > 0 ? '+' : '-',
                    ci.abs()
            ));
        } else {
            System.out.println(String.format("%-15s- %6s: %9.6f (x %s %12.6f)^2 %s %12.6f",
                    event,
                    gender,
                    ai,
                    bi.compareTo(BigDecimal.ZERO) > 0 ? '-' : '+',
                    bi.abs(),
                    ci.compareTo(BigDecimal.ZERO) > 0 ? '+' : '-',
                    ci.abs()
            ));
        }

    }

    public String TableName(){
        return String.format( "%s(%s)_%s",
                getEvent(),
                getGender(),
                getFunctie().getClass().getName());
    }
    
    @Override
    public String toString() {
        return String.format( "%s(%s): %s",
                getEvent(),
                getGender(),
                performancePoints.toString()
        );
    }

    public static void main(String[] args) throws IOException{
        ScoringTables tableIndoor = ScoringTablesBuilder.readFromXls("IAAF Scoring Tables of Athletics - Outdoor 2017.xls");
//        tableIndoor.get(Gender.MALE, Event.TRACK_100).printRegression();
//        tableIndoor.get(Gender.MALE, Event.DISCUS_THROW).printRegression();
//        tableIndoor.get(Gender.MALE, Event.DECATHLON).printRegression();
//        tableIndoor.get(Gender.FEMALE, Event.HEPTATHLON).printRegression();
//        tableIndoor.get(Gender.MALE, Event.TRACK_200).printRegression();
//        System.out.println(tableIndoor.get(Gender.MALE, Event.TRACK_200).regression.predict(19.19));
        System.out.println(tableIndoor.get(Gender.FEMALE, Event.HEPTATHLON).lookupPoints(7000));
        System.out.println(tableIndoor.get(Gender.FEMALE, Event.HEPTATHLON).lookupPerformance(1272));
        System.out.println(tableIndoor.get(Gender.FEMALE, Event.LONG_JUMP).lookupPerformance(1297));
    }
}
