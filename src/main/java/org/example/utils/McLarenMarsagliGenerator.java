package org.example.utils;

import java.util.ArrayList;
import java.util.List;

public class McLarenMarsagliGenerator {

    private final long n;
    private final long k;
    private final List<Long> T = new ArrayList<>();

    private LinearCongruentialGenerator generatorX;
    private LinearCongruentialGenerator generatorY;

    public McLarenMarsagliGenerator(long n, long k) {
        this.n = n;
        this.k = k;

        generatorX = new LinearCongruentialGenerator(7891L, 3751L, 367, n);
        generatorY = new LinearCongruentialGenerator(7931L, 1441L, 148, k);

        dropSettings();
    }

    public void dropSettings() {
        generatorX = new LinearCongruentialGenerator(7891L, 3751L, 367, n);
        generatorY = new LinearCongruentialGenerator(7931L, 1441L, 148, k);

        for (long i = 0; i < k; ++i) {
            T.add(generatorX.next());
        }

    }

    public int nextInt() {
        long s = generatorY.next();
        int z = (int)(long)T.get((int)s);
        T.set((int)s, generatorX.next());
        return z;
    }

    public int calculatePeriod() {

        int result = 0;
        int currentPeriod = 0;

        dropSettings();

        for (int i = 0; i < n * 2; ++i) {
            nextInt();
        }

        List<Integer> firstSequence = new ArrayList<>();
        List<Integer> secondSequence = new ArrayList<>();

        int nextInt = nextInt();
        firstSequence.add(nextInt);

        for (int i = 0; i < n * 2; ++i) {
            nextInt = nextInt();

            if (!firstSequence.get(0).equals(nextInt)) {
                firstSequence.add(nextInt);
                continue;
            }

            secondSequence.add(nextInt);
            ++currentPeriod;

            for (int j = 1; j < firstSequence.size(); ++j) {

                nextInt = nextInt();
                secondSequence.add(nextInt);

                if (!firstSequence.get(j).equals(nextInt)) {
                    if (result < currentPeriod) result = currentPeriod;
                    firstSequence.addAll(secondSequence);
                    secondSequence.clear();
                    currentPeriod = 0;
                    break;
                }
                ++currentPeriod;
            }
            if (!secondSequence.isEmpty()) {
                return currentPeriod;
            }
        }

        dropSettings();
        return result;
    }
}
