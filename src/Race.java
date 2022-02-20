import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

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

    private static List<Integer> parseTop(List<String> lines) {
        return lines.stream()
            .skip(1)
            .limit(4)
            .map(line -> Integer.parseInt(line.split(", ")[0]))
            .toList();
    }

    private static Map<Integer, Time> parseTime(List<String> lines, Map<Integer, Time> startTimes) {
        return lines.stream()
            .skip(1)
            .takeWhile(str -> Character.isDigit(str.charAt(0)))
            .collect(HashMap<Integer, Time>::new, (map, item) -> {
                var fields = item.split(", ");
                var key = Integer.parseInt(fields[0]);
                map.put(key, Time.diff(startTimes.get(key), Time.parse(fields[1])));
            }, (a, b) -> {});
    }

    public static Race parse(List<String> lines, Map<Integer, Time> startTimes) {
        var headerFields = lines.get(0).split(": ");
        var raceKind = headerFields[0];
        var raceInfo = headerFields[1].split(" - ");
        var top = parseTop(lines);
        var times = parseTime(lines, startTimes);

        return switch (raceKind) {
            case "Meta volante" -> new BonusSprint(raceInfo[0], raceInfo[1], times, top);
            case "Puerto" -> new Climb(raceInfo[0], raceInfo[1], Integer.parseInt(raceInfo[2].split(" ")[0]), times, top);
            default -> new LastRace(raceInfo[0], raceInfo[1], times, top);
        };
    }

    public String headerString() {
        return switch (this) {
            case Climb c -> String.format("| Puerto: %-44s |\n| %9d Categoria %19s | %-10s |\n",
                destination, c.category(), "", distance);
            default -> String.format("| Meta volante: %-25s | %-10s |\n", destination, distance);
        };
    }

    public String stringifyTop(Map<Integer, Participant> participants) {
        return IntStream.range(0, 3).boxed()
            .map(i -> String.format("| %d. %-29s %s %d puntos |\n", 
                i+1, participants.get(top.get(i)), times.get(top.get(i)), points()[i]))
            .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
            .toString();
    }

    public String stringifyWithdrawals(List<Integer> withdrawals, Map<Integer, Participant> participants) {
        if (withdrawals.size() == 0)
            return "";

        return Utils.blank
            + String.format("| Abandonos: %41s |\n", "")
            + withdrawals.stream()
                .map(code -> String.format("| %-52s |\n", participants.get(code)))
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    public String toString(Map<Integer, Participant> participants, Rankings ranking) {
        return headerString()
            + Utils.separator
            + String.format("| Puntuación: %40s |\n", "")
            + stringifyTop(participants)
            + Utils.blank
            + String.format("| Clasificación: %37s |\n", "")
            + ranking.toString(this, participants)
            + Utils.separator;
    }
}
