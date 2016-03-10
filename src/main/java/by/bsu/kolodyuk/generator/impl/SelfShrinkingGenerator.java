package by.bsu.kolodyuk.generator.impl;

import by.bsu.kolodyuk.generator.BinaryGenerator;

public class SelfShrinkingGenerator implements BinaryGenerator {

    private BinaryGenerator binaryGenerator = new LFSRGenerator(32, 31, 30, 28, 26, 1);

    @Override
    public int nextBit() {
        int first = binaryGenerator.nextBit();
        int second = binaryGenerator.nextBit();

        if(first == 1 && second == 0) {
            return 0;
        } else if(first == 1 && second == 1) {
            return 1;
        } else {
            return nextBit();
        }
    }
}
