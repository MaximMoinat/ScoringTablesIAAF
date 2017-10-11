package util;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class UtilitiesTest {

    @Test
    public void testParseTime() {
        assertEquals(111.91d, Utilities.parseTime("1:51.91"), 0.001);
        assertEquals(3599.99d, Utilities.parseTime("59:59.99"), 0.001);
        assertEquals(7199.99d, Utilities.parseTime("1:59:59.99"), 0.001);
        assertEquals(12.34d, Utilities.parseTime("12.34"), 0.001);
    }

    @Test
    public void testParsePerformanceFromCellSeconds() {
        Cell cell;

        cell = createTimeCell(0,0,51.91);
        assertEquals(51.91d, Utilities.parsePerformanceFromCell(cell), 0.001);

        cell = createTimeCell(0,0,55.5);
        assertEquals(55.5d, Utilities.parsePerformanceFromCell(cell), 0.001);
    }

    @Test
    public void testParsePerformanceFromCellMinutes() {
        Cell cell;

        cell = createTimeCell(0,1,51.91);
        assertEquals(111.91d, Utilities.parsePerformanceFromCell(cell), 0.001);

        cell = createTimeCell(0,30,55.5);
        assertEquals(1855.5d, Utilities.parsePerformanceFromCell(cell), 0.001);
    }

    @Test
    public void testParsePerformanceFromCellHours() {
        // All results minus a day to correct for cell date creation
        double CORRECTION_DAY = 24*3600;
        Cell cell;

        cell = createTimeCell(1,0,0);
        assertEquals(3600d, Utilities.parsePerformanceFromCell(cell) - CORRECTION_DAY, 0.001);

        cell = createTimeCell(3,30,55);
        assertEquals(12655d, Utilities.parsePerformanceFromCell(cell) - CORRECTION_DAY, 0.001);

        cell = createTimeCell(13,8,4);
        assertEquals(47284d, Utilities.parsePerformanceFromCell(cell) - CORRECTION_DAY, 0.001);
    }

    private Cell createTimeCell(int hours, int minutes, double seconds) {
        HSSFWorkbook wb = new HSSFWorkbook();
        CreationHelper creationHelper = wb.getCreationHelper();
        Sheet sheet = wb.createSheet("new sheet");

        // Create a row and put some cells in it. Rows are 0 based.
        Row row = sheet.createRow(0);

        // Create a cell and put a value in it.
        Cell cell = row.createCell(0);

        if (hours > 0) {
            cell.setCellValue(new Date(0, 0,1, hours, minutes, (int) seconds));
            CellStyle style = wb.createCellStyle();
            style.setDataFormat(creationHelper.createDataFormat().getFormat("m/d/yy h:mm"));
            cell.setCellStyle(style);
        } else if (minutes > 0) {
            cell.setCellValue(String.format("%d:%05.2f", minutes, seconds));
        } else {
            cell.setCellValue(seconds);
        }
        return cell;
    }
}
