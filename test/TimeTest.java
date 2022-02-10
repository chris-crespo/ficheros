import org.junit.Assert;
import org.junit.Test;

public class TimeTest {
    @Test
    public void calculatesDiffs() {
        var a = new Time(10, 20, 30);
        var b = new Time(10, 20, 31);
        var expected = new Time(0, 0, 1);

        var actual = Time.diff(a, b);

        Assert.assertEquals(expected, actual);
    }
}
