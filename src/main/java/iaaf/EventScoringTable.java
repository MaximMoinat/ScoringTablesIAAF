package iaaf;

import functions.Function;
import functions.IaafFunction;
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
    private IaafFunction scoringFunction;

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
        performance = Math.round(performance * 100d)/100d;

        if (performance < Collections.min(performancePoints.keySet())
                || performance> Collections.max(performancePoints.keySet())) {
            throw new IllegalArgumentException("Performance out of bounds");
        }

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
        if (points < Collections.min(performancePoints.values())
                || points> Collections.max(performancePoints.values())) {
            throw new IllegalArgumentException("Points out of bounds");
        }

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

    public Function getFunction() {
        return scoringFunction;
    }

    public int getCount() {
        return performancePoints.size();
    }

    public void doRegression() {
        System.out.println("Starting Polynomial Regression for: " + gender + event);
        PolynomialRegression regression = new PolynomialRegression(this.getScoresAsDouble(), this.getPointsAsDouble(), 2);
        this.scoringFunction = new IaafFunction(regression.beta(2),regression.beta(1),regression.beta(0));
    }

    public int calculatePoints(double performance) {
        if (this.scoringFunction == null) {
            throw new IllegalArgumentException("Please execute regression first with .doRegression()");
        }
        return (int) Math.round(this.scoringFunction.calculatePoints(performance));
    }

    public double calculatePerformance(int points) {
        if (this.scoringFunction == null) {
            throw new IllegalArgumentException("Please execute regression first with .doRegression()");
        }
        return this.scoringFunction.calculatePerformance(points);
    }

    public String TableName(){
        return String.format( "%s(%s)_%s",
                getEvent(),
                getGender(),
                getFunction().getClass().getName());
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
