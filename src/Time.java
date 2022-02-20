public record Time(int hours, int minutes, int seconds) {
    public int toSeconds() {
        return hours * 3600 + minutes * 60 + seconds;
    }

    public static Time fromSeconds(int seconds) {
        return new Time(seconds / 3600, (seconds / 60) % 60, seconds % 60);
    }

    public static Time diff(Time t1, Time t2) {
        return Time.fromSeconds(Math.abs(t1.toSeconds() - t2.toSeconds()));
    }

    public static Time sum(Time t1, Time t2) {
        return Time.fromSeconds(t1.toSeconds() + t2.toSeconds());
    }

    public static Time parse(String time) {
        var fields = time.split(":");
        return new Time(
            Integer.parseInt(fields[0]), 
            Integer.parseInt(fields[1]), 
            Integer.parseInt(fields[2]));
    }

    @Override
    public String toString() {
        return String.format("%2d:%02d:%02d", hours, minutes, seconds);
    }
}
