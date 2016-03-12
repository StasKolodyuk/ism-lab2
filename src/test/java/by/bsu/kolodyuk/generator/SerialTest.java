package by.bsu.kolodyuk.generator;

import by.bsu.kolodyuk.generator.impl.A5Generator;
import by.bsu.kolodyuk.generator.impl.LFSRGenerator;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.apache.commons.math3.random.RandomGenerator;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static by.bsu.kolodyuk.util.BitsUtil.generateBinaryArraysOfLength;
import static java.lang.Math.pow;
import static java.util.Arrays.asList;
import static java.util.Collections.indexOfSubList;
import static junit.framework.Assert.assertTrue;
import static org.apache.commons.lang3.ArrayUtils.*;
import static ucar.unidata.util.SpecialMathFunction.igamc;

public class SerialTest {

    @Test
    public void serialExponent() {
        serialTest(Exponent.INSTANCE.getBits(), 2);
    }

    @Test
    public void serialLFSR() {
        LFSRGenerator lfsrGenerator = new LFSRGenerator(32, 31, 30, 28, 26, 1);
        serialTest(lfsrGenerator.nextBit(1000000), 2);
    }

    @Test
    public void serialA5() {
        A5Generator generator = new A5Generator();
        serialTest(generator.nextBit(1000000), 2);
    }

    @Test
    public void serialFirstLinearCongruential() {
        RandomGenerator generator = new LinearCongruentialGenerator(4, 7, 3, 11);
        serialTest(new UniformIntegerDistribution(generator, 0, 1).sample(1000000), 2);
    }

    @Test
    public void serialSecondLinearCongruential() {
        RandomGenerator generator = new LinearCongruentialGenerator();
        serialTest(new UniformIntegerDistribution(generator, 0, 1).sample(1000000), 2);
    }

    @Test
    public void serialFirstMaclarenMarsaglia() {
        RandomGenerator first = new LinearCongruentialGenerator(4, 7, 3, 11);
        RandomGenerator second = new LinearCongruentialGenerator();
        RandomGenerator generator = new MacLarenMarsagliaGenerator(first, second, 10);
        serialTest(new UniformIntegerDistribution(generator, 0, 1).sample(1000000), 2);
    }

    @Test
    public void serialSecondMaclarenMarsaglia() {
        RandomGenerator first = new LinearCongruentialGenerator(4, 7, 3, 11);
        RandomGenerator second = new LinearCongruentialGenerator();
        RandomGenerator generator = new MacLarenMarsagliaGenerator(second, first, 10);
        serialTest(new UniformIntegerDistribution(generator, 0, 1).sample(1000000), 2);
    }

    public void serialTest(int[] randomBits, int m) {
        int n = randomBits.length;

        int[] extended = addAll(randomBits, Arrays.copyOf(randomBits, m - 1));

        double first = m > 0 ? generateBinaryArraysOfLength(m).stream().map(i -> numberOfMatches(extended, i))
                                                                       .mapToDouble(i -> i * i)
                                                                       .sum() * pow(2, m)/n - n : 0;
        double second = m > 1 ? generateBinaryArraysOfLength(m-1).stream().map(i -> numberOfMatches(extended, i))
                                                                          .mapToDouble(i -> i * i)
                                                                          .sum() * pow(2, m - 1)/n - n : 0;
        double third = m > 2 ? generateBinaryArraysOfLength(m-2).stream().map(i -> numberOfMatches(extended, i))
                                                                         .mapToDouble(i -> i * i)
                                                                         .sum() * pow(2, m - 2)/n - n : 0;

        System.out.println(first);
        System.out.println(second);
        System.out.println(third);

        double firstDelta = first*first - second*second;
        double secondDelta = first*first - 2*second*second + third*third;

        System.out.println(firstDelta);
        System.out.println(secondDelta);

        double p1 = igamc(pow(2, m-2), firstDelta);
        double p2 = igamc(pow(2, m-3), secondDelta);

        System.out.println(p1);
        System.out.println(p2);

        assertTrue(p1 >= 0.1);
        assertTrue(p2 >= 0.1);
    }

    private static int numberOfMatches(int[] array, List<Integer> toMatch) {
        int count = 0;
        int globalIndex = 0;
        while(globalIndex <= array.length) {
            int index = indexOfSubList(asList(toObject(array)).subList(globalIndex, array.length), toMatch);
            if(index == -1) {
                break;
            }
            globalIndex += (index+1);
            count++;
        }

        System.out.println(toMatch + " "  + count);

        return count;
    }

    private Map<String, Integer> matchMap(int[] array, int matchLength) {
        Map<String, Integer> result = new HashMap<>();
        for(int i = 0; i < array.length - matchLength; i++) {
            String key = "";
            for(int j = 0; j < matchLength; j++) {
                key += array[i + j];
            }
            Integer value = result.get(key);
            result.put(key, value != null ? value++ : 1);
        }

        return result;
    }
}
