package iaaf;

import java.util.Arrays;
import java.util.Locale;

public enum Event {
    TRACK_50 (50,"50m","time"),
    TRACK_50H (50,"50mH","time"),
    TRACK_55 (55,"55m","time"),
    TRACK_55H (55,"55mH","time"),
    TRACK_60 (60,"60m","time"),
    TRACK_60H (60,"60mH","time"),
    ROAD_10MILES (16093,"10 Miles","time"),
    ROAD_10KM (10000,"10 km","time"),
    TRACK_10000 (10000,"10000m","time"),
    TRACK_1000 (1000,"1000m","time"),
    TRACK_100 (100,"100m","time"),
    TRACK_100H (100,"100mH","time"),
    TRACK_110H (110,"110mH","time"),
    WALK_10KM (10000,"10km W","time"),
    ROAD_15KM (15000,"15 km","time"),
    TRACK_1500 (1500,"1500m","time"),
    TRACK_2MILES (3218,"2 Miles","time"),
    ROAD_20KM (20000,"20 km","time"),
    TRACK_2000 (2000,"2000m","time"),
    TRACK_2000SC (2000,"2000m SC","time"),
    TRACK_200 (200,"200m","time"),
    TRACK_3000 (3000,"3000m","time"),
    TRACK_3000SC (3000,"3000m SC","time"),
    TRACK_300 (300,"300m","time"),
    WALK_3KM (3000,"3km W","time"),
    TRACK_400 (400,"400m","time"),
    TRACK_400H (400,"400mH","time"),
    TRACK_4x100 (400,"4x100m","time"),
    TRACK_4x200 (800,"4x200m","time"),
    TRACK_4x400 (1600,"4x400m","time"),
    TRACK_5000 (5000,"5000m","time"),
    WALK_5KM (5000,"5km W","time"),
    TRACK_500 (500,"500m","time"),
    TRACK_600 (600,"600m","time"),
    TRACK_800 (800,"800m","time"),
    DISCUS_THROW (null,"DT","distance"),
    HEPTATHLON (null,"Heptathlon","multi"),
    HIGH_JUMP (null,"HJ","distance"),
    ROAD_HALF_MARATHON (21097,"HM","time"),
    HAMMER_THROW (null,"HT","distance"),
    JAVELIN_THROW (null,"JT","distance"),
    LONG_JUMP (null,"LJ","distance"),
    TRACK_1MILE (1609,"Mile","time"),
    POLE_VAULT (null,"PV","distance"),
    SHOT_PUT (null,"SP","distance"),
    TRIPLE_JUMP (null,"TJ","distance"),
    ROAD_MARATHON (42195,"Marathon","time"),
    DECATHLON (null,"Decathlon","multi"),
    PENTATHLON (null,"Pentathlon","multi");

    private final Integer distance;
    private final String iaafName;
    private final String performanceType;

    Event(Integer distance, String iaafName, String performanceType) {
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

    public String getPerformanceType() {
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
