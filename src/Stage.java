import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Stage {
    public final Integer code;
    public final LocalTime startTime;
    public final List<Line> lines; 

    public Stage(Integer code, LocalTime startTime, List<Line> lines) {
        this.code = code;
        this.startTime = startTime;
        this.lines = lines;
    }

    public static Stage parse(List<String> lines) {
        var header = StageHeader.parse(lines.get(0));
        // Nos saltamos la cabecera de la etapa y de la salida
        var rest = lines.stream().skip(2).collect(Collectors.toList()); 

        var start = LocalTime.parse(rest.get(0).split(", ")[1]);
        rest = rest.stream()
            .skip(1)
            .dropWhile(str -> Character.isDigit(str.charAt(0)))
            .collect(Collectors.toList());

        rest.stream()
            .reduce(Pair::new, (Pair<List<List<String>>, List<String>> acc, String str) -> {
                if (Character.isAlphabetic(str.charAt(0))) {
                    acc.first
                }
            });

    }
}
