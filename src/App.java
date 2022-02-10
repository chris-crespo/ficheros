import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class App {
    private static List<String> readFile(String filename) {
        try {
            return Files.lines(Path.of(filename))
                .filter(line -> !line.isBlank())
                .collect(Collectors.toList());
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    private static List<Participant> readParticipants(String filename) {
        return readFile(filename).stream()
            .map(Participant::parse)
            .collect(Collectors.toList());
    }

    private static Stage readStage(String filename) {
        var fileContent = readFile(filename);
        return Stage.parse(fileContent);
    }

    private static void generateStage(Stage stage, List<Rankings> rankings, List<List<Integer>> withdrawals) {
        System.out.println(stage.toStringWith(rankings, withdrawals));
        //Files.write(Path.of("Fichero Resultado Etapa 1.txt"), stage.toStringWith(rankings, withdrawals));
    }

    public static void main(String[] args) throws Exception {
        var participants = readParticipants("Fichero Participantes.txt");
        var stage = readStage("Ejercicio Fichero Texto/Fichero Etapa 1.txt");
        System.out.println("Stage => Code: " + stage.header().code() + " Start: " + stage.startTime());
        stage.races().forEach(race -> {
            System.out.println("Race => " + race.destination() + " " + race.distance());
            race.times().entrySet()
                .forEach(entry -> System.out.println("Time => " + entry.getKey() + " " + entry.getValue()));
        });

        var rankingsAndWithdrawals = stage.races().stream()
            .collect(
                () -> new ArrayList<Pair<Rankings, List<Integer>>>() {{ 
                    add(new Pair<>(Rankings.initialize(participants), new ArrayList<>())); 
                }},
                (lists, race) -> {
                    var prev = lists.get(lists.size() - 1);
                    var lastRanking = prev.first();
                    var nextRanking = Rankings.generateNext(lastRanking, race);
                    var withdrawals = lastRanking.bonusSprint().entrySet().stream()
                        .map(entry -> entry.getKey())
                        .filter(key -> !nextRanking.bonusSprint().containsKey(key))
                        .toList();

                    lists.add(new Pair<>(nextRanking, withdrawals));
                },
                (a, b) -> {}
            );

        var rankings = rankingsAndWithdrawals.stream()
                .map(p -> p.first())
                .toList();

        var withdrawals = rankingsAndWithdrawals.stream()
                .map(p -> p.second())
                .toList();

                generateStage(stage, rankings, withdrawals);
            /*
        var extraInfo = stage.races().stream()
            .collect(
                () -> new Pair<>(
                    new ArrayList<Rankings>() {{ add(Rankings.initialize(participants)); }},
                    new ArrayList<Participants>(),
                ),
                (lists, race) -> {
                    var last = list.get(list.size() - 1);
                    var lastRanking = last.first();
                }
            )
            */
    }
}
