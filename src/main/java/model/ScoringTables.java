package model;

import tools.ScoringTablesBuilder;
import util.NestedHashMap;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

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
                    fileWriter.write(String.format(Locale.ENGLISH,
                            "%.2f,%d%n",
                            score.getKey(),
                            score.getValue()
                    ));
                }
                fileWriter.close();
            }
        }
        System.out.println("Written conversion tables to: " + outputFolder);
    }

    public void writeCombined(String outputFolder, String name) throws IOException {
        for (Gender gender : this.keySet1()) {
            File outFile = new File(String.format("%s/Table %s - %s.csv",
                    outputFolder,
                    name,
                    gender
            ));
            if (outFile.getParentFile().mkdirs()) {
                System.out.println("Created directory: " + outFile.getParent());
            }

            FileWriter fileWriter = new FileWriter(outFile);
            fileWriter.write("points");

            for (EventScoringTable scoreTable : this.get(gender).values()) {
                fileWriter.write(String.format(",%s", scoreTable.getEvent()));
            }
            fileWriter.write('\n');

            for (int i = 1; i <= 1200; i++) {
                fileWriter.write(String.format(Locale.ENGLISH, "%d", i));
                for (EventScoringTable scoreTable : this.get(gender).values()) {
                    fileWriter.write(String.format(Locale.ENGLISH,
                            ",%.2f",
                            scoreTable.lookupPerformance(i))
                    );
                }
                fileWriter.write('\n');
            }
            fileWriter.close();
        }
        System.out.println("Written overview table.");
    }

    public void writeFormulaConstants(String outputFolder) throws IOException {
        this.writeFormulaConstants(outputFolder, "");
    }
    public void writeFormulaConstants(String outputFolder, String name) throws IOException {
        writeFormulaConstants(outputFolder, name, false);
    }

    public void writeFormulaConstants(String outputFolder, String name, boolean doWriteMetaColumns) throws IOException {
        // File per gender
        for (Gender gender : this.keySet1()) {
            File outFile = new File(String.format("%s/Constants %s - %s.csv",outputFolder, name, gender));
            if (outFile.getParentFile().mkdirs()) {
                System.out.println("Created directory: " + outFile.getParent());
            }

            FileWriter fileWriter = new FileWriter(outFile);
            fileWriter.write(String.format("%s,%s,%s,%s,%s", "event", "formula", "a", "b", "c"));
            if (doWriteMetaColumns)
                fileWriter.write(String.format(",%s,%s,%s,%s,%s,%s%n", "a2", "b2", "c2","enum_name", "distance", "performance_type"));
            else
                fileWriter.write('\n');

            for (EventScoringTable scoreTable : this.get(gender).values()) {
                scoreTable.doRegression();
                Event event = scoreTable.getEvent();
                IaafFunction function = scoreTable.getFunction();

                fileWriter.write(String.format(Locale.ENGLISH,
                        "%s,%s,%.10f,%.10f,%.10f",
                        event,
                        "IAAF_hungarian",
                        function.getIaafA(),
                        function.getIaafB(),
                        function.getIaafC()
                        )
                );

                if (doWriteMetaColumns)
                    fileWriter.write(String.format(Locale.ENGLISH,
                        ",%.10f,%.10f,%.10f,%s,%s,%s,%.10f,%n",
                        function.getA(),
                        function.getB(),
                        function.getC(),
                        event,
                        event.getDistance() != null ? event.getDistance() : "",
                        event.getPerformanceType(),
                        function.getR2()
                    ));
                else
                    fileWriter.write('\n');
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
