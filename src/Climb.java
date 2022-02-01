import java.time.LocalTime;
import java.util.List;

public class Climb extends Race {
    public Climb(String destination, String distance, List<Pair<Integer, LocalTime>> times) {
        super(destination, distance, times);
    }
}