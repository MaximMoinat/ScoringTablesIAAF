//import model.EventScoringTable;

import iaaf.ScoringTables;
import iaaf.ScoringTablesBuilder;

import java.io.File;
import java.io.IOException;

/**
 * Created by Maxim on 12-11-16.
 */
public class IAAFScoring {

    /**
     * Read IAAF points tables
     * @param args Path to folder where IAAF points tables as .xls can be found
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        File path = new File(args[0]);

        for (File inFile : path.listFiles((dir, name) -> name.endsWith(".xls"))) {
            System.out.println("Processing: " + inFile.getAbsolutePath());
            ScoringTables tables = ScoringTablesBuilder.readFromXls(inFile);

            // Get identifying name of this file, e.g. 'Indoor 2017'
            String outPath = inFile.getParent();
            String name = inFile.getName()
                    .replace("IAAF Scoring Tables of Athletics -","")
                    .replace(".xls","")
                    .trim();

            System.out.println("Writing point conversion tables");
            tables.write(outPath + "/tables", name);

            System.out.println("Writing formula constants");
            tables.writeFormulaConstants(outPath + "/constants", name);
        }

//        ScoringFileConverter converter = new ScoringFileConverter();
//        ScoringTables indoor2014, outdoor2014, indoor2017, outdoor2017;
//        try {
////            indoor2014 = converter.convert("IAAF Scoring Tables of Athletics - Indoor 2014.xls");
////            outdoor2014 = converter.convert("IAAF Scoring Tables of Athletics - Outdoor 2014.xls");
////            indoor2017 = converter.convert("IAAF Scoring Tables of Athletics - Indoor 2017.xls");
////            outdoor2017 = converter.convert("IAAF Scoring Tables of Athletics - Indoor 2017.xls");
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            return;
//        }
//        indoor2017.keySet().stream().sorted().forEach(System.out::println);
//        System.out.println(outdoor2017.toString());

//
//        // Setup
//        EventScoringTable tjMen = fullTable.getEventScoringTable("TJ", "Men");
//        tjMen.setFunctie(new ABCFormule());
//
//        EventScoringTable tjWomen = fullTable.getEventScoringTable("TJ", "Women");
//        tjWomen.setFunctie(new ABCFormule());
//
//        // Threading with runnables
//        Runnable runTjMen = new Runnable(){
//            @Override
//            public void run(){
//                System.out.println("Runnable tjMen running");
//                PolynomialRegression regression = new PolynomialRegression(tjMen.getScoresAsDouble(), tjMen.getPointsAsDouble(), 2);
//                System.out.println(regression);
//                int sum = 0;
//                for( int r: regression.getResidualsCorrected() ) {
//                    sum += Math.abs(r);
//                }
//                System.out.println("Total error is " + sum);
////                ExtrapolateFunction.Extrapolate(tjMen);
//            }
//        };
//
//        Runnable runTjWomen = new Runnable(){
//            @Override
//            public void run(){
//                System.out.println("Runnable tjWomen running");
//                PolynomialRegression regression = new PolynomialRegression(tjWomen.getScoresAsDouble(), tjWomen.getPointsAsDouble(), 2);
////                System.out.println(regression);
////                ExtrapolateFunction.Extrapolate(tjWomen);
//            }
//        };
//
//        Thread thread1 = new Thread(runTjMen);
//        thread1.start();
//        Thread thread2 = new Thread(runTjWomen);
//        thread2.start();
//
////        EventScoringTable tjMen = fullTable.getEventScoringTable("TJ", "Men");
////        tjMen.setFunctie(new ABCFormule());
////        GraphWindow.createAndShowGui( tjMen.getPerformancePoints(), ExtrapolateFunction.Extrapolate(tjMen, 1, 100) );
//
////        EventScoringTable tjWomen = fullTable.getEventScoringTable("TJ", "Women");
////        tjWomen.setFunctie(new ABCFormule());
////        GraphWindow.createAndShowGui(tjWomen.getPerformancePoints(),ExtrapolateFunction.Extrapolate(tjWomen));
//        System.out.println(tjMen.toString());
//        System.out.println( iaaf.ScoringFileConverter.parseTime("1:5.22") );
    }

//    public ScoringTables scoresToCsv(String filename) {
//        ScoringFileConverter converter = new ScoringFileConverter(filename);
//        converter.load();
//        converter.write();
//    }

}
