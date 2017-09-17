package iaaf;

import java.util.Arrays;
import java.util.Locale;

public enum Event {
    TRACK_50 (50,"50m",PerformanceType.TIME),
    TRACK_50H (50,"50mH",PerformanceType.TIME),
    TRACK_55 (55,"55m",PerformanceType.TIME),
    TRACK_55H (55,"55mH",PerformanceType.TIME),
    TRACK_60 (60,"60m",PerformanceType.TIME),
    TRACK_60H (60,"60mH",PerformanceType.TIME),
    TRACK_100 (100,"100m",PerformanceType.TIME),
    TRACK_200 (200,"200m",PerformanceType.TIME),
    TRACK_300 (300,"300m",PerformanceType.TIME),
    TRACK_400 (400,"400m",PerformanceType.TIME),
    TRACK_500 (500,"500m",PerformanceType.TIME),
    TRACK_600 (600,"600m",PerformanceType.TIME),
    TRACK_800 (800,"800m",PerformanceType.TIME),
    TRACK_1000 (1000,"1000m",PerformanceType.TIME),
    TRACK_1500 (1500,"1500m",PerformanceType.TIME),
    TRACK_1MILE (1609,"Mile",PerformanceType.TIME),
    TRACK_2000 (2000,"2000m",PerformanceType.TIME),
    TRACK_3000 (3000,"3000m",PerformanceType.TIME),
    TRACK_2MILES (3218,"2 Miles",PerformanceType.TIME),
    TRACK_5000 (5000,"5000m",PerformanceType.TIME),
    TRACK_10000 (10000,"10000m",PerformanceType.TIME),
    TRACK_100H (100,"100mH",PerformanceType.TIME),
    TRACK_110H (110,"110mH",PerformanceType.TIME),
    TRACK_400H (400,"400mH",PerformanceType.TIME),
    TRACK_2000SC (2000,"2000m SC",PerformanceType.TIME),
    TRACK_3000SC (3000,"3000m SC",PerformanceType.TIME),
    TRACK_4x100 (400,"4x100m",PerformanceType.TIME),
    TRACK_4x200 (800,"4x200m",PerformanceType.TIME),
    TRACK_4x400 (1600,"4x400m",PerformanceType.TIME),
    ROAD_10MILES (16093,"10 Miles",PerformanceType.TIME),
    ROAD_10KM (10000,"10 km",PerformanceType.TIME),
    ROAD_15KM (15000,"15 km",PerformanceType.TIME),
    ROAD_20KM (20000,"20 km",PerformanceType.TIME),
    ROAD_25KM (25000,"25 km",PerformanceType.TIME),
    ROAD_30KM (30000,"30 km",PerformanceType.TIME),
    ROAD_100KM (100000,"100 km",PerformanceType.TIME),
    ROAD_HALF_MARATHON (21097,"HM",PerformanceType.TIME),
    ROAD_MARATHON (42195,"Marathon",PerformanceType.TIME),
    WALK_3KM (3000,"3km W",PerformanceType.TIME),
    WALK_5KM (5000,"5km W",PerformanceType.TIME),
    WALK_10KM (10000,"10km W",PerformanceType.TIME),
    WALK_20KM (20000,"20km W",PerformanceType.TIME),
    WALK_30KM (30000,"30km W",PerformanceType.TIME),
    WALK_35KM (35000,"35km W",PerformanceType.TIME),
    WALK_50KM (50000,"50km W",PerformanceType.TIME),
    HIGH_JUMP (null,"HJ",PerformanceType.DISTANCE),
    LONG_JUMP (null,"LJ",PerformanceType.DISTANCE),
    TRIPLE_JUMP (null,"TJ",PerformanceType.DISTANCE),
    DISCUS_THROW (null,"DT",PerformanceType.DISTANCE),
    HAMMER_THROW (null,"HT",PerformanceType.DISTANCE),
    JAVELIN_THROW (null,"JT",PerformanceType.DISTANCE),
    POLE_VAULT (null,"PV",PerformanceType.DISTANCE),
    SHOT_PUT (null,"SP",PerformanceType.DISTANCE),
    HEPTATHLON (null,"Heptathlon",PerformanceType.MULTI),
    PENTATHLON (null,"Pentathlon",PerformanceType.MULTI),
    DECATHLON (null,"Decathlon",PerformanceType.MULTI);

    private final Integer distance;
    private final String iaafName;
    private final PerformanceType performanceType;

    Event(Integer distance, String iaafName, PerformanceType performanceType) {
        this.distance = distance;
        this.iaafName = iaafName;
        this.performanceType = performanceType;
    }

    public Integer getDistance() {
        return distance;
    }

    public String getIaafName() {
        return iaafName;
    }

    public PerformanceType getPerformanceType() {
        return performanceType;
    }

    public static Event fromString(String name) {
        for (Event event : Event.values()) {
            if (event.iaafName.equalsIgnoreCase(name)) {
                return event;
            }
        }

        // Alternative translations
        switch (name.toLowerCase()) {
            case "pent.":
                return Event.PENTATHLON;
            case "hept.":
                return Event.HEPTATHLON;
            default:
                System.out.println(name);
                return Event.valueOf(name);
        }
    }

    public int compareDistance(Event anotherEvent) {
        if (this.getDistance() == null && anotherEvent.getDistance() == null) {
            return this.compareTo(anotherEvent);
        } else if (this.getDistance() == null) {
            return 1;
        } else if (anotherEvent.getDistance() == null) {
            return -1;
        }
        return this.getDistance().compareTo(anotherEvent.getDistance());
    }

    public String toStringWithDistance() {
        if (this.getDistance() == null) {
            return super.toString();
        }

        return String.format(Locale.UK,"%-22s (%5s meter)",
                super.toString(),
                this.getDistance()
        );
    }

    public static void main(String[] args) {
        Arrays.stream(Event.values()).sorted(Event::compareDistance).map(Event::toStringWithDistance).forEach(System.out::println);
    }
}
