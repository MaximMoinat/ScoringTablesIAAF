package iaaf;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Maxim on 12-11-16.
 */
public class EventScoringTable {
    private String mEventName;
    private Set<Integer> mPoints ;
    private Map<Double,Integer> mScorings;

    public EventScoringTable(String eventName) {
        mEventName = eventName;
        mPoints = new HashSet<>();
        mScorings = new HashMap<>();
    }

    public void addScore(double performance, int point) {
        boolean pointIsInserted = mPoints.add(point);

        // Print warnings if either performance or points were already added before.
        if ( ! pointIsInserted ) {
            System.out.println( String.format("WARNING: '%d' points was already added", point) );
            return;
        }

        if ( mScorings.containsKey( performance ) ) {
            System.out.println( String.format("WARNING: Performance of '%.2f' was already added", performance) );
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

    @Override
    public String toString() {
        return String.format( "%s: %s",
                getEventName(),
                mScorings.toString()
        );
    }
}
