import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

public class RankingsTest {
    @Test
    public void initialize() {
        var participants = new ArrayList<Participant>() {{
            add(new Participant(10, "x", "(ESP)", "a"));
            add(new Participant(12, "a", "(ESP)", "a"));
        }};

        var expected = new Rankings(
            new HashMap<Integer, Integer>() {{
                put(10, 0);
                put(12, 0);
            }},
            new HashMap<Integer, Integer>() {{
                put(10, 0);
                put(12, 0);
            }}
        );

        var actual = Rankings.initialize(participants);

        Assert.assertEquals(2, actual.bonusSprint().size());
        Assert.assertEquals(2, actual.climb().size());
        Assert.assertEquals(expected.climb().get(10), actual.climb().get(10));
    }    
}