import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Race {
    private final String destination;
    private final String distance;
    private final Map<Integer, Time> times;
    private final List<Integer> top;

    public Race(String destination, String distance, Map<Integer, Time> times, List<Integer> top) {
        this.destination = destination;
        this.distance    = distance;
        this.times       = times;
        this.top         = top;
    }

    public String destination() { return destination; }
    public String distance() { return distance; }
    public Map<Integer, Time> times() { return times; }
    public List<Integer> top() { return top; }

    public abstract int[] points(); 

    public static Race parse(List<String> lines, Time startTime) {
        var headerFields = lines.get(0).split(": ");
        var raceKind = headerFields[0];
        var raceInfo = headerFields[1].split(" - ");

        var top = lines.stream()
            .skip(1)
            .limit(4)
            .map(line -> {
                var fields = line.split(", ");
                return Integer.parseInt(fields[0]);
            })
            .toList();

        var times = lines.stream()
            .skip(1)
            .takeWhile(str -> Character.isDigit(str.charAt(0)))
            .collect(HashMap<Integer, Time>::new, (map, item) -> {
                var fields = item.split(", ");
                map.put(
                    Integer.parseInt(fields[0]), 
                    Time.diff(startTime, Time.parse(fields[1])));
            }, (a, b) -> {});

        return switch (raceKind) {
            case "Meta volante" -> new BonusSprint(raceInfo[0], raceInfo[1], times, top);
            case "Puerto" -> new Climb(raceInfo[0], raceInfo[1], Integer.parseInt(raceInfo[2].split(" ")[0]), times, top);
            default -> new LastRace(raceInfo[0], raceInfo[1], times, top);
        };
    }

    public String toStringWith(Rankings ranking, List<Integer> withdrawals) {
        var formattedTop = top.stream()
            .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append);
        var formattedRanking = ranking.bonusSprint().entrySet().stream()
            .sorted((e1, e2) -> e1.getValue() - e2.getValue())
            .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
            .toString();

        return formattedRanking;
    }
}
