import org.junit.Assert;
import org.junit.Test;

public class StageHeaderTest {
    @Test    
    public void parsesStage() {
        var str = "Etapa 2 - X / Y - 200 km";
        var expected = new StageHeader(2, "X / Y", "200 km");

        var header = StageHeader.parse(str);

        Assert.assertEquals(expected.code(), header.code());
        Assert.assertEquals(expected.tour(), header.tour());
        Assert.assertEquals(expected.distance(), header.distance());
    }
}
