import java.util.List;
import java.util.Map;

public class LastRace extends Race {
    public LastRace(String destination, String distance, Map<Integer, Time> times, List<Integer> top) {
        super(destination, distance, times, top);
    }

    public int[] points() { return new int[] { 0, 0, 0, 0 }; }
}