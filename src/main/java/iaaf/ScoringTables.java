package iaaf;

import functions.IaafFunction;
import util.NestedHashMap;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created on 12-11-16.
 */
public class ScoringTables extends NestedHashMap<Gender,Event,EventScoringTable> {

    public void addScore(Gender gender, Event event, double performance, int points) {
        if (!this.containsKey(gender, event)) {
            this.put(gender, event, new EventScoringTable(gender, event));
        }

        this.get(gender, event).addScore(performance, points);
    }

    public void write(String outputFolder) throws IOException {
        this.write(outputFolder, "");
    }

    public void write(String outputFolder, String name) throws IOException {
        // File output for each gender
        for (Gender gender : this.keySet1()) {
            for (EventScoringTable scoreTable : this.get(gender).values()) {
                File outFile = new File(String.format("%s/Table %s - %s - %s.csv",
                        outputFolder,
                        name,
                        gender,
                        scoreTable.getEvent().getIaafName()
                ));
                if (outFile.getParentFile().mkdirs()) {
                    System.out.println("Created directory: " + outFile.getParent());
                }

                FileWriter fileWriter = new FileWriter(outFile);
                fileWriter.write(String.format("%s,%s%n", "performance", "points"));

                for (Entry<Double, Integer> score : scoreTable.getPerformancePoints().entrySet()) {
                    fileWriter.write(String.format("%.2f,%d%n",
                            score.getKey(),
                            score.getValue()
                    ));
                }
                fileWriter.close();
            }
        }
        System.out.println("Written conversion tables to: " + outputFolder);
    }

    public void writeFormulaConstants(String outputFolder) throws IOException {
        this.writeFormulaConstants(outputFolder, "");
    }

    public void writeFormulaConstants(String outputFolder, String name) throws IOException {
        // File per gender
        for (Gender gender : this.keySet1()) {
            File outFile = new File(String.format("%s/Constants %s - %s.csv",outputFolder, name, gender));
            if (outFile.getParentFile().mkdirs()) {
                System.out.println("Created directory: " + outFile.getParent());
            }

            FileWriter fileWriter = new FileWriter(outFile);
            fileWriter.write(String.format("%s,%s,%s,%s,%s,%s,%s%n", "event", "a", "b", "c", "a2", "b2", "c2"));

            for (EventScoringTable scoreTable : this.get(gender).values()) {
                scoreTable.doRegression();
                Event event = scoreTable.getEvent();
                IaafFunction function = scoreTable.getFunction();
                fileWriter.write(String.format("%s,%.10f,%.10f,%.10f,%.10f,%.10f,%.10f,%s,%s,%s%n",
                    event.getIaafName(),
                    function.getIaafA(),
                    function.getIaafB(),
                    function.getIaafC(),
                    function.getA(),
                    function.getB(),
                    function.getC(),
                    event,
                    event.getDistance() != null ? event.getDistance() : "",
                    event.getPerformanceType()

                ));
            }
            fileWriter.close();
            System.out.println("Written constants from regression analysis to: " + outFile.getAbsoluteFile());
        }

    }

    public static void main(String[] args) throws IOException {
        ScoringTables tableIndoor = ScoringTablesBuilder.readFromXls(new File("/src/main/resources/IAAF Scoring Tables of Athletics - Indoor 2017.xls"));
        tableIndoor.write("Indoor 2017");

        ScoringTables tableOutdoor = ScoringTablesBuilder.readFromXls(new File("/src/main/resources/IAAF Scoring Tables of Athletics - Outdoor 2017.xls"));
        tableOutdoor.write("Outdoor 2017");
    }

}
