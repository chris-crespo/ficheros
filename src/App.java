import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class App {
    private static void callWithReader(String filename, Consumer<Scanner> proc) {
        var file = new File(filename);

        try {
            var reader = new Scanner(file);
            proc.accept(reader);
            reader.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("Could not open " + file.getName());
            System.exit(1);
        }
    }

    private static List<String> readFile(String filename) {
        var lines = new ArrayList<String>();
        callWithReader(filename, reader -> {
            while (reader.hasNextLine()) {
                var line = reader.nextLine();
                if (line.strip().isEmpty())
                    continue;

                lines.add(line);
            }
        });

        return lines;
    }

    private static List<Participant> readParticipants(String filename) {
        return readFile(filename).stream()
            .map(Participant::parse)
            .collect(Collectors.toList());
    }

    private static Stage readStage(String filename) {
        var fileContent = readFile(filename);
        var header = StageHeader.parse(fileContent.get(0));
        //var lines = parseLines(fileContent.stream().skip(1).collect(Collectors.toList()));
        return null;
    }

    public static void main(String[] args) throws Exception {

    }
}
