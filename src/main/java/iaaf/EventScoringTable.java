package iaaf;

import functions.Function;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.TreeBidiMap;

import java.util.*;

/**
 * Created on 12-11-16.
 */
public class EventScoringTable {
    private Gender gender;
    private Event event;
    private BidiMap<Double,Integer> performancePoints;
    private Function functie;

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
            System.out.printf( "WARNING: '%d' points was already added to '%s'.%n", point, event);
            return;
        }

        if ( performancePoints.containsKey(performance) ) {
            System.out.printf( "WARNING: Performance of '%.2f' was already added to '%s'.%n", performance, event);
            return;
        }
        performancePoints.put(performance, point);
    }

    public double[] getPointsAsDouble() {
        return performancePoints
                .values()
                .stream()
                .mapToDouble(Integer::doubleValue)
                .toArray();
    }

    public Double[] getScoresAsDouble() {
        return performancePoints
                .keySet()
                .toArray(new Double[this.getCount()]);
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
}
