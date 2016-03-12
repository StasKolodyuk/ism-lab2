package by.bsu.kolodyuk.generator;

import by.bsu.kolodyuk.generator.impl.A5Generator;
import by.bsu.kolodyuk.generator.impl.LFSRGenerator;
import by.bsu.kolodyuk.util.LFSRUtil;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.apache.commons.math3.random.RandomGenerator;
import org.junit.Test;

import java.util.Arrays;
import java.util.stream.IntStream;

import static java.lang.Math.pow;
import static java.util.Arrays.asList;
import static java.util.Arrays.copyOfRange;
import static org.apache.commons.lang3.ArrayUtils.toObject;
import static org.junit.Assert.assertTrue;
import static ucar.unidata.util.SpecialMathFunction.igamc;

public class LinearComplexityTest {

    private static final double [] pi = {0.010417, 0.031250, 0.125000, 0.500000, 0.250000, 0.062500, 0.020833};

    @Test
    public void linearComplexityExponent() {
        linearComplexity(Exponent.INSTANCE.getBits(), 1000);
    }

    @Test
    public void linearComplexityLFSR() {
        LFSRGenerator generator = new LFSRGenerator(32, 31, 30, 28, 26, 1);
        linearComplexity(generator.nextBit(1000000), 1000);
    }

    @Test
    public void linearComplexityA5() {
        A5Generator generator = new A5Generator();
        linearComplexity(generator.nextBit(1000000), 1000);
    }

    @Test
    public void linearComplexityFirstCongruential() {
        RandomGenerator generator = new LinearCongruentialGenerator(4, 7, 3, 11);
        linearComplexity(new UniformIntegerDistribution(generator, 0, 1).sample(1000000), 1000);
    }

    @Test
    public void linearComplexitySecondCongruential() {
        RandomGenerator generator = new LinearCongruentialGenerator();
        linearComplexity(new UniformIntegerDistribution(generator, 0, 1).sample(1000000), 1000);
    }

    @Test
    public void linearComplexityFirstMaclarenMarsaglia() {
        RandomGenerator first = new LinearCongruentialGenerator(4, 7, 3, 11);
        RandomGenerator second = new LinearCongruentialGenerator();
        RandomGenerator generator = new MacLarenMarsagliaGenerator(first, second, 10);
        linearComplexity(new UniformIntegerDistribution(generator, 0, 1).sample(1000000), 1000);
    }

    @Test
    public void linearComplexitySecondMaclarenMarsaglia() {
        RandomGenerator first = new LinearCongruentialGenerator(4, 7, 3, 11);
        RandomGenerator second = new LinearCongruentialGenerator();
        RandomGenerator generator = new MacLarenMarsagliaGenerator(second, first, 10);
        linearComplexity(new UniformIntegerDistribution(generator, 0, 1).sample(1000000), 1000);
    }


    public void linearComplexity(int[] bits, int m) {
        int n = bits.length / m;
        int k = 6;

        int[] v = new int[k+1];
        Arrays.fill(v, 0);

        for(int i = 0; i < n; i++) {
            int length = LFSRUtil.minLength(copyOfRange(bits, i * m, (i + 1) * m));
            double mean = mean(m);
            double t = t(length, mean, m);

            if(t <= -2.5) {
                v[0]++;
            } else if (t > -2.5 && t <= -1.5) {
                v[1]++;
            } else if (t > -1.5 && t <= -0.5) {
                v[2]++;
            } else if (t > -0.5 && t <= 0.5) {
                v[3]++;
            } else if (t > 0.5 && t <= 1.5) {
                v[4]++;
            } else if (t > 1.5 && t <= 2.5) {
                v[5]++;
            } else if (t > 2.5) {
                v[6]++;
            }
        }

        System.out.println(asList(toObject(v)));
        System.out.println(pearson(v, n));

        double p = igamc(k/2, pearson(v, n)/2);

        System.out.println(p);

        assertTrue(p >= 0.01);
    }


    private static double mean(int m) {
        return m/2 + (9 + pow(-1, m + 1))/36 - (m/3 + 2/9)/pow(2, m);
    }

    private static double t(int length, double mean, int m) {
        return pow(-1, m)*(length - mean) + 2/9;
    }

    private static double pearson(int[] v, int n) {
        return IntStream.range(0, v.length).mapToDouble(i -> pow(v[i] - n * pi[i], 2) / (n * pi[i])).sum();
    }
}
