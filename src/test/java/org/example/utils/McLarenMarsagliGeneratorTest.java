package org.example.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
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

        StringBuilder bytes = new StringBuilder();

        for (int i = 0; i < 31250; ++i) {
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

    private int runsNumber(String runs) {
        int result = 1;
        for (int i = 1; i < runs.length(); ++i) {
            if (runs.charAt(i) != runs.charAt(i - 1)) ++result;
        }
        return result;
    }
}