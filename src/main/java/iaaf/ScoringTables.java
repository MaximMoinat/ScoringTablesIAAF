package iaaf;

import java.util.Map;
import java.util.TreeMap;

/**
 * Stores the EventScoringTables for easy retrieval.
 * Created on 12-11-16.
 */
public class ScoringTables {
    Map<String,EventScoringTable> mScoringTables;

    public ScoringTables() {
        mScoringTables = new TreeMap<>();
    }

    public void addScoringTable(EventScoringTable table) {
        String key = createKey( table.getEventName(), table.getGender() );

        if( mScoringTables.containsKey(key) ) {
            System.out.println( key + " already exists" );
            return;
        }

        mScoringTables.put( key, table );
    }

    private String createKey(String eventName, String gender ) {
        return String.format("%s-%s", eventName, gender);
    }

    public EventScoringTable getEventScoringTable( String eventName, String gender ) {
        return mScoringTables.get( createKey(eventName, gender) );
    }

    public boolean containsEvent( String eventName, String gender ) {
        return mScoringTables.containsKey( createKey(eventName, gender) );
    }

    @Override
    public String toString() {
        String result = "";
        for( EventScoringTable value : mScoringTables.values() ) {
            result += value.toString() + "\n";
        }
        return result;
    }

}
