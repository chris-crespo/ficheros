import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public record Rankings(Map<Integer, Integer> bonusSprint, Map<Integer, Integer> climb) { 
    public static Rankings initialize(Set<Integer> participants) {
        return new Rankings(
            participants.stream().collect(Collectors.toMap(Function.identity(), x -> 0)), 
            participants.stream().collect(Collectors.toMap(Function.identity(), x -> 0))
        );
    }

    public static Map<Integer, Integer> generateNextMap(Map<Integer, Integer> prev, Race race) {
        var participants = race.times().entrySet().stream()
            .map(entry -> entry.getKey())
            .collect(Collectors.toMap(Function.identity(), x -> 0));
        var map = new HashMap<Integer, Integer>(participants);

        IntStream.range(0, 4).forEach(i -> {
            map.replace(
                race.top().get(i), 
                prev.get(race.top().get(i)) + race.points()[i]);
        });
            
        return map;
    }

    public static Rankings generateNext(Rankings prev, Race race) {
        return switch (race) {
            case Climb climb -> new Rankings(
                prev.bonusSprint(),
                generateNextMap(prev.climb(), race)
            );
            case BonusSprint sprint -> new Rankings(
                generateNextMap(prev.bonusSprint(), race),
                prev.climb()
            );
            default -> prev;
        };
    }

    private String toString(Map<Integer, Integer> ranking) {
        var set = ranking.entrySet();
        var sortedEntries = set.stream()
            .filter(entry -> entry.getValue() > 0)
            .sorted((e1, e2) -> e2.getValue() - e1.getValue())
            .toList();
            
        return IntStream.range(0, sortedEntries.size())
            .boxed()
            .map(i -> String.format(
                "| %d. %d |\n", i+1, sortedEntries.get(i).getValue()))
            .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
            .toString();
    }

    public String toString(Race race, Map<Integer, Participant> participants) {
        return switch (race) {
            case Climb c -> toString(climb);
            default -> toString(bonusSprint);
        };
    }
}
