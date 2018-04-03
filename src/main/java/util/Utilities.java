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

    /**
     * Three options:
     *  - < minute: represented as numeric value (24.58)
     *  - < hour: represented as string ('1:51.91')
     *  - >= hour: represented as date (Sun Dec 31 01:06:25 CET 1899)
     * @param cell
     * @return
     */
    public static Double parsePerformanceFromCell(Cell cell) throws NumberFormatException {
        if (isDateCell(cell)) {
            return performanceDateCellToSeconds(cell);
        }

        if (isNumericCell(cell)) {
            return cell.getNumericCellValue();
        }

        // In all other cases, treat as string
        return performanceStringCellToSeconds(cell);
    }

    private static boolean isNumericCell(Cell cell) {
        // Numeric cell type, but not a date (which is also of numeric type)
        return cell.getCellType() == Cell.CELL_TYPE_NUMERIC && !isDateCell(cell);
    }

    private static boolean isDateCell(Cell cell) {
        // Very lenient date recognition. Three 2-4 characters separated by dashes or slashes
        return cell.toString().matches(".{2,4}[/-].{2,4}[/-].{2,4}");
    }

    private static Double performanceDateCellToSeconds(Cell cell) {
        double days = cell.getNumericCellValue();
        // Round to seconds. Assumption: performances above the hour are in seconds precision
        // Note: this assumption does NOT hold for 10,000m women (<140 points)
        return (double) Math.round(days*24*3600);
    }

    private static Double performanceStringCellToSeconds(Cell cell) throws NumberFormatException {
        return parseTime(cell.getStringCellValue());
    }

    /**
     * Casts a performance to a double if possible
     * If time format, parses the time.
     * @param performance
     * @return
     * @throws NumberFormatException
     */
    public static Double parseTime(String performance) throws NumberFormatException {
        performance = performance.trim();
        if (performance.contains(":")) {
            try {
                return parseTimeSemicolon(performance);
            } catch (Exception e) {
                throw new NumberFormatException( String.format("Can't process '%s'", performance) );
            }
        } else if (performance.matches(".*\\.\\d\\d\\..*")) {
            try {
                return parseTimeDot(performance);
            } catch (Exception e) {
                throw new NumberFormatException( String.format("Can't process '%s'", performance) );
            }
        } else {
            throw new NumberFormatException( String.format("Can't process '%s' as it is not a recognised format", performance) );
        }
    }

    /**
     * Parses time in the format hh:mm:ss.tt
     * @param performance
     * @return
     * @throws NumberFormatException
     */
    public static Double parseTimeSemicolon(String performance) throws NumberFormatException {
        String[] parts = performance.split(":");
        int hours, minutes, seconds;
        switch(parts.length) {
            case 1:
                return Double.valueOf(performance);
            case 2:
                int minutes = Integer.parseInt( parts[0] );
                Double seconds = Double.parseDouble( parts[1] );
                break;
            case 3:
                int hours = Integer.parseInt( parts[0] );
                int minutes = Integer.parseInt( parts[1] );
                Double seconds = Double.parseDouble( parts[2] );
                break;
            default:
                throw new NumberFormatException("Unexpected number of parts.");
        }
        return hours*3600 + minutes*60 + seconds;
    }

    /**
     * Parses time in the format [m]m.ss.t
     * @param performance
     * @return
     * @throws NumberFormatException
     */
    public static Double parseTimeDot(String performance) throws NumberFormatException {
        String[] parts = performance.split(".");
        int hours, minutes, seconds, tenths;
        switch(parts.length) {
            case 2:
                int seconds = Integer.parseInt( parts[0] );
                int tenths = Integer.parseInt( parts[1] );
                break;
            case 3:
                int minutes = Integer.parseInt( parts[0] );
                int seconds = Integer.parseInt( parts[1] );
                int tenths = Integer.parseInt( parts[2] );
                break;
            case 4:
                int hours = Integer.parseInt( parts[0] );
                int minutes = Integer.parseInt( parts[1] );
                int seconds = Integer.parseInt( parts[2] );
                int tenths = Integer.parseInt( parts[3] );
                break;
            default:
                throw new NumberFormatException("Unexpected number of parts.");
        }
        return hours*3600 + minutes*60 + seconds + tenths/10d;
    }
}
