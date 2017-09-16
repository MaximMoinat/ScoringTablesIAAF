package util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Utilities {

    /**
     * Row to simple String array. All types are converted to strings.
     * @param row
     * @return
     */
    public static List<String> rowToArray(Row row) {
        Iterator<Cell> cellIterator = row.cellIterator();
        List<String> result = new ArrayList<>();
        while ( cellIterator.hasNext() ) {
            result.add( cellIterator.next().toString().trim() );
        }
        return result;
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
}
