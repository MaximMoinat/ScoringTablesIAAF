package iaaf;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Stores Scoring of one event.
 * Created on 12-11-16.
 */
public class EventScoringTable {
    private String mEventName;
    private String mGender;
    private Set<Integer> mPoints ;
    private Map<Double,Integer> mScorings;

    public EventScoringTable(String eventName, String gender) {
        mEventName = eventName;
        mGender = gender;
        mPoints = new HashSet<>();
        mScorings = new TreeMap<>();
    }

    public void addScore(double performance, int point) {
        boolean pointIsInserted = mPoints.add(point);

        // Exit and print warnings if either performance or points were already added before.
        if ( ! pointIsInserted ) {
            System.out.printf( "WARNING: '%d' points was already added to '%s'.%n", point, mEventName );
            return;
        }

        if ( mScorings.containsKey( performance ) ) {
            System.out.printf( "WARNING: Performance of '%.2f' was already added to '%s'.%n", performance, mEventName );
            return;
        }

        mScorings.put(performance, point);

    }

    public Set<Integer> getPoints() {
        return mPoints;
    }

    public Map<Double, Integer> getScorings() {
        return mScorings;
    }

    public String getEventName() {
        return mEventName;
    }

    public String getGender() {
        return mGender;
    }

    @Override
    public String toString() {
        return String.format( "%s(%s): %s",
                getEventName(),
                getGender(),
                mScorings.toString()
        );
    }
}
