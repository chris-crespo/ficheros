import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public record Stage(StageHeader header, Time startTime, List<Race> races) {
    private static <T> BiConsumer<List<List<T>>, T> splitWhen(Predicate<T> pred) {
        return (lists, line) -> {
            if (pred.test(line))
                lists.add(new ArrayList<>(List.of(line)));
            else
                lists.get(lists.size() - 1).add(line);
        };
    }

    private static boolean hasNoRaces(List<String> chunk) {
        return chunk.size() == 1;
    }

    private static List<List<String>> parseChunks(List<String> lines) {
        var chunks = lines.stream() // Agrupamos las lineas por carreras
            .skip(1)
            .collect(ArrayList<List<String>>::new,
                splitWhen(str -> Character.isAlphabetic(str.charAt(0))),
                (a, b) -> {});

        var secondLast = Utils.secondLast(chunks);
        if (hasNoRaces(secondLast)) // Por ejemplo, puerto y meta juntos
            Utils.last(chunks).stream().skip(1).forEach(secondLast::add);

        return chunks;
    }

    private static List<Race> parseRaces(List<List<String>> chunks, Time startTime) {
        return chunks.stream()
            .skip(1)
            .map(race -> Race.parse(race, startTime))
            .toList();
    }

    public static Stage parse(List<String> lines) {
        var header = StageHeader.parse(lines.get(0));
        var chunks = parseChunks(lines);
        var startTime = Time.parse(chunks.get(0).get(1).split(", ")[1]);
        var races = parseRaces(chunks, startTime);

        return new Stage(header, startTime, races);
    }

    private String stringifyRaces(Map<Integer, Participant> participants, List<Rankings> rankings) {
        return IntStream.range(0, races.size() - 1)
            .boxed()
            .map(i -> {
                var x = races.get(i).toString(participants, rankings.get(i));
                System.out.println(x);
                return x;
            })
            .reduce((acc, str) -> acc + str)
            .orElse("");
    }

    private String stringifyTop4(Map<Integer, Participant> participants, Race lastRace) {
        var sortedTimes = lastRace.times().entrySet().stream()
            .sorted(Comparator.comparing(e -> e.getValue().toSeconds()))
            .toList();

        return IntStream.range(0, 4)
            .boxed()
            .map(i -> {
                var entry = sortedTimes.get(i);
                var participant = participants.get(entry.getKey());
                return String.format("| %2d. %-36s %-11s |\n", 
                    i+1, participant.toStringWithCountry(), entry.getValue());
            })
            .reduce((acc, str) -> acc + str)
            .orElse("");
    }

    private String stringifyLast(Map<Integer, Participant> participants, Race lastRace) {
        var sortedTimes = lastRace.times().entrySet().stream()
            .sorted(Comparator.comparing(e -> e.getValue().toSeconds()))
            .toList();

        var last = Utils.last(sortedTimes);
        var participant = participants.get(last.getKey());

        return String.format("| %2d. %-36s %-11s |\n", 
            sortedTimes.size(), participant.toStringWithCountry(), last.getValue());
    }

    private String stringifyFinishTop(Map<Integer, Participant> participants, Race lastRace) {
        return stringifyTop4(participants, lastRace)
            + String.format("|  5.   .......... %35s |\n", "")
            + Utils.blank
            + stringifyLast(participants, lastRace);
    }

    private String stringifyWithdrawals(Map<Integer, Participant> participants, Race lastRace) {
        return participants.keySet().stream()
            .filter(key -> !lastRace.times().containsKey(key))
            .map(key -> String.format("|   %-50s |\n", participants.get(key)))
            .reduce((acc, str) -> acc + str)
            .orElse("");
    }

    private String stringifyFinishLine(Race lastRace, Rankings lastRanking, Map<Integer, Participant> participants) {
        return String.format("| META: %-33s | %-10s |\n", lastRace.destination(), lastRace.distance())
            + Utils.separator
            + stringifyFinishTop(participants, lastRace)
            + Utils.blank
            + String.format("| Abandonos: %41s |\n", "")
            + stringifyWithdrawals(participants, lastRace);
    }

    public String toString(Map<Integer, Participant> participants, List<Rankings> rankings) {
        return Utils.outer
            + header
            + Utils.separator
            + String.format("| Salida: %s horas %29s |\n", startTime, "")
            + Utils.separator
            + stringifyRaces(participants, rankings)
            + stringifyFinishLine(Utils.last(races), Utils.last(rankings), participants)
            + Utils.outer
            + "\n"
            + Utils.outer
            + String.format("| %s |\n", Utils.center("CLASIFICACIONES", 37))
            + Utils.separator
            + String.format("| GENERAL: %43s |\n", "")
            + stringifyFinishTop(participants, Utils.last(races))
            + Utils.separator
            + Utils.last(rankings).stringifyBoth(participants)
            + Utils.outer;
    }
}
