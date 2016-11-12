package iaaf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.poi.ss.usermodel.Cell;
//import org.apache.poi.ss.usermodel.Row;

/**
 * Created by Maxim on 12-11-16.
 */
public class ScoringTables {
    Map<String,EventScoringTable> mScoringTables;

    public ScoringTables() {
        mScoringTables = new HashMap<>();
    }

    public void addScoringTable(EventScoringTable table) {
        String key = table.getEventName();
        if( mScoringTables.containsKey(key) ) {
            System.out.println( key + " already exists" );
            return;
        }

        mScoringTables.put( key, table );
    }

    public void read(String filename) throws FileNotFoundException, IOException {
        ProcessFromFile processor = new ProcessFromFile();
        processor.readFromXls(filename);
    }

    @Override
    public String toString() {
        return mScoringTables.values().toString();
    }

    public class ProcessFromFile {

        public void readFromXls(String filename) throws FileNotFoundException, IOException {

            // Open file
            File f = new File("src/main/resources/" + filename);
            System.out.println( "Opening file: " + f.getAbsoluteFile() );
            FileInputStream file = new FileInputStream( f );

            //Get the workbook instance for XLS file
            HSSFWorkbook workbook = new HSSFWorkbook(file);

            //Get first sheet from the workbook
            HSSFSheet sheet = workbook.getSheetAt(111);

            System.out.println( String.format( "'%s'", sheet.getSheetName() ) );

            // Get first row from sheet
            Row row = sheet.getRow(1);
            Iterator<Cell> cellIterator = row.cellIterator();
            while ( cellIterator.hasNext() ) {
                Cell cell = cellIterator.next();
                try {
                    System.out.println( String.format( "%.2f", cell.getNumericCellValue() ) );
                } catch (IllegalStateException e) {
                    System.out.println( e.getMessage() );
                    System.out.println( String.format("'%s'", cell.toString() ) );
                }
//            System.out.println( String.format("'%s'", cell.toString() ) );
            }

            //Get iterator to all the rows in current sheet
            Iterator<Row> rowIterator = sheet.iterator();
//        while( rowIterator.hasNext() ) {
//            row = rowIterator.next();
//
//            Iterator<Cell> cellIterator = row.cellIterator();
//
//        }
            // TODO: methods (in innerclass?) to process each sheet and row.
        }

        public void processSheet(HSSFSheet sheet) {

        }
    }

}
