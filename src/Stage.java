import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public record Stage(StageHeader header, Map<Integer, Time> startTimes, List<Race> races) {
    private static <T> BiConsumer<List<List<T>>, T> splitWhen(Predicate<T> pred) {
        return (lists, line) -> {
            if (pred.test(line))
                lists.add(new ArrayList<>(List.of(line)));
            else
                lists.get(lists.size() - 1).add(line);
        };
    }

    private static boolean hasNoTimes(List<String> chunk) {
        return chunk.size() == 1;
    }

    private static List<List<String>> parseChunks(List<String> lines) {
        var chunks = lines.stream() // Agrupamos las lineas por carreras
            .skip(1)
            .collect(ArrayList<List<String>>::new,
                splitWhen(str -> Character.isAlphabetic(str.charAt(0))),
                (a, b) -> {});

        var secondLast = Utils.secondLast(chunks);
        if (hasNoTimes(secondLast)) // Por ejemplo, puerto y meta juntos
            Utils.last(chunks).stream().skip(1).forEach(secondLast::add);

        return chunks;
    }

    private static Map<Integer, Time> parseStartTimes(List<String> chunk) {
        return chunk.stream()
            .skip(1) // Cabecera del bloque ('Salida')
            .map(str -> {
                var fields = str.split(", ");
                return Map.entry(Integer.parseInt(fields[0]), Time.parse(fields[1]));
            })
            .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
    }

    private static List<Race> parseRaces(List<List<String>> chunks, Map<Integer, Time> startTimes) {
        return chunks.stream()
            .skip(1)
            .map(race -> Race.parse(race, startTimes))
            .toList();
    }

    public static Stage parse(List<String> lines) {
        var header = StageHeader.parse(lines.get(0));
        var chunks = parseChunks(lines);
        var startTimes = parseStartTimes(chunks.get(0));
        var races = parseRaces(chunks, startTimes);

        return new Stage(header, startTimes, races);
    }

    private String stringifyRaces(Map<Integer, Participant> participants, List<Rankings> rankings) {
        return IntStream.range(0, races.size() - 1)
            .boxed()
            .map(i -> races.get(i).toString(participants, rankings.get(i)))
            .reduce((acc, str) -> acc + str)
            .orElse("");
    }

    private String stringifyTop4(Map<Integer, Participant> participants, Map<Integer, Time> endTimes) {
        var sortedTimes = endTimes.entrySet().stream()
            .sorted(Comparator.comparing(e -> e.getValue().toSeconds()))
            .toList();

        return IntStream.range(0, 4)
            .boxed()
            .map(i -> {
                var entry = sortedTimes.get(i);
                var participant = participants.get(entry.getKey());
                return String.format("| %2d. %-44s %-11s |\n", 
                    i+1, participant.toStringWithCountry(), endTimes.get(entry.getKey()));
            })
            .reduce((acc, str) -> acc + str)
            .orElse("");
    }

    private String stringifyLast(Map<Integer, Participant> participants, Map<Integer, Time> endTime) {
        var sortedTimes = endTime.entrySet().stream()
            .sorted(Comparator.comparing(e -> e.getValue().toSeconds()))
            .toList();

        var last = Utils.last(sortedTimes);
        var participant = participants.get(last.getKey());

        return String.format("| %2d. %-44s %-11s |\n", 
            sortedTimes.size(), participant.toStringWithCountry(), endTime.get(last.getKey()));
    }

    private String stringifyFinishTop(Map<Integer, Participant> participants, Map<Integer, Time> endTimes) {
        return stringifyTop4(participants, endTimes)
            + String.format("|  5.   .......... %43s |\n", "")
            + Utils.blank
            + stringifyLast(participants, endTimes);
    }

    private String stringifyWithdrawals(Map<Integer, Participant> participants, Race lastRace) {
        var withdrawals =  participants.keySet().stream()
            .filter(key -> !lastRace.times().containsKey(key))
            .map(key -> String.format("|   %-58s |\n", participants.get(key)))
            .reduce((acc, str) -> acc + str);

        return withdrawals.isPresent()
            ?  Utils.blank + String.format("| Abandonos: %49s |\n", "") + withdrawals.get()
            : "";
    }

    private String stringifyFinishLine(Race lastRace, Map<Integer, Participant> participants) {
        return String.format("| META: %-41s | %-10s |\n", lastRace.destination(), lastRace.distance())
            + Utils.separator
            + stringifyFinishTop(participants, lastRace.times())
            + stringifyWithdrawals(participants, lastRace);
    }

    public String toString(Map<Integer, Participant> participants, List<Rankings> rankings, Map<Integer, Time> endTimes) {
        return Utils.outer
            + header
            + Utils.separator
            + (header.timed()
                ? String.format("| %-60s |\n", "Contrareloj")
                : String.format("| Salida: %s horas %37s |\n", startTimes.values().iterator().next(), ""))
            + Utils.separator
            + stringifyRaces(participants, rankings)
            + stringifyFinishLine(Utils.last(races), participants)
            + Utils.outer
            + "\n"
            + Utils.outer
            + String.format("| %s |\n", Utils.center("CLASIFICACIONES", 45))
            + Utils.separator
            + String.format("| GENERAL: %51s |\n", "")
            + stringifyFinishTop(participants, endTimes)
            + Utils.separator
            + Utils.last(rankings).stringifyBoth(participants)
            + Utils.outer;
    }
}
