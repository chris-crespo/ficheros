public record Participant(int code, String name, String countryCode, String team){
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
            "{ code: %d, name: %s, countryCode: %s, team: %s }", 
            code, name, countryCode, team);
    }
}
