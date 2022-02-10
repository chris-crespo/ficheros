import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class StageTest {
    @Test
    public void parsesStage() {
        var lines = new ArrayList<>(List.of(
            "Etapa 5 - X / Y - 40 Km",
            "Salida ...: X - Km 0.0",
            "10, 04:00:20",
            "Meta volante: Zumaia - Km 20.2",
            "20, 06:10:20"));
        var expected = new Stage(
            new StageHeader(5, "X / Y", "40 Km"),
            Time.parse("04:00:20"),
            new ArrayList<>(List.of(
                new BonusSprint("Zumaia", "Km 50.2", Map.of(20, Time.parse("06:10:20")), new ArrayList<Integer>(){{
                    add(20);
                }}))));

        var actual = Stage.parse(lines);

        Assert.assertEquals(expected.header().code(), actual.header().code());
        Assert.assertEquals(expected.startTime(), actual.startTime());
        Assert.assertEquals(
            expected.races().get(0).destination(), 
            actual.races().get(0).destination());
    } 
}
