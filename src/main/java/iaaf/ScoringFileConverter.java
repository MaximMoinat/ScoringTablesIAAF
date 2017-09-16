package iaaf;


import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Convert IAAF scoring table to simple csv table.
 * Input file needs to be .xls
 * TODO:
 *  - Format h:mm:ss.SS. .toString() and returns this as date string ('31-Dec-1899'). Damn you Excel...
 */
public class ScoringFileConverter {
    private List<String> currentHeader;
    private int pointsIndex;

    private FileWriter mFileWriter;
    private ScoringTables mScoringTables;
    private Gender currentGender;

    public ScoringFileConverter() {
        // Object output
        mScoringTables = new ScoringTables();
        currentGender = Gender.UNKNOWN;
    }

    /**
     * Reads the table, parses and writes to a
     * flat csv file (with the same name as input file)
     * @param filename
     * @return
     * @throws IOException
     */
    public ScoringTables convert(String filename) throws IOException {
        // Check for extension
        if ( ! filename.endsWith(".xls") ) {
            throw new IOException("Only excel .xls is supported. Please convert to xls.");
        }

        // Open file
        File inFile = new File("src/main/resources/" + filename);
        System.out.println( "Opening file: " + inFile.getAbsoluteFile() );
        FileInputStream file = new FileInputStream( inFile );

        // File output (csv)
        File outFile = new File("src/main/resources/" + filename.split("\\.")[0] + ".csv" );
        System.out.println( "Writing to: " + outFile.getAbsoluteFile() );
        mFileWriter = new FileWriter( outFile );
        mFileWriter.write(String.format("%s,%s,%s,%s\n", "event", "gender", "performance", "points"));

        //Get the workbook instance for XLS file
        HSSFWorkbook workbook = new HSSFWorkbook(file);
        int nSheets = workbook.getNumberOfSheets();

        //Get first sheet from the workbook
        currentGender = Gender.MALE;
        for(int i=0; i < nSheets; i++) {
            // Get next sheet
            HSSFSheet sheet = workbook.getSheetAt(i);

            // Odd sheet (even index), points in first column.
            // Even sheet (odd index), points in last column.
            if ( i % 2 == 0 ) {
                pointsIndex = 0;
            } else {
                pointsIndex = -1;
            }

            // First half is men, second half is women
            if ( i >= nSheets/2 ) {
                currentGender = Gender.FEMALE;
            }

            processSheet(sheet);
        }
        mFileWriter.flush();
        mFileWriter.close();

        return mScoringTables;
    }

    private void processSheet(HSSFSheet sheet) {
//        System.out.println( String.format( "'%s'", sheet.getSheetName() ) );
        Iterator<Row> rowIterator = sheet.iterator();

        // First row is the header
        currentHeader = rowToArray( rowIterator.next() );
//        System.out.println(currentHeader);
        if (currentHeader.stream().anyMatch(x -> x.contains("\n"))) {
            System.out.printf( "'%s'%n", sheet.getSheetName() );
            System.out.println(currentHeader);
            System.out.println("WARNING: the header of this sheet contains an enter. Does it contain an extra row?");
        } else if (currentHeader.size() <= 2) {
            System.out.printf( "'%s'%n", sheet.getSheetName() );
            System.out.println(currentHeader);
            System.out.println("WARNING: the header of this sheet is too short. Is it correctly formed?");
        }
        // Iterate over rows
        int i = 0;
        while( rowIterator.hasNext() ) {
            processRow( rowIterator.next() );
            i++;
        }
    }

    private void processRow(Row row) {
        // Get the row as arrayList
        List<String> rowList = rowToArray(row);
        int n = rowList.size();

        // Skip empty rows
        if ( n <= 1 ) {
            return;
        }

        // Points column index (determine for each row separately
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

            // If not numeric, continue.
            // Note: this also silently ignores if the performance is badly formatted
            // Todo: distinguish between bad and no ('-' or '') input
            Double performanceDouble;
            Integer pointsInt;
            String performance = rowList.get(i);
            try {
                performanceDouble = parseTime( performance );
                pointsInt = Double.valueOf( pointsResult ).intValue();
            } catch (NumberFormatException e) {
//                System.out.println( e.getMessage() );
                continue;
            }

            // Parse event
            String eventName = currentHeader.get(i);
            Event event = Event.fromString(eventName);

            // Write strings to file
            try {
                mFileWriter.write(String.format("%s,%s,%s,%s\n", eventName, currentGender, performance, pointsResult));
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

            // Create lookup table with processed values
            EventScoringTable eventScoringTable;
            if (mScoringTables.containsKey(currentGender, event)) {
                eventScoringTable = mScoringTables.get(currentGender, event);
            } else {
                eventScoringTable = new EventScoringTable(currentGender, event);
                mScoringTables.addScoringTable(eventScoringTable);
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
                if ( parts.length == 2 ) {
                    int minutes = Integer.parseInt( parts[0] );
                    Double seconds = Double.parseDouble( parts[1] );
                    return minutes*60 + seconds;
                } else if (parts.length == 3) {
                    int hours = Integer.parseInt( parts[0] );
                    int minutes = Integer.parseInt( parts[1] );
                    Double seconds = Double.parseDouble( parts[2] );
                    return hours*3600 + minutes*60 + seconds;
                } else {
                    throw new NumberFormatException();
                }
            } catch (Exception e) {
//                System.out.println( e.getMessage() );
//                e.printStackTrace();
                throw new NumberFormatException( String.format("Can't process '%s'", performance) );
            }
        }
    }

    /**
     * Row to simple String array. All types are converted to strings.
     * @param row
     * @return
     */
    private List<String> rowToArray(Row row) {
        Iterator<Cell> cellIterator = row.cellIterator();
        List<String> result = new ArrayList<>();
        while ( cellIterator.hasNext() ) {
            result.add( cellIterator.next().toString().trim() );
        }
        return result;
    }

//            try {
//                System.out.print( String.format( "%.2f", currentCell.getNumericCellValue() ) );
//            } catch (IllegalStateException e) {
//                System.out.print( String.format("'%s'", currentCell.toString() ) );
//            }

}
