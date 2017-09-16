package iaaf;

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

    public void write(String outFilename) throws IOException {
        // File output for each gender
        for (Gender gender : this.keySet1()) {
            for (EventScoringTable scoreTable : this.get(gender).values()) {
                File outFile = new File(String.format("src/main/output/%s - %s - %s.csv",
                        outFilename,
                        gender,
                        scoreTable.getEvent().getIaafName()
                ));
                System.out.println("Writing to: " + outFile.getAbsoluteFile());
                FileWriter fileWriter = new FileWriter(outFile);
                fileWriter.write(String.format("%s,%s%n", "performance", "points"));

                for (Entry<Double, Integer> score : scoreTable.getScorings().entrySet()) {
                    fileWriter.write(String.format("%.2f,%d%n",
                            score.getKey(),
                            score.getValue()
                    ));
                }
                fileWriter.close();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        ScoringTables tableIndoor = ScoringTableBuilder.readFromXls("IAAF Scoring Tables of Athletics - Indoor 2017.xls");
        tableIndoor.write("Indoor 2017");

        ScoringTables tableOutdoor = ScoringTableBuilder.readFromXls("IAAF Scoring Tables of Athletics - Outdoor 2017.xls");
        tableOutdoor.write("Outdoor 2017");
    }

}
