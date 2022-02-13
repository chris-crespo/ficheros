import java.util.ArrayList;
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

    public static Stage parse(List<String> lines) {
        var header = StageHeader.parse(lines.get(0));
        var chunks = lines.stream() // Agrupamos las lineas por carreras
            .skip(1)
            .collect(
                ArrayList<List<String>>::new,
                splitWhen(str -> Character.isAlphabetic(str.charAt(0))),
                (a, b) -> {}
            );

        var startTime = Time.parse(chunks.get(0).get(1).split(", ")[1]);
        var races = chunks.stream()
            .skip(1)
            .map(race -> Race.parse(race, startTime))
            .toList();

        return new Stage(header, startTime, races);
    }

    public String toString(Map<Integer, Participant> participants, List<Rankings> rankings, List<List<Integer>> withdrawals) {
        return "----------------------------------------------------\n"
            + header
            + "|--------------------------------------------------|\n"
            + String.format("| Salida: %s horas %25s |\n", startTime, "")
            + "|--------------------------------------------------|\n"
            + IntStream.range(0, races.size())
                .boxed()
                .map(i -> races.get(i).toString(participants, rankings.get(i), withdrawals.get(i)))
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append);
    }
}
