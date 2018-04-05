//import model.EventScoringTable;

import model.ScoringTables;
import tools.ScoringTablesBuilder;

import java.io.File;
import java.io.IOException;

/**
 * Created by Maxim on 12-11-16.
 */
public class IaafScoring {
    /**
     * Read IAAF points tables from xls and write conversion tables per event/gender.
     * Perform a second order regression on the tables and write the constants
     * @param args Path to folder where IAAF points tables as .xls can be found
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        File inPath = new File(args[0]);
        String outPath = args[1];

        // Loop through all xls files in the given path
        for (File inFile : inPath.listFiles((dir, name) -> name.endsWith(".xls"))) {
            // Get identifying name of this file, e.g. 'Indoor 2017'
            String outName = inFile.getName()
                    .replace("IAAF Scoring Tables of Athletics -", "")
                    .replace(".xls", "")
                    .trim();
            // NOTE: before removing this, check EventScoringTable.doRegression(). Is set to inverse relationship model
            if (outName.contains("2017")) {
                continue;
            }

            System.out.println("Processing: " + outName);
            ScoringTables tables = ScoringTablesBuilder.readFromXls(inFile);

            System.out.println("Writing point conversion tables");
            tables.write(outPath + "/scoring_tables", outName);
            tables.writeCombined(outPath + "/scoring_tables", outName);

            System.out.println("Writing formula constants");
            tables.writeFormulaConstants(outPath, outName, true);
        }
    }
}
