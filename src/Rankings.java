import java.util.HashMap;
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
        var map = new HashMap<Integer, Integer>(prev);

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

    private String toString(Map<Integer, Integer> ranking, Map<Integer, Participant> participants) {
        var set = ranking.entrySet();
        var sortedEntries = set.stream()
            .filter(entry -> entry.getValue() > 0)
            .sorted((e1, e2) -> e2.getValue() - e1.getValue())
            .toList();
            
        return IntStream.range(0, sortedEntries.size())
            .boxed()
            .map(i -> {
                var entryKey = sortedEntries.get(i).getKey();
                return String.format(
                    "| %d. %-40s %d puntos |\n", 
                    i+1, 
                    participants.get(entryKey).toStringWithTeam(),
                    ranking.get(entryKey));
            })
            .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
            .toString();
    }

    public String toString(Race race, Map<Integer, Participant> participants) {
        return switch (race) {
            case Climb c -> toString(climb, participants);
            default -> toString(bonusSprint, participants);
        };
    }

    public String stringifyRanking(Map<Integer, Integer> ranking, Map<Integer, Participant> participants) {
        var sortedEntries = ranking.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue() - e1.getValue())
            .takeWhile(e -> e.getValue() > 0)
            .toList();

        return IntStream.range(0, sortedEntries.size())
            .boxed()
            .map(i -> {
                var entry = sortedEntries.get(i);
                var participant = participants.get(entry.getKey());
                return String.format("| %2d. %-39s %d puntos |\n",
                    i+1, participant.toStringWithTeam(), entry.getValue());
            })
            .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
            .toString();
    }

    public String stringifyBoth(Map<Integer, Participant> participants) {
        return String.format("| METAS VOLANTES: %36s |\n", " ")
            + stringifyRanking(bonusSprint, participants)
            + Utils.separator
            + String.format("| MONTAÑA: %43s |\n", " ")
            + stringifyRanking(climb, participants);
    }
}
