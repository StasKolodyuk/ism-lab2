package by.bsu.kolodyuk.generator;

import by.bsu.kolodyuk.generator.impl.A5Generator;
import by.bsu.kolodyuk.generator.impl.LFSRGenerator;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.apache.commons.math3.random.RandomGenerator;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

import static by.bsu.kolodyuk.generator.MatchUtil.createMatchMap;
import static by.bsu.kolodyuk.generator.MatchUtil.createMatchString;
import static by.bsu.kolodyuk.util.BitsUtil.generateBinaryArraysOfLength;
import static java.lang.Math.log;
import static java.lang.Math.pow;
import static junit.framework.Assert.assertTrue;
import static org.apache.commons.lang3.ArrayUtils.addAll;
import static ucar.unidata.util.SpecialMathFunction.igamc;

public class ApproximateEntropyTest {

    @Test
    public void approximateEntropyExample() {
        int[] bits = {1, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0,
                      0, 1, 0, 0, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 1, 0, 0, 1, 1,
                      0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 1, 1, 0, 0, 0};
        approximateEntropy(bits, 2);
    }

    @Test
    public void serialLFSR() {
        LFSRGenerator lfsrGenerator = new LFSRGenerator(32, 31, 30, 28, 26, 1);
        approximateEntropy(lfsrGenerator.nextBit(100), 2);
    }

    @Test
    public void serialA5() {
        A5Generator generator = new A5Generator();
        approximateEntropy(generator.nextBit(100), 2);
    }

    @Test
    public void serialFirstLinearCongruential() {
        RandomGenerator generator = new LinearCongruentialGenerator(4, 7, 3, 11);
        approximateEntropy(new UniformIntegerDistribution(generator, 0, 1).sample(100), 2);
    }

    @Test
    public void serialSecondLinearCongruential() {
        RandomGenerator generator = new LinearCongruentialGenerator();
        approximateEntropy(new UniformIntegerDistribution(generator, 0, 1).sample(100), 2);
    }

    @Test
    public void serialFirstMaclarenMarsaglia() {
        RandomGenerator first = new LinearCongruentialGenerator(4, 7, 3, 11);
        RandomGenerator second = new LinearCongruentialGenerator();
        RandomGenerator generator = new MacLarenMarsagliaGenerator(first, second, 10);
        approximateEntropy(new UniformIntegerDistribution(generator, 0, 1).sample(100), 2);
    }

    @Test
    public void serialSecondMaclarenMarsaglia() {
        RandomGenerator first = new LinearCongruentialGenerator(4, 7, 3, 11);
        RandomGenerator second = new LinearCongruentialGenerator();
        RandomGenerator generator = new MacLarenMarsagliaGenerator(second, first, 10);
        approximateEntropy(new UniformIntegerDistribution(generator, 0, 1).sample(100), 2);
    }


    private void approximateEntropy(int[] randomBits, int m) {
        int n = randomBits.length;

        int[] extended = addAll(randomBits, Arrays.copyOf(randomBits, m - 1));

        double first = 0;
        if(m > 0) {
            Map<String, Integer> matchMap = createMatchMap(extended, m, m);
            first = generateBinaryArraysOfLength(m).stream().map(i -> matchMap.getOrDefault(createMatchString(i), 0))
                                                            .mapToDouble(i -> i != 0 ? ((double) i / n) * log((double) i / n) : 0)
                                                            .sum();
        }

        double second = 0;
        if(m + 1 > 0) {
            Map<String, Integer> matchMap = createMatchMap(extended, m + 1, m);
            second = generateBinaryArraysOfLength(m + 1).stream().map(i -> matchMap.getOrDefault(createMatchString(i), 0))
                                                                 .mapToDouble(i -> i != 0 ? ((double) i / n) * log((double) i / n) : 0)
                                                                 .sum();
        }

        double apEn = first - second;

        System.out.println(apEn);

        double x = 2 * n * (log(2) - apEn);

        System.out.println(x);

        double p = igamc(pow(2, m-1), x/2);

        System.out.println(p);

        assertTrue(p >= 0.01);
    }

}
