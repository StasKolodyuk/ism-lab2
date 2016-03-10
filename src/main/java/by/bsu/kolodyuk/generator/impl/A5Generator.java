package by.bsu.kolodyuk.generator.impl;


import by.bsu.kolodyuk.generator.BinaryGenerator;

import static by.bsu.kolodyuk.util.BitsUtil.getBit;

public class A5Generator implements BinaryGenerator {

    private LFSRGenerator firstGenerator = new LFSRGenerator(19, 18, 17, 14, 1);
    private LFSRGenerator secondGenerator = new LFSRGenerator(22, 21, 1);
    private LFSRGenerator thirdGenerator = new LFSRGenerator(23, 22, 21, 8, 1);

    private int firstSyncBit = 9;
    private int secondSyncBit = 11;
    private int thirdSyncBit = 11;


    @Override
    public int nextBit() {
        int first = 0;
        int second = 0;
        int third = 0;
        int f = calculateF();

        if(f == getBit(firstGenerator.getRegister(), firstSyncBit)) {
            first = firstGenerator.nextBit();
        }

        if(f == getBit(secondGenerator.getRegister(), secondSyncBit)) {
            second = secondGenerator.nextBit();
        }

        if(f == getBit(thirdGenerator.getRegister(), thirdSyncBit)) {
            third = thirdGenerator.nextBit();
        }

        return first ^ second ^ third;
    }

    private int calculateF() {
        return (getBit(firstGenerator.getRegister(), firstSyncBit) & getBit(secondGenerator.getRegister(), secondSyncBit)) |
               (getBit(firstGenerator.getRegister(), firstSyncBit) & getBit(thirdGenerator.getRegister(), thirdSyncBit)) |
               (getBit(secondGenerator.getRegister(), secondSyncBit) & getBit(thirdGenerator.getRegister(), thirdSyncBit));
    }
}
