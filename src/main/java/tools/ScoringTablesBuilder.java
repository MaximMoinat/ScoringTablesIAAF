package tools;

import java.util.logging.Logger;

import model.Event;
import model.Gender;
import model.ScoringTables;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.*;
import java.util.Iterator;
import java.util.List;

import static util.Utilities.parsePerformanceFromCell;
import static util.Utilities.parseTime;
import static util.Utilities.rowToArray;

public class ScoringTablesBuilder {
    private static final Logger logger = Logger.getLogger(ScoringTablesBuilder.class.getName());

    private HSSFWorkbook workbook;
    private ScoringTables scoringTables;

    private List<String> currentHeader;
    private boolean pointsInFirstColumn;
    private Gender currentGender;

    /**
     * Load a ScoringTables object
     */
    public static ScoringTables readFromXls(File inFile) throws IOException {
        // Check for extension
        if ( ! inFile.getAbsolutePath().endsWith(".xls") ) {
            throw new IOException("Only excel .xls is supported. Please convert to xls.");
        }

        ScoringTablesBuilder builder = new ScoringTablesBuilder(inFile);
        return builder.build();
    }

    private ScoringTablesBuilder(File inFile) throws IOException {
        // Create scoring tables instance
        scoringTables = new ScoringTables();

        //Get the workbook instance
        FileInputStream file = new FileInputStream(inFile);
        workbook = new HSSFWorkbook(file);
        file.close();
    }

    private ScoringTables build() {
        Iterator<Sheet> sheets = workbook.iterator();
        int sheetNumber = 1;
        while( sheets.hasNext() ) {
            // Odd sheet index: points in first column. Event sheet index: points in last column.
            pointsInFirstColumn = sheetNumber % 2 == 1;
            // First half is men, second half is women
            currentGender = sheetNumber <= workbook.getNumberOfSheets()/2 ? Gender.MALE : Gender.FEMALE;

            readSheet(sheets.next());

            sheetNumber++;
        }
        return scoringTables;
    }

    private void readSheet(Sheet sheet) {
        Iterator<Row> rows = sheet.iterator();

        // First row is the header
        currentHeader = rowToArray(rows.next());

        // Some checking
        if (currentHeader.stream().anyMatch(x -> x.contains("\n"))) {
            System.out.println(currentHeader);
            logger.warning(String.format(
                    "The header of sheet '{}' contains an enter. Does it contain an extra row?",
                    sheet.getSheetName()
            ));
        } else if (currentHeader.size() <= 2) {
            System.out.println(currentHeader);
            logger.warning(String.format(
                    "The header of sheet '%s' is too short. Is it correctly formed?",
                    sheet.getSheetName()
            ));
        }

        // Iterate over rows
        int i = 0;
        while(rows.hasNext()) {
            processRow(rows.next());
            i++;
        }
    }

    private void processRow(Row row) {
        List<String> columns = rowToArray(row);

        // Skip rows without columns (or just one column)
        if (columns.size() <= 1) {
            return;
        }

        // Location of column with points
        int pointsColumnIndex = pointsInFirstColumn ? 0 : columns.size() - 1;
        String pointsRaw = columns.get(pointsColumnIndex);

        Integer points;
        try {
            // String parsed as ###.0, so read as double
            points = Double.valueOf(pointsRaw).intValue();
        } catch (NumberFormatException e) {
            return;
        }

        for (int i = 0; i < columns.size(); i++) {
            if (i == pointsColumnIndex) {
                continue;
            }

            // If not numeric, continue.
            String cellString = columns.get(i);
            if (cellString == null || cellString.equals("-") || cellString.isEmpty()) {
                continue;
            }

            Double performance;
            try {
                performance = parsePerformanceFromCell(row.getCell(i));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                continue;
            }

            // Add to table
            scoringTables.addScore(currentGender, getCurrentEvent(i), performance, points);
        }
    }

    private Event getCurrentEvent(int index) {
        // Parse event enum from header
        String eventName = currentHeader.get(index);
        return Event.fromString(eventName);
    }

    public static void main(String[] args) throws IOException {
        ScoringTables table = ScoringTablesBuilder.readFromXls(new File("src/main/resources/IAAF Scoring Tables of Athletics - Outdoor 2017.xls"));
        System.out.println(table.get(Gender.FEMALE,Event.TRACK_5000).toString());
    }
}
