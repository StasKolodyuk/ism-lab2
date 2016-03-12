package by.bsu.kolodyuk.generator;

import by.bsu.kolodyuk.generator.impl.A5Generator;
import by.bsu.kolodyuk.generator.impl.LFSRGenerator;
import ucar.unidata.util.SpecialMathFunction;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args)  throws IOException{
        A5Generator a5Generator = new A5Generator();
        LFSRGenerator firstGenerator = new LFSRGenerator(19, 18, 17, 14, 1);
        LFSRGenerator secondGenerator = new LFSRGenerator(22, 21, 1);
        LFSRGenerator thirdGenerator = new LFSRGenerator(23, 22, 21, 8, 1);

        byte[] bytes = Files.readAllBytes(Paths.get("src/main/resources/input.txt"));
        byte[] codes = new byte[bytes.length];

        for(int i = 0; i < codes.length; i++) {
            codes[i] = createByte(a5Generator);
        }

        for(int i = 0; i < bytes.length; i++) {
            bytes[i] ^= codes[i];
        }

        Files.write(Paths.get("src/main/resources/output.txt"), bytes);
    }

    private static byte createByte(A5Generator a5Generator) {
        String str = "";
        for(int i = 0; i < 8; i++) {
            str += a5Generator.nextBit();
        }
        str += 'b';
        return Byte.parseByte(str);
    }
}
