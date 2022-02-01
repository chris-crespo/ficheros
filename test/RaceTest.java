import java.time.LocalTime;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

public class RaceTest {
    @Test
    public void parsesBonusSprint() {
        var lines = new ArrayList<String>();
        lines.add("Meta volante: X - Km 20");
        lines.add("10, 10:40:20");
        lines.add("18, 10:40:20");
        lines.add("40, 10:40:20");

        var expectedLines = new ArrayList<Pair<Integer, LocalTime>>();
        var time = LocalTime.parse("10:40:20");
        expectedLines.add(new Pair<Integer, LocalTime>(10, time));
        expectedLines.add(new Pair<Integer, LocalTime>(18, time));
        expectedLines.add(new Pair<Integer, LocalTime>(40, time));
        var expected = new BonusSprint("X", "Km 20", expectedLines);

        var actual = Race.parse(lines);

        Assert.assertTrue(actual instanceof BonusSprint);
        Assert.assertEquals(expected.destination(), actual.destination());
        Assert.assertEquals(expected.distance(), actual.distance());
        Assert.assertEquals(expected.times().get(0), actual.times().get(0));
        Assert.assertEquals(expected.times().get(1), actual.times().get(1));
        Assert.assertEquals(expected.times().get(2), actual.times().get(2));
    } 
}
