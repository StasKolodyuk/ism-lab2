package by.bsu.kolodyuk.generator;

import by.bsu.kolodyuk.generator.impl.A5Generator;
import by.bsu.kolodyuk.generator.impl.LFSRGenerator;

import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {
        A5Generator a5Generator = new A5Generator();
        LFSRGenerator firstGenerator = new LFSRGenerator(19, 18, 17, 14, 1);
        LFSRGenerator secondGenerator = new LFSRGenerator(22, 21, 1);
        LFSRGenerator thirdGenerator = new LFSRGenerator(23, 22, 21, 8, 1);
        Stream.generate(a5Generator::nextBit).limit(32).forEach(System.out::println);
    }
}
