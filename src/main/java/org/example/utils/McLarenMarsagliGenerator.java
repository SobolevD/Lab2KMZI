package org.example.utils;

import java.util.ArrayList;
import java.util.List;

public class McLarenMarsagliGenerator {

    private final long n;
    private final long k;
    private long pos = 0;
    private final List<Long> T = new ArrayList<>();

    private final LinearCongruentialGenerator generatorX;
    private final LinearCongruentialGenerator generatorY;

    public McLarenMarsagliGenerator(long n, long k) {
        this.n = n;
        this.k = k;

        generatorX = new LinearCongruentialGenerator(7891L, 3751L, 367, n);
        generatorY = new LinearCongruentialGenerator(7931L, 1441L, 148, k);

        for (long i = 0; i < k; ++i) {
            T.add(generatorX.next());
        }
    }

    public int nextInt() {
        long s = generatorY.next();
        int z = (int)(long)T.get((int)s);
        T.set((int)s, k + pos);
        ++pos;
        return z;
    }
}
