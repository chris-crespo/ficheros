import java.time.LocalTime;
import java.util.List;

public record TimeRanking(List<Pair<Integer, LocalTime>> position) { }
