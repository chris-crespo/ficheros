import java.util.List;

public class Utils {
    public static <T> T secondLast(List<T> list) {
        return list.get(list.size() - 2);
    }    

    public static <T> T last(List<T> list) {
        return list.get(list.size() - 1);
    }    

    public static boolean isOdd(int x) {
        return x % 2 != 0;
    }

    public static String center(String text, int width) {
        var half = width / 2;
        var format = String.format("%%%ds%%s%%%ds", 
            isOdd(width) ? half + 1 : half, width/2);
        return String.format(format, " ", text, " ");
    }

    public static String separator = "|--------------------------------------------------------------|\n";
    public static String outer = "----------------------------------------------------------------\n";
    public static String blank = String.format("| %60s |\n", "");
}
