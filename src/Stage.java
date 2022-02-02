import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public record Stage(StageHeader header, LocalTime startTime, List<Race> races) {
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

        var startTime = LocalTime.parse(chunks.get(0).get(1).split(", ")[1]);
        var races = chunks.stream()
            .skip(1)
            .map(Race::parse)
            .collect(Collectors.toList());

        return new Stage(header, startTime, races);
    }
}
