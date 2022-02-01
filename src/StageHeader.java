public class StageHeader {
    public final Integer code;
    public final String  rec;
    public final String  distance;

    public StageHeader(Integer code, String rec, String distance) {
        this.code = code;
        this.rec  = rec;
        this.distance = distance;
    }

    public static StageHeader parse(String str) {
        var fields = str.split(" - ");
        return new StageHeader(
            Integer.parseInt(fields[0].split(" ")[1]), 
            fields[1], 
            fields[2]);
    }
}
