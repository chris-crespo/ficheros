import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class App {
    private static Optional<File> getFolder() {
        var fileChooser = new JFileChooser();

        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        return switch (fileChooser.showOpenDialog(null)) {
            case JFileChooser.APPROVE_OPTION -> Optional.of(fileChooser.getSelectedFile());
            default -> Optional.empty();
        };
    }

    private static List<String> readFile(String filename) {
        System.out.println(Path.of(filename));
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

    private static Map<Integer, Participant> readParticipants(String filename) {
        return readFile(filename).stream()
            .map(Participant::parse)
            .collect(Collectors.toMap(p -> p.code(), Function.identity()));
    }

    private static Stage readStage(String filename) {
        var fileContent = readFile(filename);
        return Stage.parse(fileContent);
    }

    private static void generateStage(Stage stage, Map<Integer, Participant> participants, List<Rankings> rankings) {
        try {
            Files.writeString(Path.of("x.txt"), stage.toString(participants, rankings));
        } catch (IOException e) {}
        //Files.write(Path.of("Fichero Resultado Etapa 1.txt"), stage.toStringWith(rankings, withdrawals));
    }

    private static List<Rankings> generateRankings(Stage stage, Map<Integer, Participant> participants, Rankings prevRankings) {
        return stage.races().stream()
            .collect(() -> new ArrayList<Rankings>() {{ add(prevRankings); }},
                (list, race) -> {
                    var last = list.get(list.size() - 1);
                    var next = Rankings.generateNext(last, race);

                    list.add(next);
                },
                (a, b) -> {})
            .subList(1, stage.races().size());
    }

    private static void writeStage(String path, String contents) {
        try {
            Files.writeString(Path.of(path), contents);
        }
        catch (IOException e) {
            System.out.println("xxxxxxxxxxxxxxxx");
        }
    }

    private static <T> Map<Integer, T> filterWithdrawals(Race lastRace, Map<Integer, T> map) {
        return map.entrySet().stream()
            .filter(entry -> lastRace.times().containsKey(entry.getKey()))
            .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
    }

    public static void main(String[] args) throws Exception {
        getFolder().ifPresent(folder -> {
            var participants = readParticipants(folder + "/Fichero Participantes.txt");
            var stageNumber = 4;
            var prevRankings = Rankings.initialize(participants.keySet());

            for (int i = 0; i < stageNumber; i++) {
                var stagePath = String.format("%s/Fichero Etapa %d.txt", folder, i+1);
                var stage = readStage(stagePath);
                var rankings = generateRankings(stage, participants, prevRankings);

                var resultPath = String.format("%s/Resultado Etapa %d.txt", folder, i+1);
                writeStage(resultPath, stage.toString(participants, rankings));

                var lastRace = Utils.last(stage.races());
                participants = filterWithdrawals(lastRace, participants);

                var lastRanking = Utils.last(rankings);
                prevRankings = new Rankings(
                    filterWithdrawals(lastRace, lastRanking.bonusSprint()),
                    filterWithdrawals(lastRace, lastRanking.climb())
                );
            }
        });
        /*
        var participants = readParticipants("Fichero Participantes.txt");
        var stage = readStage("Ejercicio Fichero Texto/Fichero Etapa 1.txt");
        System.out.println("Stage => Code: " + stage.header().code() + " Start: " + stage.startTime());
        stage.races().forEach(race -> {
            System.out.println("Race => " + race.destination() + " " + race.distance());
            race.times().entrySet()
                .forEach(entry -> System.out.println("Time => " + entry.getKey() + " " + entry.getValue()));
        });

        var rankings = stage.races().stream()
            .collect(
                () -> new ArrayList<Rankings>() {{ 
                    add(Rankings.initialize(participants.keySet())); 
                }},
                (list, race) -> {
                    var last = list.get(list.size() - 1);
                    var next = Rankings.generateNext(last, race);

                    list.add(next);
                },
                (a, b) -> {}
            )
            .subList(1, stage.races().size());

        rankings.forEach(System.out::println);
        generateStage(stage, participants, rankings);
        */
    }

}
