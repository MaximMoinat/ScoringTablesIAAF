package iaaf;

import static org.junit.Assert.assertEquals;

import functions.IaafFunction;
import org.junit.Before;
import org.junit.Test;

public class EventScoringTableTest {

    private EventScoringTable tableLJ;
    private EventScoringTable table400;
    private EventScoringTable tableFictive;

    @Before
    public void setupTest() {
        tableLJ = new EventScoringTable(Gender.FEMALE, Event.LONG_JUMP);
        tableLJ.addScore(7.37,1300);
        tableLJ.addScore(7.36,1298);
        tableLJ.addScore(7.35,1295);
        tableLJ.addScore(7.34,1293);
        tableLJ.addScore(7.33,1291);
        tableLJ.addScore(7.32,1289);
        tableLJ.addScore(7.31,1287);
        tableLJ.addScore(7.29,1286);

        table400 = new EventScoringTable(Gender.MALE, Event.TRACK_400);
        table400.addScore(71.87,1300);
        table400.addScore(71.92,1299);
        table400.addScore(71.97,1298);
        table400.addScore(72.03,1297);
        table400.addScore(72.08,1296);
        table400.addScore(72.14,1295);
        table400.addScore(72.19,1294);
        table400.addScore(72.24,1290);
        table400.addScore(72.30,1288);
        table400.addScore(72.35,1287);

        // 0.000001 * (B5+1000)^2 + 100
        tableFictive = new EventScoringTable(Gender.MALE, Event.ROAD_100KM);
        tableFictive.addScore(4000, 125);
        tableFictive.addScore(5000, 136);
        tableFictive.addScore(6000, 149);
        tableFictive.addScore(7000, 164);
        tableFictive.addScore(8000, 181);
        tableFictive.addScore(9000, 200);
        tableFictive.addScore(10000, 221);
        tableFictive.addScore(11000, 244);
        tableFictive.addScore(12000, 269);
        tableFictive.addScore(13000, 296);
    }

    @Test
    public void testLookupPoints() {
        assertEquals("Direct point lookup failed",
                1286, (int) tableLJ.lookupPoints(7.29d));

        // Rule: return points for first score in table worse than given performance
        assertEquals("Inferred point lookup from distance failed",
                1286, (int) tableLJ.lookupPoints(7.30d));
        assertEquals("Inferred point lookup from time failed",
                1299, (int) table400.lookupPoints(71.9d));
    }

    @Test
    public void testLookupPerformance() {
        assertEquals("Direct performance lookup failed",
                7.37d, (double) tableLJ.lookupPerformance(1300), 0.009);

        // Rule: return performance for first points in table worse than given points
        assertEquals("Inferred performance lookup of distance failed",
                7.35d, (double) tableLJ.lookupPerformance(1296), 0.009);
        assertEquals("Inferred performance lookup of time failed",
                72.30d, (double) table400.lookupPerformance(1289), 0.009);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLookupPointsExceptionLowerBound() {
        tableLJ.lookupPoints(7.28);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLookupPointsExceptionUpperBound() {
        tableLJ.lookupPoints(7.38);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLookupPeformancesExceptionLowerBound() {
        table400.lookupPerformance(1280);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLookupPeformancesExceptionUpperBound() {
        table400.lookupPerformance(1500);
    }

    @Test
    public void testRegression() {
        tableFictive.doRegression();
        IaafFunction function = (IaafFunction) tableFictive.getFunction();

        // 0.000001 * x^2 + 0.002 * x + 101
        assertEquals("Polynomial Regression failed",1e-6, function.getA(), 1e-7);
        assertEquals("Polynomial Regression failed",2e-3, function.getB(), 1e-7);
        assertEquals("Polynomial Regression failed",101, function.getC(), 1e-7);

        // 0.000001 * (B5+1000)^2 + 100
        assertEquals("Iaaf constants calculation failed",1e-6, function.getIaafA(), 1e-7);
        assertEquals("Iaaf constants calculation failed",1000, function.getIaafB(), 1e-7);
        assertEquals("Iaaf constants calculation failed",100, function.getIaafC(), 1e-7);
    }

    @Test
    public void testFunctionCalculate() {
        tableFictive.doRegression();

        assertEquals("Point calculation failed",
                149, tableFictive.calculatePoints(6000));
        assertEquals("Point calculation failed",
                356, tableFictive.calculatePoints(15000));
        assertEquals("Point calculation failed",
                278, tableFictive.calculatePoints(12345));

        assertEquals("Performance calculation failed",
                6000, tableFictive.calculatePerformance(149), 1e-7);
        assertEquals("Performance calculation failed",
                9000, tableFictive.calculatePerformance(200), 1e-7);
    }

}
