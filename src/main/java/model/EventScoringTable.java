package model;

import tools.PolynomialRegression;
import tools.ScoringTablesBuilder;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.TreeBidiMap;

import java.io.File;
import java.io.IOException;
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

    public double[] getInverseScoresAsDouble() {
        return performancePoints
                .keySet()
                .stream()
                .mapToDouble(Double::doubleValue)
                .map(score -> 1/score)
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

    public IaafFunction getFunction() {
        return scoringFunction;
    }

    public int getCount() {
        return performancePoints.size();
    }

    public void doRegression() {
//        System.out.println("Starting Polynomial Regression for: " + gender + event);
        // TODO: proper switch between models (inverse relation vs quadratic)
        double[] scores = this.getInverseScoresAsDouble(); // this.getScoresAsDouble()
        double[] points = this.getPointsAsDouble();
        int degree = 1; //2
        PolynomialRegression regression = new PolynomialRegression(scores, points, degree);
        scoringFunction = new IaafFunction(regression.beta(2),regression.beta(1),regression.beta(0));
        scoringFunction.setR2(regression.R2());
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

    public String functionToString() {
        return this.scoringFunction.toString(event.getPerformanceType());
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
        ScoringTables tables = ScoringTablesBuilder.readFromXls(new File("/src/main/resources/IAAF Scoring Tables of Athletics - Outdoor 2014.xls"));
        EventScoringTable tripleJumpScores = tables.get(Gender.MALE, Event.TRIPLE_JUMP);
        tripleJumpScores.doRegression();
        System.out.println(tripleJumpScores.functionToString());
        System.out.println(tripleJumpScores.getFunction().getA());
        System.out.println(tripleJumpScores.getFunction().getB());
        System.out.println(tripleJumpScores.getFunction().getC());
    }
}
