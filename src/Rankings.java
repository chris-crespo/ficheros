import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public record Rankings(Map<Integer, Integer> bonusSprint, Map<Integer, Integer> climb) { 
    public static Rankings initialize(List<Participant> participants) {
        return new Rankings(
            participants.stream().collect(Collectors.toMap(Participant::code, x -> 0)), 
            participants.stream().collect(Collectors.toMap(Participant::code, x -> 0))
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
}
