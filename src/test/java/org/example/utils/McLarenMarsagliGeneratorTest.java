package org.example.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class McLarenMarsagliGeneratorTest {

    @Test
    void runsTest() {
        LinearCongruentialGenerator generator1 = new LinearCongruentialGenerator(0, 3616, 9563, 125682);
        LinearCongruentialGenerator generator2 = new LinearCongruentialGenerator(15612, 7853, 1292, 54748);
        McLarenMarsagliGenerator generator = new McLarenMarsagliGenerator(125682, 127312, generator1, generator2);

        int count0 = 0;
        int count1 = 0;

        int numbersCount = 31250; // Because 31250 * 32 = 1'000'000 and int size = 32 bits (4 bytes)

        StringBuilder bytes = new StringBuilder();

        for (int i = 0; i < numbersCount; ++i) {
            int num = generator.nextInt();
            StringBuilder numBinary = new StringBuilder(Integer.toBinaryString(num));

            int countForAddZeros = 32 - numBinary.length();
            if (numBinary.length() < 32)
                for (int j = 0; j < countForAddZeros; ++j) numBinary.insert(0, "0");

            Pattern pattern = Pattern.compile("0");
            Matcher matcher = pattern.matcher(numBinary.toString());

            int zerosCount = 0;
            while (matcher.find()) {
                ++zerosCount;
            }

            count0 += zerosCount;
            count1 += 32 - zerosCount;

            bytes.append(numBinary);
        }

        int count = count0 + count1;
        double eR = (2.0 * count0 * count1) / count + 1;
        //System.out.println("E(R)=" + eR);

        BigInteger count0BI = BigInteger.valueOf(count0);
        BigInteger count1BI = BigInteger.valueOf(count1);
        BigInteger countBI = BigInteger.valueOf(count);

        BigInteger numerator = BigInteger.TWO.multiply(count0BI).multiply(count1BI).multiply(BigInteger.TWO.multiply(count0BI).multiply(count1BI).subtract(countBI));
        BigInteger denominator = countBI.pow(2).multiply(countBI.subtract(BigInteger.ONE));

        BigInteger sigmaBI = numerator.divide(denominator);

        double sigma = sigmaBI.doubleValue();
        //System.out.println("Sigma=" + sigma);

        double runsNumber = runsNumber(bytes.toString());
        //System.out.println("Runs number: " + runsNumber);

        // Доверительный интервал 95%
        double leftBorder = eR - 1.96 * sigma;
        double rightBorder = eR + 1.96 * sigma;

        //System.out.println("Interval=[" + leftBorder + "; " + rightBorder + "]");

        Assertions.assertTrue(runsNumber > leftBorder && runsNumber < rightBorder);
    }

    @Test
    void binaryMatrixRankTest() {
        LinearCongruentialGenerator generator1 = new LinearCongruentialGenerator(0, 3616, 9563, 125682);
        LinearCongruentialGenerator generator2 = new LinearCongruentialGenerator(15612, 7853, 1292, 54748);
        McLarenMarsagliGenerator generator = new McLarenMarsagliGenerator(125682, 127312, generator1, generator2);

        int numbersCount = 31250; // Because 31250 * 32 = 1'000'000 and int size = 32 bits (4 bytes)

        StringBuilder bytes = new StringBuilder();

        for (int i = 0; i < numbersCount; ++i) {
            int num = generator.nextInt();
            StringBuilder numBinary = new StringBuilder(Integer.toBinaryString(num));

            int countForAddZeros = 32 - numBinary.length();
            if (numBinary.length() < 32)
                for (int j = 0; j < countForAddZeros; ++j) numBinary.insert(0, "0");

            bytes.append(numBinary);
        }

        String bytesStr = bytes.toString();

        int matrixSize = 32 * 32;

        int matricesCount = 1_000_000/matrixSize;

        List<List<List<Integer>>> matrices = new ArrayList<>();

        for (int i = 0; i < matricesCount; ++i) {
            int leftSequenceBorder = i * 32;
            int rightSequenceBorder = (i+1) * 32;
            matrices.add(sequenceToIntMatrix(bytesStr.substring(leftSequenceBorder, rightSequenceBorder)));
        }

        double pm = 0.2888;
        double pm1 = 0.5776;
        double pm2 = 0.1336;

        int matricesWithRank32 = 0;
        int matricesWithRank31 = 0;

        for (List<List<Integer>> matrix : matrices) {
            switch (Matrices.rankOfMatrix(matrix)) {
                case 32 -> {
                    ++matricesWithRank32;
                }
                case 31 -> {
                    ++matricesWithRank31;
                }
            }
        }

        double xi2 = (Math.pow(matricesWithRank32 - pm * matricesCount, 2)/(pm * matricesCount))
                + (Math.pow(matricesWithRank31 - pm1 * matricesCount, 2)/(pm1 * matricesCount))
                + (Math.pow(matricesCount - matricesWithRank32 - matricesWithRank31 - pm2 * matricesCount,2)/(pm2 * matricesCount));

        double result = Math.pow(Math.E, -xi2/2);

        Assertions.assertTrue(result > 0.01);
    }

    private int runsNumber(String runs) {
        int result = 1;
        for (int i = 1; i < runs.length(); ++i) {
            if (runs.charAt(i) != runs.charAt(i - 1)) ++result;
        }
        return result;
    }

    private List<List<Integer>> sequenceToIntMatrix(String sequence) {

        int dimSize = (int)Math.sqrt(sequence.length());
        List<List<Integer>> result = new ArrayList<>();
        for (int i = 0; i < dimSize; ++i) {
            List<Integer> row = new ArrayList<>();
            for (int j = 0; j < dimSize; ++j) {
                String symbol = Integer.valueOf(sequence.charAt(i * dimSize + j)).toString();
                row.add(Integer.valueOf(symbol));
            }
            result.add(row);
        }
        return result;
    }

}