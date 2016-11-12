/**
 * Created by Maxim on 12-11-16.
 */
import xyz.maximin.iaaf.ScoringTable;

public class ScoringIAAFMain {

    public static void main(String[] args) {
        ScoringTable table = new ScoringTable("Honderd meter");

        table.addScore( 6.5, 23);
        table.addScore( 16.5, 34);
        table.addScore( 26.5, 40);
        table.addScore( 26.5, 55);


//        System.out.println( table.getPerformances() );
//        System.out.println( table.getPoints() );
        System.out.println(table);


    }

}
