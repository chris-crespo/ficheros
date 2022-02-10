import java.util.List;
import java.util.Map;

public class Climb extends Race {
    private static int[] firstCatPoints  = new int[] { 20, 15, 10, 5 };
    private static int[] secondCatPoints = new int[] { 10, 5, 3, 0 };
    private static int[] thirdCatPoints  = new int[] { 5, 3, 1, 0 };
    private final int category;

    public Climb(String destination, String distance, int category, Map<Integer, Time> times, List<Integer> top) {
        super(destination, distance, times, top);
        this.category = category;
    }

    public int category() { return category; }

    public int[] points() {
        return switch (category) {
            case 1 -> firstCatPoints;
            case 2 -> secondCatPoints;
            default -> thirdCatPoints;
        };
    }
}