package org.example.generators;

import java.math.BigInteger;

// https://www.mathcelebrity.com/linear-congruential-generator-calculator.php
public class LinearCongruentialGenerator {

    private long x0;
    private final long a;
    private final long c;
    private final long N;

    public LinearCongruentialGenerator(long x0, long a, long c, long n) {

/*        if (a == 0){
            throw new RuntimeException("Parameter a could not be equal zero");
        }

        if (c == 0){
            throw new RuntimeException("Parameter c could not be equal zero");
        }

        if (gcd(c, n) != 1) {
            throw new RuntimeException("GCD of parameters c and N must be equals 1");
        }*/

        this.c = c;
        this.N = n;
        this.x0 = x0;
        this.a = a;

        //validateA();
    }

    public long next() {
        long result = (a * x0 + c) % N;
        x0 = result;
        return result;
    }

    private static int gcd(long a, long b) {
        BigInteger b1 = BigInteger.valueOf(a);
        BigInteger b2 = BigInteger.valueOf(b);
        BigInteger gcd = b1.gcd(b2);
        return gcd.intValue();
    }

    private void validateA() {
        long b = a - 1;
        BigInteger primeNum = BigInteger.ONE;
        while (primeNum.longValue() <= N) {
            primeNum = primeNum.nextProbablePrime();

            if (N % (double)primeNum.longValue() != 0)
                continue;

            if (b % primeNum.longValue() != 0) {
                throw new RuntimeException("a - 1 must be multiple of " + primeNum.longValue());
            }
        }

        if (N % 4 == 0 && b % 4 != 0) {
            throw new RuntimeException("N multiple of 4. a - 1 must be multiple of 4");
        }
    }
}
