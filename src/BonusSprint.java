import java.time.LocalTime;
import java.util.List;

public class BonusSprint extends Race {
    public BonusSprint(String destination, String distance, List<Pair<Integer, LocalTime>> times) {
        super(destination, distance, times);
    }
}