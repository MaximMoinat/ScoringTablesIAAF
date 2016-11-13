import iaaf.EventScoringTable;
import iaaf.ScoringTables;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Convert IAAF scoring table to simple csv table
 * TODO:
 * - Men/Women scheiding => Zelfde onderdeel, zelfde punten weer.
 */
public class ScoringFileConverter {
    private Row currentRow;
    private List<String> currentHeader;
    private int pointsIndex;

    private FileWriter mFileWriter;
    private ScoringTables mScoringTables;

    public ScoringFileConverter() {
        this("output.csv");
    }

    public ScoringFileConverter(String outFile) {
        // File output
        try {
            mFileWriter = new FileWriter(new File(outFile));
        } catch (IOException ioe ) {
            ioe.printStackTrace();
        }

        // Object output
        mScoringTables = new ScoringTables();
    }

    public ScoringTables readFromXls(String filename) throws FileNotFoundException, IOException {
        // Open file
        File f = new File("src/main/resources/" + filename);
        System.out.println( "Opening file: " + f.getAbsoluteFile() );
        FileInputStream file = new FileInputStream( f );

        //Get the workbook instance for XLS file
        HSSFWorkbook workbook = new HSSFWorkbook(file);

        //Get first sheet from the workbook
        for(int i=0; i < workbook.getNumberOfSheets(); i++) {
            // Get next sheet
            HSSFSheet sheet = workbook.getSheetAt(i);

            // Odd sheet (even index), points in first column.
            // Even sheet (odd index), points in last column.
            if ( i % 2 == 0 ) {
                pointsIndex = 0;
            } else {
                pointsIndex = -1;
            }
            processSheet(sheet);
        }
        mFileWriter.flush();
        mFileWriter.close();

        return mScoringTables;
    }

    public void processSheet(HSSFSheet sheet) {
//        System.out.println( String.format( "'%s'", sheet.getSheetName() ) );
        Iterator<Row> rowIterator = sheet.iterator();

        // Header
        currentHeader = rowToArray( rowIterator.next() );
//        System.out.println(currentHeader);
        if (currentHeader.get(0).contains("\n") ) {
            System.out.printf( "'%s'%n", sheet.getSheetName() );
            System.out.println("WARNING: this sheets header contains an enter. Does it contain an extra row?");
        }

        // Iterate over rows
        while( rowIterator.hasNext() ) {
            currentRow = rowIterator.next();
            processRow(currentRow);
        }
    }

    public void processRow(Row row) {
        // Get the row as arrayList
        List<String> rowList = rowToArray(row);
        int n = rowList.size();

        // Skip empty rows
        if ( n <= 1 ) {
            return;
        }

        // Points
        int pointsColumnIndex;
        if ( pointsIndex == 0 ) {
            pointsColumnIndex = 0;
        } else {
            pointsColumnIndex = n-1;
        }
        String pointsResult = rowList.get( pointsColumnIndex );

        for( int i = 0; i < n; i++ ) {
            if ( i == pointsColumnIndex ) {
                continue;
            }
            String colName = currentHeader.get(i);
            String performance = rowList.get(i);

            // If not numeric, continue.
            Double performanceDouble;
            Integer pointsInt;
            try {
                performanceDouble = parseTime( performance );
                pointsInt = Double.valueOf( pointsResult ).intValue();
            } catch (NumberFormatException e) {
//                e.printStackTrace();
                continue;
            }

            // Write strings to file
            try {
                mFileWriter.write(String.format("%s,%s,%s\n", colName, performance, pointsResult));
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

            // Create lookup table with processed values
            EventScoringTable eventScoringTable;
            if ( mScoringTables.containsEvent(colName) ) {
                eventScoringTable = mScoringTables.getEventScoringTable( colName );
            } else {
                eventScoringTable = new EventScoringTable( colName );
                mScoringTables.addScoringTable( eventScoringTable );
            }
            eventScoringTable.addScore( performanceDouble, pointsInt );
        }
    }

    public static Double parseTime(String performance) throws NumberFormatException {
        try {
            return Double.valueOf( performance );
        } catch (NumberFormatException nfe) {
            try {
                String[] parts = performance.split(":");

                int minutes = Integer.parseInt(parts[0]);
                Double seconds = Double.parseDouble(parts[1]);

                return minutes*60 + seconds;

            } catch (Exception e) {
                throw new NumberFormatException( String.format("Can't process '%s'", performance) );
            }
        }
    }

    /**
     * Row to simple String array. All types are converted to strings.
     * @param row
     * @return
     */
    public List<String> rowToArray(Row row) {
        Iterator<Cell> cellIterator = row.cellIterator();
        List<String> result = new ArrayList<>();
        while ( cellIterator.hasNext() ) {
            result.add( cellIterator.next().toString() );
        }
        return result;
    }

//            try {
//                System.out.print( String.format( "%.2f", currentCell.getNumericCellValue() ) );
//            } catch (IllegalStateException e) {
//                System.out.print( String.format("'%s'", currentCell.toString() ) );
//            }

}
