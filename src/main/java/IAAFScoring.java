//import model.EventScoringTable;

import functions.ABCFormule;
import functions.ExtrapolateFunction;
import functions.IaafFunction;
import functions.PolynomialRegression;
import iaaf.EventScoringTable;
import iaaf.ScoringFileConverter;
import iaaf.ScoringTables;
import java.util.Map;
import visual.GraphWindow;

/**
 * Created by Maxim on 12-11-16.
 */
public class IAAFScoring {

    public static void main(String[] args) {
//        EventScoringTable table = new EventScoringTable("Honderd meter", "Mannen");
//
//        table.addScore( 6.5, 23);
//        table.addScore( 16.5, 34);
//        table.addScore( 26.5, 40);
//        table.addScore( 33, 40);
//        table.addScore( 26.5, 55);
//
//        ScoringTables master = new ScoringTables();
//
//        master.addScoringTable( table );
//
//        System.out.println(master);

        ScoringFileConverter converter = new ScoringFileConverter();
        ScoringTables fullTable;
        try {
            fullTable = converter.convert("IAAF Scoring Tables of Athletics - Indoor.xls");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        // Setup
        EventScoringTable tjMen = fullTable.getEventScoringTable("TJ", "Men");
        tjMen.setFunctie(new ABCFormule());

        EventScoringTable tjWomen = fullTable.getEventScoringTable("TJ", "Women");
        tjWomen.setFunctie(new ABCFormule());

        // Threading with runnables
        Runnable runTjMen = new Runnable(){
            @Override
            public void run(){
                System.out.println("Runnable tjMen running");
                PolynomialRegression regression = new PolynomialRegression(tjMen.getScoresAsDouble(), tjMen.getPointsAsDouble(), 2);
                System.out.println(regression);
                int sum = 0;
                for( int r: regression.getResidualsCorrected() ) {
                    sum += Math.abs(r);
                }
                System.out.println("Total error is " + sum);
//                ExtrapolateFunction.Extrapolate(tjMen);
            }
        };

        Runnable runTjWomen = new Runnable(){
            @Override
            public void run(){
                System.out.println("Runnable tjWomen running");
                PolynomialRegression regression = new PolynomialRegression(tjWomen.getScoresAsDouble(), tjWomen.getPointsAsDouble(), 2);
//                System.out.println(regression);
//                ExtrapolateFunction.Extrapolate(tjWomen);
            }
        };

        Thread thread1 = new Thread(runTjMen);
        thread1.start();
        Thread thread2 = new Thread(runTjWomen);
        thread2.start();

//        EventScoringTable tjMen = fullTable.getEventScoringTable("TJ", "Men");
//        tjMen.setFunctie(new ABCFormule());
//        GraphWindow.createAndShowGui( tjMen.getScorings(), ExtrapolateFunction.Extrapolate(tjMen, 1, 100) );
        
//        EventScoringTable tjWomen = fullTable.getEventScoringTable("TJ", "Women");
//        tjWomen.setFunctie(new ABCFormule());
//        GraphWindow.createAndShowGui(tjWomen.getScorings(),ExtrapolateFunction.Extrapolate(tjWomen));
        System.out.println(tjMen.toString());
//        System.out.println( iaaf.ScoringFileConverter.parseTime("1:5.22") );
    }

}
