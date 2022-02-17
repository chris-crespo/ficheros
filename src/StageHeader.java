public record StageHeader(int code, String tour, String distance) {
    public static StageHeader parse(String str) {
        var fields = str.split(" - ");
        return new StageHeader(
            Integer.parseInt(fields[0].split(" ")[1]), 
            fields[1], 
            fields[2]);
    }

    @Override
    public String toString() {
        return String.format("| Etapa %-2d |    %-22s    | %-10s |\n", code, tour, distance);
    }
}
