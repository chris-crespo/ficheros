import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record Stage(int code, LocalTime startTime, List<Race> races) {
    public static Stage parse(List<String> lines) {
        var header = StageHeader.parse(lines.get(0));
        // Nos saltamos la cabecera de la etapa y de la salida
        var rest = lines.stream().skip(2).collect(Collectors.toList()); 

        var start = LocalTime.parse(rest.get(0).split(", ")[1]);
        rest = rest.stream()
            .skip(1)
            .dropWhile(str -> Character.isDigit(str.charAt(0)))
            .collect(Collectors.toList());

        var races = new ArrayList<Race>();
    }
}
