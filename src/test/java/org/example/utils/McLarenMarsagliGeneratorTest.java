package org.example.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class McLarenMarsagliGeneratorTest {

    // https://studopedia.ru/15_94516_test-seriy-runs-test.html
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
        //("E(R)=" + eR);

        BigInteger count0BI = BigInteger.valueOf(count0);
        BigInteger count1BI = BigInteger.valueOf(count1);
        BigInteger countBI = BigInteger.valueOf(count);

        BigInteger numerator = BigInteger.TWO.multiply(count0BI).multiply(count1BI).multiply(BigInteger.TWO.multiply(count0BI).multiply(count1BI).subtract(countBI));
        BigInteger denominator = countBI.pow(2).multiply(countBI.subtract(BigInteger.ONE));

        BigInteger sigmaBI = numerator.divide(denominator);

        double sigma = sigmaBI.doubleValue();
        //("Sigma=" + sigma);

        double runsNumber = runsNumber(bytes.toString());
        //("Runs number: " + runsNumber);

        // Доверительный интервал 95%
        double leftBorder = eR - 1.96 * sigma;
        double rightBorder = eR + 1.96 * sigma;

        //("Interval=[" + leftBorder + "; " + rightBorder + "]");

        Assertions.assertTrue(runsNumber > leftBorder && runsNumber < rightBorder);
    }

    // https://habr.com/ru/company/securitycode/blog/237695/
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

        Assertions.assertTrue(result < 0.01);
    }

    @Test
    void linearComplexityTest() {
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

        int M = 1000;

        int blocksCount = bytesStr.length()/M;

        List<String> blocks = new ArrayList<>();
        for (int i = 0; i < blocksCount; i += M) {
            blocks.add(bytesStr.substring(i, i + M));
        }

        // ==========================================
        List<Integer> Ls = new ArrayList<>();
        for (String block : blocks) {
            int[] blockAsInts = string2IntArray(block);
            Ls.add(linearComplexity(blockAsInts, 2));
        }

        double mu = M/2.0 + (9+Math.pow(-1.0, M+1.0))/36.0 + (M/3.0 + 2/9.0)/Math.pow(2.0, M);

        List<Double> Ts = new ArrayList<>();

        for (int i = 0; i < blocks.size(); ++i) {
            Double currentT = Math.pow(-1.0, M) * (Ls.get(i) - mu) + 2.0/9.0;
            Ts.add(currentT);
        }

        int v0, v1,v2,v3,v4,v5,v6;
        v0 = v1 = v2 = v3 = v4 = v5 = v6 = 0;

        for (Double currentT : Ts) {
            if (currentT <= -2.5)
                ++v0;
            else if (currentT > -2.5 && currentT <= -1.5)
                ++v1;
            else if (currentT > -1.5 && currentT <= -0.5)
                ++v2;
            else if (currentT > -0.5 && currentT <= 0.5)
                ++v3;
            else if (currentT > 0.5 && currentT <= 1.5)
                ++v4;
            else if (currentT > 1.5 && currentT <= 2.5)
                ++v5;
            else if (currentT > 2.5)
                ++v6;
        }

        List<Integer> Vs = Arrays.asList(v0, v1, v2, v3, v4, v5, v6);
        List<Double> PIs = Arrays.asList(0.010417, 0.03125, 0.125, 0.5, 0.25, 0.0625, 0.020833);

        double xiSqr = 0.0;
        for (int i = 0; i < Vs.size(); ++i) {
            xiSqr += Math.pow((Vs.get(i) - blocksCount * PIs.get(i)), 2)/(blocksCount * PIs.get(i));
        }

        double p = Utils.igamc(6/2.0, xiSqr/2.0);
        Assertions.assertTrue(p < 0.01);
    }

    private int[] string2IntArray(String str) {
        int[] result = new int[str.length()];
        for (int i = 0; i < str.length(); ++i) {
            result[i] = Integer.parseInt(String.valueOf(str.charAt(i)));
        }
        return result;
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


    private static void printNextSeq(int[] sourceSeq, int osn, int L, int[] lfsr, int count) {
        int[] registr = new int[L];
        for (int i = 0 ; i < L; ++i) {
            registr[i] = sourceSeq[lfsr.length-i-1];
        }

        for (int i = 0; i < count; ++i) {
            int next_s = 0;
            for (int j = 0; j < L; ++j) {
                next_s = (next_s + ((lfsr[j+1]*registr[j]) % osn)) % osn;
            }
            for (int j = L-1; j > 0; --j)
                registr[j] = registr[j-1];
            registr[0] = next_s;
            System.out.print(next_s+" ");
        }
    }

    public static int linearComplexity(int[] s, int osn) {
        int r=0,delta=0,L=0;
        int n = s.length;
        int[] B = new int[n];
        B[0] = 1;
        int[] Lambda = new int[n];
        Lambda[0] = 1;
        int[] T = new int[n];

        for (r = 1; r <= n; ++r) {
            delta = 0;
            for (int j = 0; j <= L; ++j) //вычисление delta
                delta = (delta + ((Lambda[j]*s[r-j-1]) % osn)) % osn;

            for (int i = n-1; i > 0; --i) //сдвиг
                B[i] = B[i-1];
            B[0] = 0;

            if (delta != 0) {
                for (int i = 0; i < n; ++i) { // T = Lambda + delta*B
                    T[i] = (Lambda[i] + (((osn-delta)*B[i]) % osn)) % osn;
                }
                if (2*L <= r - 1) {
                    for (int i = 0; i < n; ++i)  // B = delta*Lambda
                        B[i] = Lambda[i]*delta % osn;
                    L = r - L;
                }
                Lambda = Arrays.copyOf(T, n); //Lambda = T
            }
        }
        Map.Entry<int[], Integer> integerSimpleEntry = new AbstractMap.SimpleEntry<>(Lambda, L);
        return integerSimpleEntry.getValue();
    }


    public static String polyToStr(int[] P) {
        String res = "";
        for (int i = 0; i < P.length; ++i) {
            if (P[i] == 0)
                continue;
            if (res.length() != 0)
                res += " + ";
            if (i == 0)
                res += P[i];
            else
                res += P[i] + "x^" + i;
        }
        return res;
    }



}