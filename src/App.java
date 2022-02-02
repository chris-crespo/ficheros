import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class App {
    private static List<String> readFile(String filename) {
        try {
            return Files.lines(Path.of(filename))
                .filter(line -> !line.strip().isEmpty())
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

    public static void main(String[] args) throws Exception {
        readParticipants("Fichero Participantes.txt");
        var stage = readStage("Ejercicio Fichero Texto/Fichero Etapa 1.txt");
        stage.races().forEach(race -> {
            System.out.println("Race => " + race.destination() + " " + race.distance());
            race.times().forEach(time -> System.out.println("Time => " + time));
        });
    }
}
