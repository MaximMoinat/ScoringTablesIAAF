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
        File path = new File(args[0]);

        for (File inFile : path.listFiles((dir, name) -> name.endsWith(".xls"))) {
            System.out.println("Processing: " + inFile.getAbsolutePath());
            ScoringTables tables = ScoringTablesBuilder.readFromXls(inFile);

            // Get identifying name of this file, e.g. 'Indoor 2017'
            String outPath = inFile.getParent();
            String name = inFile.getName()
                    .replace("IAAF Scoring Tables of Athletics -", "")
                    .replace(".xls", "")
                    .trim();

            System.out.println("Writing point conversion tables");
            tables.write(outPath + "/scoring_tables", name);

            System.out.println("Writing formula constants");
            tables.writeFormulaConstants(outPath + "/formula_constants", name);
        }
    }
}
