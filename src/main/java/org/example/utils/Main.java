package org.example.utils;

public class Main {
    public static void main(String[] args) {
        McLarenMarsagliGenerator generator = new McLarenMarsagliGenerator(10, 5);

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 80; j++) {
                System.out.print(generator.nextInt() + " ");
            }
            System.out.println("");
        }
        System.out.println(generator.calculatePeriod());
    }
}
