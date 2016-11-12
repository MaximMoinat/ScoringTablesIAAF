import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Convert IAAF scoring table to simple csv table
 * TODO:
 * - Write to csv
 * - Men/Women scheiding
 */
public class ScoringFileConverter {
    private Row currentRow;
    private Cell currentCell;
    private List<String> currentHeader;
    private int pointsIndex;

    public ScoringFileConverter() {
        // TODO: filewriter
    }

    public void readFromXls(String filename) throws FileNotFoundException, IOException {
        // Open file
        File f = new File("src/main/resources/" + filename);
        System.out.println( "Opening file: " + f.getAbsoluteFile() );
        FileInputStream file = new FileInputStream( f );

        //Get the workbook instance for XLS file
        HSSFWorkbook workbook = new HSSFWorkbook(file);

        //Get first sheet from the workbook
        for(int i=0; i < workbook.getNumberOfSheets(); i++) {
            HSSFSheet sheet = workbook.getSheetAt(i);
            if (i==0){
                continue;
            }

            // Odd sheet (even index), points in first column.
            // Even sheet (odd index), points in last column.
            if ( i % 2 == 0 ) {
                pointsIndex = 0;
            } else {
                pointsIndex = -1;
            }
            processSheet(sheet);
        }
    }

    public void processSheet(HSSFSheet sheet) {
//        System.out.println( String.format( "'%s'", sheet.getSheetName() ) );
        Iterator<Row> rowIterator = sheet.iterator();

        // Header
        currentHeader = rowToArray( rowIterator.next() );
        if (currentHeader.get(0).contains("\n") ) {
            System.out.println( String.format( "'%s'", sheet.getSheetName() ) );
            System.out.println("WARNING: this sheets header contains an enter. Does it contain an extra row?");
        }

        // Iterate over rows
        while( rowIterator.hasNext() ) {
            currentRow = rowIterator.next();
            processRow(currentRow);
        }
    }

    public void processRow(Row row) {
        List<String> rowList = rowToArray(row);
        int n = rowList.size();

        // Points
        String pointsResult;
        int skipColIndex;
        if ( pointsIndex == 0 ) {
            pointsResult = rowList.get(0);
            skipColIndex = 0;
        } else {
            pointsResult = rowList.get(n - 1);
            skipColIndex = n-1;
        }

        for( int i = 0; i < n; i++ ) {
            if ( i == skipColIndex ) {
                continue;
            }
            String colName = currentHeader.get(i);
            String performance = rowList.get(i);

            // If not numeric, continue.
            try {
                Double.valueOf(performance);
            } catch (NumberFormatException e) {
//                e.printStackTrace();
                continue;
            }

//            System.out.println(String.format("%s,%s,%s", colName, performance, pointsResult));
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
