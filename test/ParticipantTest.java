import org.junit.Assert;
import org.junit.Test;

public class ParticipantTest {
    @Test
    public void parsesParticipant() {
        var expected = new Participant(10, "x", "ESP", "X");
        var input = "10, x, (ESP), X";

        var actual = Participant.parse(input);

        Assert.assertEquals(expected, actual);
    } 
}
