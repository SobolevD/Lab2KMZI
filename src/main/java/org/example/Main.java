package org.example;

import org.example.generators.McLarenMarsagliGenerator;
import org.example.tests.BinaryMatrixRankTest;
import org.example.tests.LinearComplexityTest;
import org.example.tests.RunsTest;
import org.example.utils.BinaryManager;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        McLarenMarsagliGenerator generator = new McLarenMarsagliGenerator(10, 5);

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 80; j++) {
                System.out.print(generator.nextInt() + " ");
            }
            System.out.println("");
        }
        System.out.println(generator.calculatePeriod());

        runForBinary();
    }

    private static void runForBinary() throws IOException {
        System.out.println("E and PI tests...");
        List<Boolean> bytes = BinaryManager.getBytes("src\\main\\resources\\binaries\\data.e");

        RunsTest.run(bytes);
        BinaryMatrixRankTest.run(bytes);
        LinearComplexityTest.run(bytes);

        bytes = BinaryManager.getBytes("src\\main\\resources\\binaries\\data.pi");

        RunsTest.run(bytes);
        BinaryMatrixRankTest.run(bytes);
        LinearComplexityTest.run(bytes);
        System.out.println("E and PI tests complete...");
    }
}
