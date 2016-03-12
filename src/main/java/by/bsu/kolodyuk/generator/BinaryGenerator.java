package by.bsu.kolodyuk.generator;

/**
 * Created by Aspire on 09.03.2016.
 */
public interface BinaryGenerator {

    int nextBit();

    default int[] nextBit(int n) {
        int[] result = new int[n];

        for(int i = 0; i < n; i++) {
            result[i] = nextBit();
        }

        return result;
    }

}
