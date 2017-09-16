package iaaf;

import util.NestedHashMap;

/**
 * Created on 12-11-16.
 */
public class ScoringTables extends NestedHashMap<Gender,Event,EventScoringTable> {

    public void addScoringTable(EventScoringTable table) {
        Event event = table.getEvent();
        Gender gender = table.getGender();

        if( this.containsKey(gender, event) ) {
            System.out.println(String.format("%s-%s already exists", gender, event ));
            return;
        }

        this.put(gender, event, table);
    }

    public void write(String outFilename) {

    }

}
