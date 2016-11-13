package iaaf;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Maxim on 12-11-16.
 */
public class ScoringTables {
    Map<String,EventScoringTable> mScoringTables;

    public ScoringTables() {
        mScoringTables = new TreeMap<>();
    }

    public void addScoringTable(EventScoringTable table) {
        String key = table.getEventName();
        if( mScoringTables.containsKey(key) ) {
            System.out.println( key + " already exists" );
            return;
        }

        mScoringTables.put( key, table );
    }

    public EventScoringTable getEventScoringTable( String eventName ) {
        return mScoringTables.get( eventName );
    }

    public boolean containsEvent( String eventName ) {
        return mScoringTables.containsKey( eventName );
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
