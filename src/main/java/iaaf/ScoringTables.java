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
            File outFile = new File("src/main/resources/" + outFilename + " - " + gender + ".csv");
            System.out.println("Writing to: " + outFile.getAbsoluteFile());
            FileWriter fileWriter = new FileWriter(outFile);
            fileWriter.write(String.format("%s,%s,%s%n", "event", "performance", "points"));

            for (EventScoringTable scoreTable : this.get(gender).values()) {
                for (Entry<Double, Integer> score : scoreTable.getScorings().entrySet()) {
                    fileWriter.write(String.format("%s,%.2f,%d%n",
                            scoreTable.getEvent().getIaafName(),
                            score.getKey(),
                            score.getValue()
                    ));
                }
            }
            fileWriter.close();
        }
    }

    public static void main(String[] args) throws IOException {
        ScoringTables table = ScoringTableBuilder.readFromXls("IAAF Scoring Tables of Athletics - Indoor 2017.xls");
        table.write("Indoor 2017");
    }

}
