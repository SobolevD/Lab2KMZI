package org.example.utils;

import org.example.generators.LinearCongruentialGenerator;
import org.example.generators.McLarenMarsagliGenerator;
import org.example.tests.BinaryMatrixRankTest;
import org.example.tests.LinearComplexityTest;
import org.example.tests.RunsTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class McLarenMarsagliGeneratorTest {

    @Test
    void runsTest() {
        double run = RunsTest.run(getSequenceForTest());
        Assertions.assertTrue(run > 0.01);
    }

    @Test
    void matrixTest() {
        double run = BinaryMatrixRankTest.run(getSequenceForTest());
        Assertions.assertTrue(run > 0.01);
    }

    @Test
    void linearTest() {
        double run = LinearComplexityTest.run(getSequenceForTest());
        Assertions.assertTrue(run > 0.01);
    }

    private static List<Boolean> getSequenceForTest() {
        LinearCongruentialGenerator generator1 = new LinearCongruentialGenerator(0, 3616, 9563, 125682);
        LinearCongruentialGenerator generator2 = new LinearCongruentialGenerator(15612, 7853, 1292, 54748);
        McLarenMarsagliGenerator generator = new McLarenMarsagliGenerator(125682, 127312, generator1, generator2);

        List<Integer> sequence = new ArrayList<>();
        for (int i = 0; i < 31250; ++i) {
            sequence.add(generator.nextInt());
        }
        return sequenceToBytes(sequence);
    }

    private static List<Boolean> sequenceToBytes(List<Integer> sequence) {
        List<Boolean> result = new ArrayList<>();

        for (Integer num : sequence) {
            result.addAll(int2Bytes(num));
        }
        return result;
    }

    private static List<Boolean> int2Bytes(int num) {
        List<Boolean> result = new ArrayList<>();
        String s = Long.toBinaryString(num);

        StringBuilder resultBuilder = new StringBuilder();
        for (int i = 0; i < 16 - s.length(); ++i) {
            resultBuilder.append(0);
        }
        resultBuilder.append(s);
        String resultStr = resultBuilder.toString();
        for (int i = 0; i < resultStr.length(); ++i) {
            if (resultStr.charAt(i) == '0')
                result.add(false);
            if (resultStr.charAt(i) == '1')
                result.add(true);
        }
        return result;
    }
}