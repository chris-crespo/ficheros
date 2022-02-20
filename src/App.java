import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        try {
            return Files.lines(Path.of(filename))
                .filter(line -> !line.isBlank())
                .collect(Collectors.toList());
        }
        catch (IOException e) {
            JOptionPane.showMessageDialog(null, "No se pudo leer el archivo: " + filename);
            System.exit(1);
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

    private static int askStage() {
        try {
            var input = JOptionPane.showInputDialog(null, "Introduce la etapa: ");
            if (input == null)
                System.exit(0);

            return Integer.parseInt(input);
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "La entrada no es correcta.");
            return askStage();
        }
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
            JOptionPane.showMessageDialog(null, "No se pudo escribir el archivo: " + path);
            System.exit(1);
        }
    }

    private static <T> Map<Integer, T> filterWithdrawals(Race lastRace, Map<Integer, T> map) {
        return map.entrySet().stream()
            .filter(entry -> lastRace.times().containsKey(entry.getKey()))
            .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
    }

    private static Function<Entry<Integer, Time>, Entry<Integer, Time>> accumulateTimes(Map<Integer, Time> times) {
        return e -> Map.entry(e.getKey(), Time.sum(e.getValue(), times.get(e.getKey())));
    }

    public static void main(String[] args) throws Exception {
        getFolder().ifPresent(folder -> {
            var participants = readParticipants(folder + "/Fichero Participantes.txt");
            var stageNumber  = askStage();
            var prevRankings = Rankings.initialize(participants.keySet());
            var times = participants.keySet().stream()
                .collect(Collectors.toMap(Function.identity(), x -> Time.fromSeconds(0)));

            for (int i = 0; i < stageNumber; i++) {
                var stagePath = String.format("%s/Fichero Etapa %d.txt", folder, i+1);
                var stage = readStage(stagePath);
                var rankings = stage.header().timed()
                    ? List.of(prevRankings)
                    : generateRankings(stage, participants, prevRankings);

                times = Utils.last(stage.races()).times().entrySet().stream()
                    .map(accumulateTimes(times))
                    .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

                var resultPath = String.format("%s/Resultado Etapa %d.txt", folder, i+1);
                writeStage(resultPath, stage.toString(participants, rankings, times));

                var lastRace = Utils.last(stage.races());
                var lastRanking = Utils.last(rankings);

                participants = filterWithdrawals(lastRace, participants);
                prevRankings = new Rankings(
                    filterWithdrawals(lastRace, lastRanking.bonusSprint()),
                    filterWithdrawals(lastRace, lastRanking.climb())
                );
            }
        });
    }
}
