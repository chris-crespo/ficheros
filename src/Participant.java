
public class Participant {
    public final Integer code;
    public final String name;
    public final String countryCode;
    public final String x; // TODO: rename this

    public Participant(Integer code, String name, String countryCode, String x) {
        this.code = code;
        this.name = name;
        this.countryCode = countryCode;
        this.x = x;
    }

    public static Participant parse(String str) {
        var fields = str.split(", ");
        
        return new Participant(
            Integer.parseInt(fields[0]), 
            fields[1], 
            fields[2].substring(1, fields[2].length() - 1), 
            fields[3]);
    }

    @Override
    public String toString() {
        return String.format(
            "{ code: %d, name: %s, countryCode: %s, x: %s }", 
            code, name, countryCode, x);
    }
}
