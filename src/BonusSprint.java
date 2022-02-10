import java.util.List;
import java.util.Map;

public class BonusSprint extends Race {
    public BonusSprint(String destination, String distance, Map<Integer, Time> times, List<Integer> top) {
        super(destination, distance, times, top);
    }

    public int[] points() { return new int[] { 5, 3, 1, 0 }; }
}