package by.bsu.kolodyuk.util;


public class BitsUtil {

    private BitsUtil() {}

    public static int getBit(long number, int n) {
        return (int) ((number >> (n - 1)) & 1L);
    }

}
