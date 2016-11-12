//import model.EventScoringTable;
import iaaf.EventScoringTable;
import iaaf.ScoringTables;

/**
 * Created by Maxim on 12-11-16.
 */

public class Example {

    public static void main(String[] args) {
        EventScoringTable table = new EventScoringTable("Honderd meter");

        table.addScore( 6.5, 23);
        table.addScore( 16.5, 34);
        table.addScore( 26.5, 40);
        table.addScore( 33, 40);
        table.addScore( 26.5, 55);

        ScoringTables master = new ScoringTables();

        master.addScoringTable( table );

        System.out.println(master);

        ScoringFileConverter converter = new ScoringFileConverter();
        try {
            converter.readFromXls( "IAAF Scoring Tables of Athletics - Indoor.xls" );
        } catch (Exception e) {
            System.out.println( e.getMessage() );
        }


    }



}
