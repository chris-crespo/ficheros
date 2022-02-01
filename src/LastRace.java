import java.time.LocalTime;
import java.util.List;

public class LastRace extends Race {
    public LastRace(String destination, String distance, List<Pair<Integer, LocalTime>> times) {
        super(destination, distance, times);
    }
}