import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class Race {
    private final String destination;
    private final String distance;
    private final List<Pair<Integer, LocalTime>> times;

    public Race(String destination, String distance, List<Pair<Integer, LocalTime>> times) {
        this.destination = destination;
        this.distance    = distance;
        this.times       = times;
    }

    public String destination() { return destination; }
    public String distance() { return distance; }
    public List<Pair<Integer, LocalTime>> times() { return times; }

    public static Race parse(List<String> lines) {
        var headerFields = lines.get(0).split(": ");
        var raceKind = headerFields[0];
        var raceInfo = headerFields[1].split(" - ");
        var times = lines.stream()
            .skip(1)
            .takeWhile(str -> Character.isDigit(str.charAt(0)))
            .map(str -> {
                var fields = str.split(", ");
                return new Pair<Integer, LocalTime>(
                    Integer.parseInt(fields[0]), 
                    LocalTime.parse(fields[1]));
            })
            .collect(Collectors.toList());

        return switch (raceKind) {
            case "Meta volante" -> new BonusSprint(raceInfo[0], raceInfo[1], times);
            case "Puerto" -> new Climb(raceInfo[0], raceInfo[1], times);
            default -> new LastRace(raceInfo[0], raceInfo[1], times);
        };
    }
}
